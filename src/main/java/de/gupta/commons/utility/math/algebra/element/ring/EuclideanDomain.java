package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface EuclideanDomain<E extends EuclideanDomain<E>> extends IntegralDomain<E>, Normed<Long>
{
	default E quotient(final E divisor)
	{
		return divideWithRemainder(divisor).quotient();
	}

	DivisionResult<E> divideWithRemainder(E divisor);

	default E gcd(final E other)
	{
		E a = self();
		E b = other;
		while (!b.isZero())
		{
			final E remainder = a.remainder(b);
			a = b;
			b = remainder;
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	default E remainder(final E divisor)
	{
		return divideWithRemainder(divisor).remainder();
	}
}