package de.gupta.commons.utility.math.algebra.element.ring.concrete;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public record IntegerRing(int value) implements EuclideanDomain<IntegerRing>
{
	@Override
	public IntegerRing negate()
	{
		return of(Math.negateExact(value));
	}

	public static IntegerRing of(final int value)
	{
		return new IntegerRing(value);
	}

	@Override
	public IntegerRing zero()
	{
		return of(0);
	}

	@Override
	public IntegerRing one()
	{
		return of(1);
	}

	@Override
	public IntegerRing add(final IntegerRing other)
	{
		return of(Math.addExact(value, other.value));
	}

	@Override
	public IntegerRing multiply(final IntegerRing other)
	{
		return of(Math.multiplyExact(value, other.value));
	}

	@Override
	public DivisionResult<IntegerRing> divideWithRemainder(final IntegerRing divisor)
	{
		return DivisionResult.of(of(Math.floorDiv(value, divisor.value)), of(Math.floorMod(value, divisor.value)));
	}

	@Override
	public long norm()
	{
		return Math.absExact(value);
	}

	@Override
	public boolean isZero()
	{
		return value == 0;
	}
}