package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface AdditiveSemigroup<E extends AdditiveSemigroup<E>>
{
	E add(E other);

	default E addAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), AdditiveSemigroup::add);
	}

	default E addAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return addAll(Loom.harness(others));
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}