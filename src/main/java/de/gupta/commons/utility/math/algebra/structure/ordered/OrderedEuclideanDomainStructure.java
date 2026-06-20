package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

public interface OrderedEuclideanDomainStructure<E>
		extends EuclideanDomainStructure<E>, OrderedRingStructure<E>, ScalarDivisibleStructure<E>
{
	DivisionResult<E> divideFloor(E dividend, E divisor);

	@Override
	default DivisionResult<E> divideWithRemainder(final E dividend, final E divisor)
	{
		return divideFloor(dividend, divisor);
	}

	default DivisionResult<E> divide(final E dividend, final E divisor, final RoundingStrategy<E> strategy)
	{
		return strategy.divide(dividend, divisor);
	}

	@Override
	default DivisionResult<E> divide(final E element, final long scalar, final RoundingStrategy<E> strategy)
	{
		return divide(element, elementFromLong(scalar), strategy);
	}

	private E elementFromLong(final long n)
	{
		if (n == 0) return zero();
		long abs = Math.abs(n);
		E result = zero();
		E power = one();
		while (abs > 0)
		{
			if ((abs & 1) == 1) result = add(result, power);
			power = add(power, power);
			abs >>= 1;
		}
		return n < 0 ? negate(result) : result;
	}
}
