# Equality in `athena` math: `equals()` vs `compare()`

This library intentionally distinguishes **ordering** (via `compare`) from Java **object equality** (via `equals`).
That separation is powerful, but it creates a few sharp edges when the same domain is used in both “ordered” and
“value object” roles.

This note documents the current behavior and the main pitfalls so callers can make informed choices.

## Key idea

Some types have a **total order** where `compare(a, b) == 0` does **not** imply `a.equals(b)`.

Classic examples in the JDK ecosystem include `BigDecimal` (`new BigDecimal("1.0")` vs `new BigDecimal("1.00")`).
In such cases, any API that uses `compare` for semantics but `equals`/`hashCode` for identity can behave
counterintuitively in sets, maps, caches, and tests.

## Intervals

### Operations are order-based

Interval operations such as containment, overlap, span, and intersection use the interval’s
`IntervalOrderStructure` (i.e., `compare`) to define meaning.

### Equality is structural (bounds-only, `equals()`-based)

Interval `equals`/`hashCode` currently compare only the bounds (and for bounded intervals: both bounds),
and ignore the order structure carried by the instance.

Consequences:

- Two intervals can be semantically equivalent under the order (same set of elements w.r.t. `compare`) but still be
  `!equals()` if their endpoint values are different objects or differ only in `equals()`-visible details.
- If multiple total orders over the same carrier type are used in the same JVM, two intervals can be `equals()`
  while their operations are interpreted under different orders.

### Order mixing is caller error

`Intervals.over(order)` explicitly supports multiple orders. Intervals produced by different `Intervals.ForOrder`
instances must not be mixed in binary operations (`contains`, `overlaps`, `abuts`, `span`, `intersect`), because the
left operand’s order is used and there is no runtime compatibility check.

## Bounds

`Bound` is a record-based value type, so its `equals` is Java structural equality (`value.equals(...)` plus
open/closed).
There is also `Bounds.areEqual(...)` which defines equality using `TotallyOrdered.compare(...)`.

These two equalities can diverge for types where compare-equality differs from object-equality.

## Series

`SeriesFactory` builds a `TreeMap` using a comparator derived from ordering (`TotallyOrdered.compare(...).signum()` or a
`TotalOrderStructure`).

Consequences:

- If two distinct indices are compare-equal (`compare == 0`) but not `equals`, they will be treated as the same key by
  the `TreeMap` comparator and one will overwrite the other on insertion.
- This is sometimes the intended mathematical behavior, but it can surprise callers who expect Java Map semantics based
  on `equals`.

## Guidance (for callers)

- Prefer domains where compare-equality and object-equality align when you need stable `equals`/`hashCode` semantics.
- If you must use a type where they differ (e.g., BigDecimal-like), avoid relying on `equals` of intervals/bounds for
  semantic assertions; assert using the order-based operations instead.
- Do not mix intervals created by different `Intervals.ForOrder` instances in binary operations.