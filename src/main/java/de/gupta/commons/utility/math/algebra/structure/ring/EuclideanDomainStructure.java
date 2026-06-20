package de.gupta.commons.utility.math.algebra.structure.ring;

import java.util.Objects;

public interface EuclideanDomainStructure<E> extends IntegralDomainStructure<E>, NormedStructure<E>
{
	DivisionResult<E> divideWithRemainder(E dividend, E divisor);

	default E quotient(final E dividend, final E divisor)
	{
		return divideWithRemainder(dividend, divisor).quotient();
	}

	default E remainder(final E dividend, final E divisor)
	{
		return divideWithRemainder(dividend, divisor).remainder();
	}

	default E gcd(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");

		E a = left;
		E b = right;
		while (!isZero(b))
		{
			final E remainder = remainder(a, b);
			a = b;
			b = remainder;
		}
		return a;
	}
}
