package de.gupta.commons.utility.math.algebra.algebraicGroup;

import java.util.stream.IntStream;

public interface GroupStructure<T>
{
	default T power(final T element, final int exponent)
	{
		return exponent == 0 ? identity() :
				exponent < 0 ? power(inverse(element), -exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> element)
								 .reduce(element, this::multiply);
	}

	T identity();

	T inverse(final T element);

	T multiply(final T a, final T b);
}