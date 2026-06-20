package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public enum IntegerEuclideanDomainStructure implements OrderedEuclideanDomainStructure<IntegersAsEuclideanDomain>
{
	INSTANCE;

	@Override
	public IntegersAsEuclideanDomain zero()
	{
		return IntegersAsEuclideanDomain.of(0);
	}

	@Override
	public IntegersAsEuclideanDomain one()
	{
		return IntegersAsEuclideanDomain.of(1);
	}

	@Override
	public IntegersAsEuclideanDomain multiply(final IntegersAsEuclideanDomain left,
	                                          final IntegersAsEuclideanDomain right)
	{
		return left.multiply(right);
	}

	@Override
	public IntegersAsEuclideanDomain add(final IntegersAsEuclideanDomain left, final IntegersAsEuclideanDomain right)
	{
		return left.add(right);
	}

	@Override
	public boolean isZero(final IntegersAsEuclideanDomain element)
	{
		return element.isZero();
	}

	@Override
	public DivisionResult<IntegersAsEuclideanDomain> divideFloor(final IntegersAsEuclideanDomain dividend,
	                                                             final IntegersAsEuclideanDomain divisor)
	{
		return dividend.divideFloor(divisor);
	}

	@Override
	public long norm(final IntegersAsEuclideanDomain element)
	{
		return element.norm();
	}

	@Override
	public IntegersAsEuclideanDomain negate(final IntegersAsEuclideanDomain element)
	{
		return element.negate();
	}

	@Override
	public OrderRelation compare(final IntegersAsEuclideanDomain left, final IntegersAsEuclideanDomain right)
	{
		return left.compare(right);
	}
}