package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import java.util.Objects;
import java.util.stream.IntStream;

public interface AdditiveGroupStructure<E> extends AdditiveMonoidStructure<E>
{
	E negate(E element);

	default E subtract(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return add(left, negate(right));
	}

	default E power(final E element, final int exponent)
	{
		Objects.requireNonNull(element, "element");
		return exponent == 0 ? zero() :
				exponent < 0 ? power(negate(element), -exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> element)
								 .reduce(element, this::add);
	}
}