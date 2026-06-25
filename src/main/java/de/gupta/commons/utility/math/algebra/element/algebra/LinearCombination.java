package de.gupta.commons.utility.math.algebra.element.algebra;

import java.util.List;
import java.util.function.UnaryOperator;

public sealed interface LinearCombination<S, E> permits LinearCombinationImpl
{
	static <S, E> LinearCombination<S, E> empty()
	{
		return LinearCombinationImpl.empty();
	}

	static <S, E> LinearCombination<S, E> of(final S coefficient, final E element)
	{
		return LinearCombinationImpl.of(coefficient, element);
	}

	LinearCombination<S, E> addEntry(S coefficient, E element);

	LinearCombination<S, E> removeEntries(E element);

	LinearCombination<S, E> combine(LinearCombination<S, E> other);

	LinearCombination<S, E> scaleCoefficients(UnaryOperator<S> transform);

	List<Entry<S, E>> terms();

	int size();

	boolean isEmpty();

	record Entry<S, E>(S coefficient, E element)
	{
	}
}