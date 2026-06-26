package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public final class LinearCombinationFactory
{
	public static <S extends Ring<S>, E> LinearCombination<S, E> empty()
	{
		return LinearCombinationImpl.empty();
	}

	public static <S extends Ring<S>, E> LinearCombination<S, E> of(final S coefficient, final E element)
	{
		return LinearCombinationImpl.of(coefficient, element);
	}
}