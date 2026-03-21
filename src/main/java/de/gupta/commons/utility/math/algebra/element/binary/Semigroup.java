package de.gupta.commons.utility.math.algebra.element.binary;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface Semigroup<E extends Semigroup<E>>
{
	E multiply(E other);

	default E multiplyAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), Semigroup::multiply);
	}

	default E multiplyAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return multiplyAll(Loom.harness(others));
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}