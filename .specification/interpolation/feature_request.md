# Feature: Generic Interpolation Framework

## 1. Overview

Athena's algebra module provides reusable mathematical structures (rings, fields, groups). This feature
extends the math package with a generic **interpolation framework** that:

- Is parameterized over abstract input space X and output space Y
- Requires only the algebraic/metric structure actually needed by each interpolation method
- Supports nearest-neighbor, linear, cubic, and monotone-cubic interpolation out of the box
- Works for any X that has a notion of "betweenness" (e.g., `LocalDate`, `Double`, `BigDecimal`)
- Works for any Y that supports weighted blending (e.g., `Double` implied volatility, custom vector types)
- Supports multi-dimensional grids where each dimension can use a **different interpolation method**

**Primary driver**: volatility surface interpolation over expiry dates (X₁) and strikes (X₂).

---

## 2. Glossary

| Term                               | Definition                                                                                               |
|------------------------------------|----------------------------------------------------------------------------------------------------------|
| **Sample**                         | A known data point `(x, y)` used to define the interpolation                                             |
| **Knot**                           | Synonym for sample                                                                                       |
| **Query point**                    | An `x` value for which an estimate of `y` is sought                                                      |
| **Parameterization**               | A function `(x₁, x₂, x) → λ ∈ [0,1]` expressing where `x` lies between `x₁` and `x₂`                     |
| **Blending**                       | Combining two or more Y values with scalar weights derived from parameterization                         |
| **Metric space**                   | A set with a distance function: non-negative, identity of indiscernibles, symmetric, triangle inequality |
| **Affine / parameterizable space** | A metric space with a consistent notion of parameterization between points                               |

---

## 3. Mathematical Foundations

### 3.1 Package: `math.analysis`

Metric and analysis spaces live in `de.gupta.commons.utility.math.analysis`. Metric spaces are a
concept of analysis (topology, continuity), not algebra — this mirrors the mathematical distinction
between `math.algebra` (rings, fields, groups) and `math.analysis` (distances, limits, smoothness).

### 3.2 Metric Space

The minimal structure. Sufficient for nearest-neighbor interpolation.

```
MetricSpace<X>:
  distance(X a, X b) → double    // ≥ 0, symmetric, triangle inequality, d(a,a)=0
```

### 3.3 Interpolatable Space

Extends `MetricSpace`. Required for linear interpolation. Determines where a query point `x`
lies between two bracketing points `x₁` and `x₂`.

```
InterpolatableSpace<X> extends MetricSpace<X>:
  parameter(X left, X right, X query) → double    // λ ∈ [0,1]: 0=left, 1=right
  compare(X a, X b) → int                         // total ordering — for bracket search
```

`parameter` defaults to `d(left, query) / d(left, right)` but can be overridden for
non-linear parameterizations such as log-space (see §8).

### 3.4 Differentiable Space

Extends `InterpolatableSpace`. Cubic spline needs interval lengths to set up its
tridiagonal system for second-derivative computation.

```
DifferentiableSpace<X> extends InterpolatableSpace<X>:
  span(X left, X right) → double    // unsigned scalar length of the interval
```

### 3.5 Module and Vector Space (output Y)

Modules and vector spaces are **algebraic** concepts and live in
`de.gupta.commons.utility.math.algebra.structure.module` — not in the analysis package.

```
ModuleStructure<V, R>
  extends AdditiveAbelianGroupStructure<V>     // vector addition, zero, negate

  RingStructure<R> scalars()                   // the ring of scalars; self-contained
  V scale(R scalar, V vector)

VectorSpaceStructure<V, F>
  extends ModuleStructure<V, F>

  FieldStructure<F> scalars()                  // covariant override: narrows to field
```

`ModuleStructure<V, R>` satisfies four axioms (verified by `ModuleStructureLaws`):

- `r·(v+w) == r·v + r·w`
- `(r+s)·v == r·v + s·v`
- `(r·s)·v == r·(s·v)`
- `1·v == v`

`VectorSpaceStructure<V, F>` additionally satisfies `r·(r⁻¹·v) == v` and `(-1)·v == -v`
(verified by `VectorSpaceStructureLaws`, which composes `ModuleStructureLaws` via `Stream.concat`).

Weighted blending used by interpolation methods is expressed entirely through the module:
`add(scale(1-λ, v), scale(λ, w))`. No separate blend abstraction is needed.

---

## 4. Core Data Abstractions

### 4.1 `Sample<X, Y>`

An immutable known data point.

