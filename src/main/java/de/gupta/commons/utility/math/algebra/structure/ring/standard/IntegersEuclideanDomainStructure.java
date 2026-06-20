package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

import java.util.Objects;

public enum IntegersEuclideanDomainStructure implements EuclideanDomainStructure<Long>
{
	INSTANCE;

	@Override
	public DivisionResult<Long> divideWithRemainder(final Long dividend, final Long divisor)
	{
		return divideFloor(dividend, divisor);
	}

	public DivisionResult<Long> divideFloor(final Long dividend, final Long divisor)
	{
		return DivisionResult.of(Math.floorDiv(dividend, divisor), Math.floorMod(dividend, divisor));
	}

	@Override
	public long norm(final Long element)
	{
		return Math.absExact(element);
	}

	@Override
	public boolean isZero(final Long element)
	{
		return Objects.equals(element, zero());
	}

	@Override
	public Long zero()
	{
		return 0L;
	}

	@Override
	public Long negate(final Long element)
	{
		return Math.negateExact(element);
	}

	@Override
	public Long one()
	{
		return 1L;
	}

	@Override
	public Long multiply(final Long left, final Long right)
	{
		return Math.multiplyExact(left, right);
	}

	@Override
	public Long add(final Long left, final Long right)
	{
		return Math.addExact(left, right);
	}
}