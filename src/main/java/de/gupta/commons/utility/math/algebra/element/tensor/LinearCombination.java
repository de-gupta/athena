package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.List;
import java.util.function.UnaryOperator;

public sealed interface LinearCombination<S extends Ring<S>, E> extends Module<LinearCombination<S, E>, S>
		permits LinearCombinationImpl
{
	LinearCombination<S, E> addEntry(final S coefficient, final E element);

	LinearCombination<S, E> removeEntries(final E element);

	LinearCombination<S, E> combine(final LinearCombination<S, E> other);

	LinearCombination<S, E> transformCoefficients(final UnaryOperator<S> transform);

	List<Entry<S, E>> terms();

	int size();

	boolean isEmpty();

	record Entry<S extends Ring<S>, E>(S coefficient, E element)
	{
		Entry<S, E> scale(final S scalar)
		{
			return transformCoefficients(c -> c.multiply(scalar));
		}

		Entry<S, E> transformCoefficients(final UnaryOperator<S> transform)
		{
			return Entry.of(transform.apply(coefficient), element);
		}

		static <S extends Ring<S>, E> Entry<S, E> of(final S coefficient, final E element)
		{
			return new Entry<>(coefficient, element);
		}
	}
}