package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

public interface OrderedEuclideanDomainStructure<E>
		extends EuclideanDomainStructure<E>, OrderedRingStructure<E>
{
	@Override
	default DivisionResult<E> divideWithRemainder(final E dividend, final E divisor)
	{
		return divideFloor(dividend, divisor);
	}

	DivisionResult<E> divideFloor(E dividend, E divisor);

	default DivisionResult<E> divide(final E dividend, final E divisor, final RoundingStrategy<E> strategy)
	{
		return strategy.divide(dividend, divisor);
	}
}