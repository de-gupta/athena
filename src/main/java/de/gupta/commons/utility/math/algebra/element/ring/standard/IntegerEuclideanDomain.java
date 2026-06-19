package de.gupta.commons.utility.math.algebra.element.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.LongEuclideanDomainStructure;

public record IntegerEuclideanDomain(long value) implements EuclideanDomain<IntegerEuclideanDomain>
{
	private final static LongEuclideanDomainStructure canonicalStructure = LongEuclideanDomainStructure.INSTANCE;

	@Override
	public IntegerEuclideanDomain negate()
	{
		return of(canonicalStructure.negate(value));
	}

	public static IntegerEuclideanDomain of(final long value)
	{
		return new IntegerEuclideanDomain(value);
	}

	@Override
	public IntegerEuclideanDomain zero()
	{
		return of(canonicalStructure.zero());
	}

	@Override
	public IntegerEuclideanDomain one()
	{
		return of(canonicalStructure.one());
	}

	@Override
	public IntegerEuclideanDomain multiply(final IntegerEuclideanDomain other)
	{
		return of(canonicalStructure.multiply(value, other.value));
	}

	@Override
	public IntegerEuclideanDomain add(final IntegerEuclideanDomain other)
	{
		return of(canonicalStructure.add(value, other.value));
	}

	@Override
	public long norm()
	{
		return canonicalStructure.norm(value);
	}

	@Override
	public DivisionResult<IntegerEuclideanDomain> divideWithRemainder(final IntegerEuclideanDomain divisor)
	{
		return canonicalStructure.divideWithRemainder(value, divisor.value()).map(IntegerEuclideanDomain::of);
	}

	@Override
	public boolean isZero()
	{
		return canonicalStructure.isZero(value);
	}
}