# Interpolation

Generic interpolation framework for any input type X and output type Y, built on the algebraic structures in
`math.algebra` and the analytic spaces in `math.analysis`.

---

## Concepts

| Concept                      | What it is                                                                             |
|------------------------------|----------------------------------------------------------------------------------------|
| `InterpolatableSpace<X>`     | Orders and parameterises the input domain (extends `Comparator<X>`)                    |
| `DifferentiableSpace<X>`     | `InterpolatableSpace<X>` that also provides interval spans — required by cubic methods |
| `VectorSpaceStructure<Y, F>` | Defines the output space: addition, scalar multiplication, field of scalars            |
| `InterpolationData<X, Y>`    | Sorted, immutable collection of `Sample<X, Y>` knots                                   |
| `InterpolationMethod<X, Y>`  | Strategy: `InterpolationData → Interpolator`                                           |
| `Interpolator<X, Y>`         | Fitted function: `X → Y`                                                               |

---

## Policies

**`ExtrapolationPolicy`** — behaviour outside the knot range:

| Value       | Behaviour                        |
|-------------|----------------------------------|
| `FLAT`      | Clamp to the boundary knot value |
| `LINEAR`    | Extend the boundary segment      |
| `FORBIDDEN` | Throw `IllegalArgumentException` |

**`TieBreakingPolicy`** — nearest-neighbour ties only:

| Value     | Behaviour                                                          |
|-----------|--------------------------------------------------------------------|
| `LOWER`   | Return the knot with the lower X value *(default)*                 |
| `UPPER`   | Return the knot with the higher X value                            |
| `AVERAGE` | Blend the two equidistant values (requires `VectorSpaceStructure`) |

**`ParameterizationMode`** — for numeric input spaces:

| Value    | Behaviour                                                      |
|----------|----------------------------------------------------------------|
| `LINEAR` | `λ = (x − x₁) / (x₂ − x₁)` *(default)*                         |
| `LOG`    | `λ = ln(x/x₁) / ln(x₂/x₁)` — smoother for strike interpolation |

---

## Standard spaces

| Class                                     | Input type          | Notes                                            |
|-------------------------------------------|---------------------|--------------------------------------------------|
| `DoubleInterpolatableSpace.LINEAR`        | `Double`            | Linear parameterisation                          |
| `DoubleInterpolatableSpace.LOG`           | `Double`            | Log parameterisation — use for strike dimensions |
| `BigDecimalInterpolatableSpace.LINEAR`    | `BigDecimal`        |                                                  |
| `BigDecimalInterpolatableSpace.LOG`       | `BigDecimal`        |                                                  |
| `LocalDateInterpolatableSpace.INSTANCE`   | `LocalDate`         | Calendar-day distance                            |
| `DoubleVectorSpaceStructure.INSTANCE`     | output `Double`     | Standard scalar output                           |
| `BigDecimalVectorSpaceStructure.INSTANCE` | output `BigDecimal` |                                                  |

---

## Interpolation methods

All methods live in `de.gupta.commons.utility.math.interpolation.method`.

### Nearest neighbour

```java
// LOWER or UPPER tie-breaking
NearestNeighborMethod<Double, Double> method =
		NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR, TieBreakingPolicy.LOWER);

// AVERAGE tie-breaking — requires a VectorSpaceStructure
NearestNeighborMethod<Double, Double> method =
		NearestNeighborMethod.averaging(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE);
```

Requires only `MetricSpace<X>`. Works with any output type Y.

### Linear interpolation

```java
LinearInterpolationMethod<LocalDate, Double, Double> method =
		LinearInterpolationMethod.of(
				LocalDateInterpolatableSpace.INSTANCE,   // space
				DoubleVectorSpaceStructure.INSTANCE,     // output space
				d -> d,                                  // double → scalar field (identity for Double)
				ExtrapolationPolicy.FLAT);
```

