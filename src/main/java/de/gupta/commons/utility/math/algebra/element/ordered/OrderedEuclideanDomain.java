package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public interface OrderedEuclideanDomain<E extends OrderedEuclideanDomain<E>>
		extends EuclideanDomain<E>, OrderedRing<E>
{
	@Override
	default DivisionResult<E> divideWithRemainder(final E divisor)
	{
		return divideFloor(divisor);
	}

	DivisionResult<E> divideFloor(E divisor);

	default DivisionResult<E> divide(final E divisor, final RoundingStrategy<E> strategy)
	{
		return strategy.divide(self(), divisor);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}