You’ve now implicitly defined *two different notions* of “interval equality”, and you should pick one explicitly because
it affects correctness whenever multiple total orders over the same carrier set are possible.

## What your code currently means

In `BoundedIntervalImpl` / `UnboundedIntervalImpl`, `equals`/`hashCode` compare only the bounds and ignore `ios`. So:

- Two intervals with the same endpoint values compare equal even if they were built with different
  `IntervalOrderStructure<E>`.
- All operations that need ordering (`contains`, `overlaps`, `span`, `intersect`, etc.) use the *left operand’s* `ios`
  when combining intervals, because `ios` is stored per instance and there’s no compatibility check.

This is coherent only under an **ambient-order assumption**: “For a given `E`, there is one intended total order, and
all intervals are constructed with that order.” If that assumption is true for your domain/library usage, ignoring `ios`
in equality is fine and convenient.

But your API (`Intervals.over(order)`) *explicitly supports multiple orders for the same `E`*, so the ambient-order
assumption is not guaranteed by the type system.

## Why ignoring `ios` can be mathematically wrong

When multiple total orders exist, an interval is not just “two endpoints”; it is the subset:

- for bounded: `{ x | lower ≤ x ≤ upper }` (with open/closed nuance)
- for unbounded: similar with one side missing
  and **that subset depends on the order**.

So, under the “interval-as-set” view, equality should be: “they denote the same subset”, which generally requires the
same order (or at least order-equivalence on the relevant region).

Concrete failure mode:

- Suppose `E` can be ordered by `natural` and `reverseNatural`.
- Interval `[1,3]` under `natural` is `{1,2,3}` (in the usual discrete case).
- `[1,3]` under `reverseNatural` is typically *not even a valid interval under the same endpoint check* unless you
  normalize; more generally, the meaning changes.
  Even if you only allow orders where “1 ≤ 3” holds, you can still craft different orders where endpoint comparisons
  differ on other points, changing containment/overlap but leaving endpoint values identical. Your current equality
  would collapse these distinct meanings.

## What you should decide (and document)

Pick one of these design positions:

### Option A — Equality ignores order (keep as-is), but enforce “same order” operationally

Treat order as a **construction/context concern**, not part of value identity, but then you should *prevent mixing*
intervals from different orders because results depend on which `ios` is chosen.

Practically: require/ensure that any binary operation uses a shared order (by construction, by runtime checks, or by API
shape). Without that, callers can accidentally get non-commutative surprises like:

- `a.span(b)` ≠ `b.span(a)` if `a.ios != b.ios` and the orders disagree.
  Even if you test symmetry now, it’s only symmetric when both share the same order.

When this option is “mathematically acceptable”:

- Your library philosophy is “there is one canonical order per type `E` (or per application)”.
- `Intervals.over(order)` exists mainly for types that don’t implement `TotallyOrdered`, but users still conceptually
  pick *the* order and stick to it.

### Option B — Equality includes order (interval identity = endpoints + order)

Make `ios` part of interval identity. Then “same endpoints under different orders” are **not equal**, which matches the
“interval depends on order” math.

Caveat: what does it mean for two `IntervalOrderStructure<E>` instances to be equal? Java functional instances rarely
have meaningful structural equality. If you include `ios` directly in `equals`, you’ll usually end up with identity
equality (“same instance”) which may be too strict unless you canonicalize orders (singletons) or wrap them in stable,
comparable identifiers.

This option is best when:

- You expect multiple orders to coexist for the same `E` in the same JVM and you want accidental mixing to be visible.

### Option C — Move `ios` out of the value (intervals are pure data; operations take an order)

This is the “mathematics textbook” approach: an interval is just bounds; semantics come from an external `(E, ≤)`
structure. Equality is then purely structural (bounds only), and all operations require an explicit `ios` parameter (or
an `IntervalOperations<E>` object), eliminating the mixing hazard entirely.

You previously had something like this on the structure side; your refactor intentionally moved away from it for
ergonomics.

### Option D — Brand the order in the type (phantom type / wrapper)

Make it impossible to mix intervals of different orders at compile time by wrapping `E` in an order-tagged type or by
having the factory produce an interval type that carries a unique order token type. This is the most
“correct-by-construction”, but it’s heavyweight in Java.

## Decision: Option A

We go with **Option A — equality ignores order; mixing is caller error**.

### Rationale

- The element-side path (`Intervals.closed(a, b)` where `E extends TotallyOrdered<E>`) is naturally safe: the order is
  determined by `E` itself, so mixing is impossible by construction.
- The structure-side path (`Intervals.over(order)`) is an advanced escape hatch for types that do not carry their own
  ordering. In practice, a given application picks one order per type and sticks to it.
- Options B and D are impractical in Java: `IntervalOrderStructure` is a `@FunctionalInterface` so lambda instances have
  identity-only `equals`, making meaningful equality checks on `ios` impossible without abandoning lambda ergonomics.
  Option D requires leaking a phantom type parameter into every consumer API.
- Option C (intervals as pure bounds, operations take an explicit order) was the previous structure-side design; it was
  intentionally replaced for ergonomics.

### The contract

**Intervals constructed from different `Intervals.ForOrder` instances must not be mixed in binary operations** (
`contains`, `overlaps`, `abuts`, `span`, `intersect`). The left operand's `ios` is always used; if the orders differ,
results are silently wrong.

This contract is unenforced by the type system. It is the caller's responsibility. A future enforcement mechanism (e.g.
a runtime order-identity check) could be added without breaking the API.