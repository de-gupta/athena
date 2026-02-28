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

	default T multiple(final T element, final int multiplier)
	{
		return power(element, multiplier);
	}

	T identity();

	default T zero()
	{
		return identity();
	}

	T inverse(final T element);

	default T negation(final T element)
	{
		return inverse(element);
	}

	T multiply(final T a, final T b);

	default T add(final T a, final T b)
	{
		return multiply(a, b);
	}

	default T divide(final T a, final T b)
	{
		return multiply(a, inverse(b));
	}

	default T subtract(final T a, final T b)
	{
		return divide(a, b);
	}
}