```java
record Sample<X, Y>(X x, Y y)
{
}
```

### 4.2 `Dyad<Sample<X, Y>, Sample<X, Y>>` — bracket representation

The pair of adjacent samples that straddle a query point is represented as
`Dyad<Sample<X, Y>, Sample<X, Y>>` from Aletheia (`sinister` = left knot, `dexter` = right knot).
No bespoke `Bracket` record is needed — `Dyad` is a generic ordered pair already in the dependency.

### 4.3 `InterpolationData<X, Y>`

An ordered, immutable collection of samples sorted ascending by X. Provides O(log n) bracket
lookup via binary search using the comparator baked in at construction.

```java
interface InterpolationData<X, Y>
{
	List<Sample<X, Y>> samples();

	Optional<Dyad<Sample<X, Y>, Sample<X, Y>>> bracket(X query);
}
```

Factory: `InterpolationData.of(List<Sample<X,Y>>, Comparator<X>)` — sorts samples ascending;
comparator is fixed for the lifetime of the instance. Returns `Optional.empty()` only when fewer
than two samples exist (nearest-neighbor handles that case; all other methods require ≥ 2).

### 4.4 `Interpolator<X, Y>`

The result of fitting a method to data. A pure function from X to Y.

```java

@FunctionalInterface
interface Interpolator<X, Y>
{
	Y interpolate(X query);
}
```

### 4.5 `InterpolationMethod<X, Y>`

A strategy that produces an `Interpolator` from data. Primary extension point.

```java
interface InterpolationMethod<X, Y>
{
	Interpolator<X, Y> fit(InterpolationData<X, Y> data);
}
```

---

## 5. Policies

### 5.1 Extrapolation Policy

```java
enum ExtrapolationPolicy
{
	FLAT,       // clamp to the boundary sample value
	LINEAR,     // extend the outermost linear segment beyond the boundary
	FORBIDDEN   // throw IllegalArgumentException on out-of-range query
}
```

Default: `FLAT`. All built-in methods accept this as a constructor/builder parameter.

### 5.2 Tie-Breaking Policy

Used by nearest-neighbor interpolation when two samples are equidistant from the query.

```java
enum TieBreakingPolicy
{
	LOWER,      // prefer the left (lower-X) sample
	UPPER,      // prefer the right (upper-X) sample
	AVERAGE     // blend with λ=0.5 (requires ModuleStructure<Y, F>)
}
```

`LOWER` is the default. `AVERAGE` requires Y to have a `ModuleStructure` with a scalar `½`.

---

## 6. Built-in Interpolation Methods

### 6.1 Nearest Neighbor

- **Requires**: `MetricSpace<X>`. No constraint on Y.
- **Algorithm**: Returns the Y of the sample whose `distance` to the query is smallest.
- **Tie-breaking**: governed by `TieBreakingPolicy`.

### 6.2 Linear Interpolation

- **Requires**: `InterpolatableSpace<X>`, `ModuleStructure<Y, F>`
- **Algorithm**: Bracket search → λ = `parameter(x₁, x₂, x)` cast to F →
  `add(scale(1-λ, y₁), scale(λ, y₂))`
- **Fit cost**: O(n log n) sort once; O(log n) per query.

### 6.3 Natural Cubic Spline

- **Requires**: `DifferentiableSpace<X>`, `VectorSpaceStructure<Y, F>`
- **Algorithm**: Solves the tridiagonal system for second derivatives at each knot; evaluates a
  degree-3 polynomial within each interval. Boundary condition: zero second derivative (natural).
- **Fit cost**: O(n) coefficient solve; O(log n) per query.

### 6.4 Monotone Cubic (Fritsch–Carlson)

- **Requires**: `DifferentiableSpace<X>`, `VectorSpaceStructure<Y, F>`
- **Algorithm**: Shape-preserving cubic Hermite spline. Computes tangents with monotonicity
  correction to avoid spurious oscillations.
- **Preferred over natural spline for financial data** (no overshooting between knots).
- **Fit cost**: O(n); O(log n) per query.

---

## 7. Multi-Dimensional Interpolation

Per-dimension method configuration is a first-class requirement. For an N-dimensional grid each
dimension independently specifies its space descriptor and interpolation method.

### 7.1 Two-Dimensional: `InterpolationGrid<X1, X2, Y>`

