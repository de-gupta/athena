package de.gupta.commons.utility.math.algebra.structure.binary;

import java.util.Objects;
import java.util.stream.IntStream;

public interface GroupStructure<E> extends MonoidStructure<E>
{
	E inverse(E element);

	default E divide(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return multiply(left, inverse(right));
	}

	default E power(final E element, final int exponent)
	{
		Objects.requireNonNull(element, "element");
		return exponent == 0 ? identity() :
				exponent < 0 ? power(inverse(element), -exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> element)
								 .reduce(element, this::multiply);
	}
}