package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.element.binary.Monoid;

public interface AdditiveMonoid<E extends AdditiveMonoid<E>> extends AdditiveSemigroup<E>, Monoid<E>
{
	default E zero()
	{
		return identity();
	}

	@Override
	default E addAll(final Loom<E> others)
	{
		return combineAll(others);
	}

	@Override
	default E addAll(final Iterable<? extends E> others)
	{
		return combineAll(others);
	}
}
