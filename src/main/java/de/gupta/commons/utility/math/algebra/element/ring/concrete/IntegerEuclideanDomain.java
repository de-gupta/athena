package de.gupta.commons.utility.math.algebra.element.ring.concrete;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public record IntegerEuclideanDomain(int value) implements EuclideanDomain<IntegerEuclideanDomain>
{
	@Override
	public IntegerEuclideanDomain negate()
	{
		return of(Math.negateExact(value));
	}

	public static IntegerEuclideanDomain of(final int value)
	{
		return new IntegerEuclideanDomain(value);
	}

	@Override
	public IntegerEuclideanDomain zero()
	{
		return of(0);
	}

	@Override
	public IntegerEuclideanDomain one()
	{
		return of(1);
	}

	@Override
	public IntegerEuclideanDomain add(final IntegerEuclideanDomain other)
	{
		return of(Math.addExact(value, other.value));
	}

	@Override
	public IntegerEuclideanDomain multiply(final IntegerEuclideanDomain other)
	{
		return of(Math.multiplyExact(value, other.value));
	}

	@Override
	public long norm()
	{
		return Math.absExact(value);
	}

	@Override
	public DivisionResult<IntegerEuclideanDomain> divideWithRemainder(final IntegerEuclideanDomain divisor)
	{
		return DivisionResult.of(of(Math.floorDiv(value, divisor.value)), of(Math.floorMod(value, divisor.value)));
	}

	@Override
	public boolean isZero()
	{
		return value == 0;
	}
}