package de.gupta.commons.utility.math.algebra.structure.ring.concrete;

import de.gupta.commons.utility.math.algebra.element.ring.concrete.IntegerEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

public enum IntegerEuclideanDomainStructure implements EuclideanDomainStructure<IntegerEuclideanDomain>
{
	INSTANCE;

	@Override
	public IntegerEuclideanDomain zero()
	{
		return IntegerEuclideanDomain.of(0);
	}

	@Override
	public IntegerEuclideanDomain one()
	{
		return IntegerEuclideanDomain.of(1);
	}

	@Override
	public IntegerEuclideanDomain add(final IntegerEuclideanDomain left, final IntegerEuclideanDomain right)
	{
		return left.add(right);
	}

	@Override
	public IntegerEuclideanDomain multiply(final IntegerEuclideanDomain left, final IntegerEuclideanDomain right)
	{
		return left.multiply(right);
	}

	@Override
	public IntegerEuclideanDomain additiveInverse(final IntegerEuclideanDomain element)
	{
		return element.negate();
	}

	@Override
	public boolean isZero(final IntegerEuclideanDomain element)
	{
		return element.isZero();
	}

	@Override
	public DivisionResult<IntegerEuclideanDomain> divideWithRemainder(final IntegerEuclideanDomain dividend,
	                                                                  final IntegerEuclideanDomain divisor)
	{
		return dividend.divideWithRemainder(divisor);
	}

	@Override
	public long norm(final IntegerEuclideanDomain element)
	{
		return element.norm();
	}
}