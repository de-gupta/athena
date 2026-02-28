package de.gupta.commons.utility.math.algebra.algebraicGroup;

import java.util.stream.IntStream;

public interface AdditiveGroupStructure<T>
{
	default T multiple(final T element, final int multiplier)
	{
		return multiplier == 0 ? zero() :
				multiplier < 0 ? multiple(negative(element), -multiplier) :
						IntStream.range(0, multiplier - 1)
								 .mapToObj(_ -> element)
								 .reduce(element, this::add);
	}

	T zero();

	T negative(final T element);

	T add(final T a, final T b);
}