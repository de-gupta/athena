package de.gupta.commons.utility.math.algebra.element.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegersEuclideanDomainStructure;

public record IntegersAsEuclideanDomain(long value) implements EuclideanDomain<IntegersAsEuclideanDomain>
{
	private final static IntegersEuclideanDomainStructure canonicalStructure =
			IntegersEuclideanDomainStructure.INSTANCE;

	@Override
	public IntegersAsEuclideanDomain negate()
	{
		return of(canonicalStructure.negate(value));
	}

	public static IntegersAsEuclideanDomain of(final long value)
	{
		return new IntegersAsEuclideanDomain(value);
	}

	@Override
	public IntegersAsEuclideanDomain zero()
	{
		return of(canonicalStructure.zero());
	}

	@Override
	public IntegersAsEuclideanDomain one()
	{
		return of(canonicalStructure.one());
	}

	@Override
	public IntegersAsEuclideanDomain multiply(final IntegersAsEuclideanDomain other)
	{
		return of(canonicalStructure.multiply(value, other.value));
	}

	@Override
	public IntegersAsEuclideanDomain add(final IntegersAsEuclideanDomain other)
	{
		return of(canonicalStructure.add(value, other.value));
	}

	@Override
	public long norm()
	{
		return canonicalStructure.norm(value);
	}

	@Override
	public DivisionResult<IntegersAsEuclideanDomain> divideWithRemainder(final IntegersAsEuclideanDomain divisor)
	{
		return canonicalStructure.divideWithRemainder(value, divisor.value()).map(IntegersAsEuclideanDomain::of);
	}

	@Override
	public boolean isZero()
	{
		return canonicalStructure.isZero(value);
	}
}