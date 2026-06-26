package de.gupta.commons.utility.math.algebra.element.algebra;

public final class LinearCombinationFactory
{
	public static <S, E> LinearCombination<S, E> empty()
	{
		return LinearCombinationImpl.empty();
	}

	public static <S, E> LinearCombination<S, E> of(final S coefficient, final E element)
	{
		return LinearCombinationImpl.of(coefficient, element);
	}
}