Requires `InterpolatableSpace<X>` and `ModuleStructure<Y, F>`.

### Natural cubic spline

```java
NaturalCubicSplineMethod<Double, Double, Double> method =
		NaturalCubicSplineMethod.of(
				DoubleInterpolatableSpace.LINEAR,
				DoubleVectorSpaceStructure.INSTANCE,
				d -> d,
				ExtrapolationPolicy.FLAT);
```

Requires `DifferentiableSpace<X>` and `VectorSpaceStructure<Y, F>`. Works for any vector-valued output.

### Monotone cubic (Fritsch–Carlson) — *preferred for financial data*

```java
MonotoneCubicMethod<Double, Double> method =
		MonotoneCubicMethod.of(
				DoubleInterpolatableSpace.LOG,    // log-space for strike dimension
				DoubleVectorSpaceStructure.INSTANCE,
				d -> d,                          // double → F
				f -> f,                          // F → double (for monotonicity check)
				ExtrapolationPolicy.FLAT);
```

Requires `DifferentiableSpace<X>` and `VectorSpaceStructure<F, F>` (scalar output only). Guarantees no overshooting on
monotone input — critical for vol surfaces.

---

## 1D interpolation

```java
InterpolationData<Double, Double> data = InterpolationData.of(
		List.of(Sample.of(1.0, 0.10), Sample.of(2.0, 0.15), Sample.of(3.0, 0.18)),
		Double::compare);

Interpolator<Double, Double> curve = method.fit(data);
double vol = curve.interpolate(1.5);
```

---

## 2D interpolation — vol surface

The `InterpolationGrid<X1, X2, Y>` composes two independent 1D methods. Each dimension specifies its own space and
interpolation method.

```java
// Build the grid from (expiry, strike) → implied vol data
List<GridSample<LocalDate, Double, Double>> rawData = ...;

InterpolationGrid<LocalDate, Double, Double> surface =
		InterpolationGrid.<LocalDate, Double, Double>builder()
		                 .withData(rawData)
		                 .dimension1(
								 LocalDateInterpolatableSpace.INSTANCE,               // expiry ordering
								 LinearInterpolationMethod.of(
										 LocalDateInterpolatableSpace.INSTANCE,
										 DoubleVectorSpaceStructure.INSTANCE,
										 d -> d, ExtrapolationPolicy.FLAT))
		                 .dimension2(
								 DoubleInterpolatableSpace.LOG,                       // log-strike ordering
								 MonotoneCubicMethod.of(
										 DoubleInterpolatableSpace.LOG,
										 DoubleVectorSpaceStructure.INSTANCE,
										 d -> d, f -> f, ExtrapolationPolicy.FLAT))
		                 .build();

double impliedVol = surface.interpolate(expiry, strike);
```

**How it works:** at build time, one `dim2` interpolator is fitted per expiry slice. At query time, each slice
interpolator is evaluated at the target strike, producing virtual `(expiry, vol)` samples; `dim1` then interpolates over
those. The two dimensions are fully decoupled — different methods, different spaces, different extrapolation policies.

**`GridSample<X1, X2, Y>`** is the input record: `(X1 x1, X2 x2, Y y)`. Data does not need to be sorted or grouped
before passing to the builder.

---

## Scalar field conversion (`scalarOf`)

All methods except nearest-neighbour take a `DoubleFunction<F>` that converts the parameterisation result (always a
`double`) to the scalar field `F`:

| Output type  | `scalarOf`            |
|--------------|-----------------------|
| `Double`     | `d -> d`              |
| `BigDecimal` | `BigDecimal::valueOf` |

Monotone cubic additionally requires `ToDoubleFunction<F>` (`doubleOf`) for the monotonicity check:

| Scalar type  | `doubleOf`                |
|--------------|---------------------------|
| `Double`     | `f -> f`                  |
| `BigDecimal` | `BigDecimal::doubleValue` |