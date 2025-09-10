package de.gupta.commons.utility.math.algebra.algebraicGroup;

import java.util.stream.IntStream;

public interface Group<T>
{
	default T power(T element, int exponent)
	{
		return exponent == 0 ? identity() :
				exponent < 0 ? power(inverse(element), -exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> element)
								 .reduce(element, this::multiply);
	}

	T identity();

	T inverse(T element);

	T multiply(T a, T b);
}