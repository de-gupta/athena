package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegersEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;

record IntegersAsEuclideanDomain(long value) implements IntegralNumbers
{
	private final static IntegersEuclideanDomainStructure canonicalStructure =
			IntegersEuclideanDomainStructure.INSTANCE;

	@Override
	public IntegralNumbers negate()
	{
		return IntegralNumberFactory.of(canonicalStructure.negate(value));
	}

	@Override
	public IntegralNumbers zero()
	{
		return IntegralNumberFactory.of(canonicalStructure.zero());
	}

	@Override
	public IntegralNumbers one()
	{
		return IntegralNumberFactory.of(canonicalStructure.one());
	}

	@Override
	public IntegralNumbers multiply(final IntegralNumbers other)
	{
		return IntegralNumberFactory.of(canonicalStructure.multiply(value, other.value()));
	}

	@Override
	public IntegralNumbers add(final IntegralNumbers other)
	{
		return IntegralNumberFactory.of(canonicalStructure.add(value, other.value()));
	}

	@Override
	public long norm()
	{
		return canonicalStructure.norm(value);
	}

	@Override
	public DivisionResult<IntegralNumbers> divideFloor(final IntegralNumbers divisor)
	{
		return canonicalStructure.divideFloor(value, divisor.value()).map(IntegralNumberFactory::of);
	}

	@Override
	public boolean isZero()
	{
		return canonicalStructure.isZero(value);
	}

	@Override
	public OrderRelation compare(final IntegralNumbers other)
	{
		return OrderRelation.from(Long.compare(value, other.value()));
	}
}