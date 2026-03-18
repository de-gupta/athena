package de.gupta.commons.utility.math.algebra.element.binary;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface Semigroup<E extends Semigroup<E>>
{
	E combine(E other);

	default E combineAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), Semigroup::combine);
	}

	default E combineAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return combineAll(Loom.harness(others));
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}
