# Affine Order

## What it is

`AffinelyOrdered<E, D>` and `AffineOrderStructure<E, D>` model a **totally ordered affine space**: a set `E` equipped
with a total order and a displacement type `D`, where elements can be measured against each other and translated by
displacements.

`D` is intentionally unconstrained. It need not be part of this library's algebraic hierarchy — it can be
`java.time.Duration`, `Long`, or any type the caller provides. Enforcement of algebraic structure on `D` is the caller's
responsibility.

## Laws

Implementations must satisfy these laws (not enforced by the type system):

| Law          | Statement                                                                                   |
|--------------|---------------------------------------------------------------------------------------------|
| Round-trip   | `a.translate(a.displacementTo(b)) == b`                                                     |
| Antisymmetry | `a.displacementTo(b) == -(b.displacementTo(a))` (requires negation on D)                    |
| Additivity   | `a.displacementTo(c) == a.displacementTo(b) + b.displacementTo(c)` (requires addition on D) |
| Identity     | `a.translate(zero_D) == a` (requires identity on D)                                         |

The antisymmetry, additivity, and identity laws implicitly require D to be an abelian group — but the library does not
enforce this at the type level.

## The D = E case

`OrderedAdditiveGroup<E>` and `OrderedAdditiveGroupStructure<E>` are the special case where `D = E`. The group
operations on `E` naturally satisfy all four laws:

- `displacementTo(other)` = `other.subtract(this)` / `subtract(to, from)`
- `translate(displacement)` = `add(this, displacement)` / `add(point, displacement)`

## Relationship to other abstractions

- `AffinelyOrdered<E, D>` extends `TotallyOrdered<E>` — affine order enriches total order, not the other way around. A
  total order does not imply a displacement group.
- `AffineOrderStructure<E, D>` extends `TotalOrderStructure<E>` — same reasoning on the structure side.
- `History<T, E>` (planned, outside athena) would require `T` to be `AffinelyOrdered<T, D>` for some displacement type
  `D`, enabling time-weighted operations.