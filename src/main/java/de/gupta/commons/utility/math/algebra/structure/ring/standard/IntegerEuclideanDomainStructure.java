package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedEuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public enum IntegerEuclideanDomainStructure implements OrderedEuclideanDomainStructure<IntegralNumber>
{
	INSTANCE;

	@Override
	public IntegralNumber zero()
	{
		return IntegralNumberFactory.of(0);
	}

	@Override
	public IntegralNumber one()
	{
		return IntegralNumberFactory.of(1);
	}

	@Override
	public IntegralNumber multiply(final IntegralNumber left,
	                               final IntegralNumber right)
	{
		return left.multiply(right);
	}

	@Override
	public IntegralNumber add(final IntegralNumber left, final IntegralNumber right)
	{
		return left.add(right);
	}

	@Override
	public boolean isZero(final IntegralNumber element)
	{
		return element.isZero();
	}

	@Override
	public DivisionResult<IntegralNumber> divideFloor(final IntegralNumber dividend,
	                                                  final IntegralNumber divisor)
	{
		return dividend.divideFloor(divisor);
	}

	@Override
	public long norm(final IntegralNumber element)
	{
		return element.norm();
	}

	@Override
	public IntegralNumber negate(final IntegralNumber element)
	{
		return element.negate();
	}

	@Override
	public OrderRelation compare(final IntegralNumber left, final IntegralNumber right)
	{
		return left.compare(right);
	}
}