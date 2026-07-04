package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface OrderedEuclideanDomain<E extends OrderedEuclideanDomain<E>>
		extends EuclideanDomain<E>, OrderedRing<E>, ScalarDivisible<E, Long>
{
	@Override
	default DivisionResult<E> divideWithRemainder(final E divisor)
	{
		return divideFloor(divisor);
	}

	DivisionResult<E> divideFloor(E divisor);

	@Override
	default DivisionResult<E> divide(final Long scalar, final DivisionConvention<E> strategy)
	{
		return divide(elementFromLong(scalar), strategy);
	}

	default DivisionResult<E> divide(final E divisor, final DivisionConvention<E> strategy)
	{
		return strategy.divide(self(), divisor);
	}

	private E elementFromLong(final long n)
	{
		if (n == 0) return zero();
		long abs = Math.abs(n);
		E result = zero();
		E power = one();
		while (abs > 0)
		{
			if ((abs & 1) == 1) result = result.add(power);
			power = power.add(power);
			abs >>= 1;
		}
		return n < 0 ? result.negate() : result;
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}