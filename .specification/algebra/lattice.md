If you only want to capture:

```csharp
T And(T x, T y);
T Or (T x, T y);
```

then mathematically you've specified **almost nothing**. Two arbitrary binary operations on a set are called an *
*algebra with two binary operations** (or a magma with two operations). There is no standard name.

The interesting question is: **what laws do you want?**

### Option 1: Just two semigroups

If both operations are associative:

```text
(x ∧ y) ∧ z = x ∧ (y ∧ z)
(x ∨ y) ∨ z = x ∨ (y ∨ z)
```

then you have two semigroup structures on the same carrier.

This is very weak and probably not enough to justify names like "And" and "Or".

---

### Option 2: A lattice-like algebra

If you additionally require:

```text
x ∧ y = y ∧ x
x ∨ y = y ∨ x
```

(idempotence)

```text
x ∧ x = x
x ∨ x = x
```

(absorption)

```text
x ∧ (x ∨ y) = x
x ∨ (x ∧ y) = x
```

then you've arrived at the algebraic definition of a **lattice**.

Notice something important:

A lattice does **not** require an order relation in its definition.

The order can be recovered later:

[
x \le y \iff x \wedge y = x.
]

So if your concern is "I don't want to depend on Poset", that's fine. Universal algebra usually defines lattices purely
by operations.

This is the first place where "and" and "or" acquire their familiar meaning.

---

### Option 3: Distributive lattice

Add:

```text
x ∧ (y ∨ z) = (x ∧ y) ∨ (x ∧ z)
x ∨ (y ∧ z) = (x ∨ y) ∧ (x ∨ z)
```

Now you're in distributive lattices.

Specifications satisfy this.

---

### Option 4: Boolean algebra

Add:

```text
Not(x)
```

and complements.

This is the structure of your specifications.

---

### What I would do in a hierarchy

For a generic algebra library I'd probably separate:

```text
IHasMeet<T>      // ∧
IHasJoin<T>      // ∨

ILattice<T>
    : IHasMeet<T>, IHasJoin<T>

IDistributiveLattice<T>
    : ILattice<T>

IBooleanAlgebra<T>
    : IDistributiveLattice<T>, IComplement<T>
```

and avoid an interface called `IAndOr<T>`.

The reason is that "and" and "or" are names that only become meaningful once some laws are present. Without laws they're
just `Op1` and `Op2`.

---

### For Specification<T>

The surprising thing is that your specifications are not merely "something with AND and OR".

They satisfy:

* associativity
* commutativity
* idempotence
* absorption
* distributivity
* complements

which means they form a Boolean algebra exactly.

So if you're looking for the mathematically correct abstraction above `Specification<T>`, I'd model it as a Boolean
algebra (or perhaps distributive lattice if you don't require negation), not as some weaker "has and/or" structure.

In fact, `Specification<T>` is one of the canonical examples people use when explaining Boolean algebras: predicates
under conjunction and disjunction.