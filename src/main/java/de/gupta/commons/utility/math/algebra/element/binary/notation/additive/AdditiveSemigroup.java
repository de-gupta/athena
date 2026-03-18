package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.element.binary.Semigroup;

public interface AdditiveSemigroup<E extends AdditiveSemigroup<E>> extends Semigroup<E>
{
	default E add(final E other)
	{
		return combine(other);
	}

	default E addAll(final Loom<E> others)
	{
		return combineAll(others);
	}

	default E addAll(final Iterable<? extends E> others)
	{
		return combineAll(others);
	}
}
