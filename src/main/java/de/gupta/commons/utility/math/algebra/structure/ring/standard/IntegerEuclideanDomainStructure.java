package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public enum IntegerEuclideanDomainStructure implements OrderedEuclideanDomainStructure<IntegralNumbers>
{
	INSTANCE;

	@Override
	public IntegralNumbers zero()
	{
		return IntegralNumberFactory.of(0);
	}

	@Override
	public IntegralNumbers one()
	{
		return IntegralNumberFactory.of(1);
	}

	@Override
	public IntegralNumbers multiply(final IntegralNumbers left,
	                                final IntegralNumbers right)
	{
		return left.multiply(right);
	}

	@Override
	public IntegralNumbers add(final IntegralNumbers left, final IntegralNumbers right)
	{
		return left.add(right);
	}

	@Override
	public boolean isZero(final IntegralNumbers element)
	{
		return element.isZero();
	}

	@Override
	public DivisionResult<IntegralNumbers> divideFloor(final IntegralNumbers dividend,
	                                                   final IntegralNumbers divisor)
	{
		return dividend.divideFloor(divisor);
	}

	@Override
	public long norm(final IntegralNumbers element)
	{
		return element.norm();
	}

	@Override
	public IntegralNumbers negate(final IntegralNumbers element)
	{
		return element.negate();
	}

	@Override
	public OrderRelation compare(final IntegralNumbers left, final IntegralNumbers right)
	{
		return left.compare(right);
	}
}