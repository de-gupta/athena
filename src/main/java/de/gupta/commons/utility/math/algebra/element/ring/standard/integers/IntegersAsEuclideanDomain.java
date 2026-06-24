package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

record IntegersAsEuclideanDomain(long value) implements IntegralNumber
{
	private final static IntegersEuclideanDomainStructure canonicalStructure =
			IntegersEuclideanDomainStructure.INSTANCE;

	@Override
	public IntegralNumber negate()
	{
		return IntegralNumberFactory.of(canonicalStructure.negate(value));
	}

	@Override
	public IntegralNumber zero()
	{
		return IntegralNumberFactory.of(canonicalStructure.zero());
	}

	@Override
	public IntegralNumber one()
	{
		return IntegralNumberFactory.of(canonicalStructure.one());
	}

	@Override
	public IntegralNumber multiply(final IntegralNumber other)
	{
		return IntegralNumberFactory.of(canonicalStructure.multiply(value, other.value()));
	}

	@Override
	public IntegralNumber add(final IntegralNumber other)
	{
		return IntegralNumberFactory.of(canonicalStructure.add(value, other.value()));
	}

	@Override
	public long norm()
	{
		return canonicalStructure.norm(value);
	}

	@Override
	public DivisionResult<IntegralNumber> divideFloor(final IntegralNumber divisor)
	{
		return canonicalStructure.divideFloor(value, divisor.value()).map(IntegralNumberFactory::of);
	}

	@Override
	public boolean isZero()
	{
		return canonicalStructure.isZero(value);
	}

	@Override
	public OrderRelation compare(final IntegralNumber other)
	{
		return canonicalStructure.compare(value, other.value());
	}
}