```java
InterpolationGrid<LocalDate, Double, Double> surface =
		InterpolationGrid.<LocalDate, Double, Double>builder()
		                 .withData(gridData)   // Iterable<GridSample<X1, X2, Y>>
		                 .dimension1(LocalDateInterpolatableSpace.INSTANCE,
								 LinearInterpolationMethod.of(ExtrapolationPolicy.FLAT))
		                 .dimension2(DoubleInterpolatableSpace.of(ParameterizationMode.LOG),
								 MonotoneCubicMethod.of(ExtrapolationPolicy.FLAT))
		                 .build();

double vol = surface.interpolate(expiry, strike);
```

**Algorithm** (slice-and-query):

1. At build time: for each distinct X1 slice in the data, fit a `dim2Method.fit(sliceData)`
   producing an `Interpolator<X2, Y>` per slice.
2. At query time `(x1, x2)`: generate virtual samples `Sample<X1, Y>` by evaluating each stored
   slice interpolator at `x2`; then apply `dim1Method.fit(virtualData).interpolate(x1)`.

This keeps dim1 and dim2 fully decoupled — each uses its own space and method with no cross-dimension
coupling. Step 2 is O(n₁) per query (n₁ = number of distinct X1 slices, typically ≤ 30 for a vol
surface).

### 7.2 `GridSample<X1, X2, Y>`

```java
record GridSample<X1, X2, Y>(X1 x1, X2 x2, Y y)
{
}
```

### 7.3 Extensibility to Higher Dimensions

Three or more dimensions follow the same nesting pattern. A 3D case adds a `dimension3` parameter;
internally it nests `InterpolationGrid<X2, X3, Y>` slices in place of `Interpolator<X2, Y>`. This
is implemented generically rather than requiring new types per dimensionality (left for a later
iteration if needed).

---

## 8. Standard Space Implementations

All standard implementations live in `math.analysis.space.standard`.

### 8.1 Input Spaces

| Class                           | Type         | Implements                        | Notes                                                   |
|---------------------------------|--------------|-----------------------------------|---------------------------------------------------------|
| `DoubleInterpolatableSpace`     | `Double`     | `DifferentiableSpace<Double>`     | Configurable via `ParameterizationMode`                 |
| `BigDecimalInterpolatableSpace` | `BigDecimal` | `DifferentiableSpace<BigDecimal>` | Configurable via `ParameterizationMode`                 |
| `LocalDateInterpolatableSpace`  | `LocalDate`  | `InterpolatableSpace<LocalDate>`  | Calendar-day distance; `ParameterizationMode` supported |

### 8.2 `ParameterizationMode`

Applied by numeric spaces. Affects how `parameter(left, right, query)` is computed.

```java
enum ParameterizationMode
{
	LINEAR,   // λ = (x - x₁) / (x₂ - x₁)
	LOG       // λ = ln(x / x₁) / ln(x₂ / x₁)  — requires x > 0
}
```

Spaces are constructed with a mode: `DoubleInterpolatableSpace.of(ParameterizationMode.LOG)`.
A static constant `DoubleInterpolatableSpace.LINEAR` provides the default.

### 8.3 Output Spaces

| Class                            | Type         | Implements                                     |
|----------------------------------|--------------|------------------------------------------------|
| `DoubleVectorSpaceStructure`     | `Double`     | `VectorSpaceStructure<Double, Double>`         |
| `BigDecimalVectorSpaceStructure` | `BigDecimal` | `VectorSpaceStructure<BigDecimal, BigDecimal>` |

Both are singletons (static constants). The scalar field F equals V for these standard types
(a 1D vector space over itself).

---

## 9. Package Layout

```
de.gupta.commons.utility.math.algebra.structure.module
    ├── ModuleStructure.java                  (extends AdditiveAbelianGroupStructure<V>)
    └── VectorSpaceStructure.java             (extends ModuleStructure; scalars() narrows to FieldStructure)

de.gupta.commons.utility.math.analysis
└── space
    ├── MetricSpace.java                      (structure interface)
    ├── InterpolatableSpace.java              (extends MetricSpace + Comparator)
    ├── DifferentiableSpace.java              (extends InterpolatableSpace; default span())
    └── standard
        ├── DoubleInterpolatableSpace.java
        ├── BigDecimalInterpolatableSpace.java
        ├── LocalDateInterpolatableSpace.java
        ├── DoubleVectorSpaceStructure.java
        └── BigDecimalVectorSpaceStructure.java

de.gupta.commons.utility.math.interpolation
├── Interpolator.java                         (functional interface)
├── InterpolationMethod.java                  (strategy interface)
├── ExtrapolationPolicy.java                  (enum)
├── TieBreakingPolicy.java                    (enum)
├── ParameterizationMode.java                 (enum)
├── data
│   ├── Sample.java                           (record)
│   ├── GridSample.java                       (record, 2D)
│   └── InterpolationData.java                (interface + static factory)
├── method
│   ├── NearestNeighborMethod.java
│   ├── LinearInterpolationMethod.java
│   ├── NaturalCubicSplineMethod.java
│   └── MonotoneCubicMethod.java
└── grid
    └── InterpolationGrid.java                (2D, with builder)
```

---

## 10. Non-Goals

- Multidimensional kernel methods (RBF, kriging)
- Regression / curve fitting (does not pass through all knots)
- Interpolation on non-Euclidean manifolds

---

## 11. Implementation Plan

### Phase 1 — Space Abstractions *(complete)*

**Packages**: `math.analysis.space`, `math.algebra.structure.module`  
**Deliverables**: `MetricSpace`, `InterpolatableSpace`, `DifferentiableSpace` (analysis);
`ModuleStructure`, `VectorSpaceStructure` (algebra).  
**Test pattern**: Pluggable law verifier records (`MetricSpaceLaws`, `InterpolatableSpaceLaws`,
`DifferentiableSpaceLaws`, `ModuleStructureLaws`, `VectorSpaceStructureLaws`) used via
`@TestFactory`. Concrete implementations plug in their instance; laws run as named `DynamicTest`s.  
**Running**: `DoubleVectorSpaceStructureTest` — 6 module and vector space laws verified.

---

### Phase 2 — Data Model

**Package**: `math.interpolation.data`  
**Deliverables**: `Sample`, `GridSample`, `InterpolationData` (interface + sorted-list impl); bracket represented as
`Dyad<Sample, Sample>` from Aletheia.  
**Tests**: bracket lookup — exact hit, between two knots, below minimum, above maximum, single-knot.

---

### Phase 3 — Framework Skeleton + Policies

**Deliverables**: `Interpolator`, `InterpolationMethod`, `ExtrapolationPolicy`, `TieBreakingPolicy`,
`ParameterizationMode`.  
**Note**: Thin types with no logic; no dedicated tests beyond compilation.

---

### Phase 4 — Standard Space Implementations

**Package**: `math.analysis.space.standard`  
**Deliverables**: `DoubleInterpolatableSpace` (with `ParameterizationMode`),
`BigDecimalInterpolatableSpace`, `LocalDateInterpolatableSpace`, `DoubleVectorSpaceStructure`,
`BigDecimalVectorSpaceStructure`.  
**Tests**: Each concrete class provides a `@TestFactory` that delegates to the corresponding
`*Laws` record — e.g. `new DifferentiableSpaceLaws<>(space, 1.0, 5.0, 9.0).tests()`.

---

### Phase 5 — Nearest Neighbor

**Deliverable**: `NearestNeighborMethod` + full tests.  
**Purpose**: Validates the entire pipeline with the simplest algorithm.  
**Tests**: correctness, all three `TieBreakingPolicy` branches (including `AVERAGE` with a
`BlendableSpace<Y>`), single-knot edge case, all `ExtrapolationPolicy` branches.

---

### Phase 6 — Linear Interpolation

**Deliverable**: `LinearInterpolationMethod` + comprehensive tests.  
**Tests**: correctness, all three `ExtrapolationPolicy` branches, date-based X with both
`ParameterizationMode` values, identity case (query == knot).

---

### Phase 7 — Cubic Spline

**Deliverables**: `NaturalCubicSplineMethod`, `MonotoneCubicMethod` + tests.  
**Shared utility**: `TridiagonalSolver` (package-private helper, reused by both).  
**Tests**: compare against closed-form reference values. Monotone-cubic specific: verify no
overshooting on strictly monotone input.

---

### Phase 8 — Two-Dimensional Grid

**Deliverable**: `InterpolationGrid` + builder + `GridSample`.  
**Tests**: construction, query at exact grid points, query in interior, each dimension using a
different method (linear × monotone-cubic), `ExtrapolationPolicy` interaction per dimension.

---

### Phase 9 — Integration Test: Vol Surface

**Deliverable**: `VolSurfaceInterpolationTest` end-to-end test.  
**Scenario**: 4×5 grid of (expiry `LocalDate`, strike `Double`) → vol `Double`.
Expiry dimension: linear. Strike dimension: monotone-cubic with `ParameterizationMode.LOG`.  
**Goal**: confirms the full feature works together, including the standard space impls and the grid.

---

*Document version: 2026-06-06*