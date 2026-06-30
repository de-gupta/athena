package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

record IntegersAsEuclideanDomain(long value) implements IntegralNumber
{
	@Override
	public IntegralNumber negate()
	{
		return IntegralNumberFactory.of(Math.negateExact(value));
	}

	@Override
	public IntegralNumber add(final IntegralNumber other)
	{
		return IntegralNumberFactory.of(Math.addExact(value, other.value()));
	}

	@Override
	public Long norm()
	{
		return Math.absExact(value);
	}

	@Override
	public OrderRelation compare(final IntegralNumber other)
	{
		return OrderRelation.from(Long.compare(value, other.value()));
	}

	@Override
	public IntegralNumber elementQuotient(final IntegralNumber divisor)
	{
		return divideFloor(divisor).quotient();
	}

	@Override
	public boolean isZero()
	{
		return value == 0L;
	}

	@Override
	public IntegralNumber zero()
	{
		return IntegralNumberFactory.of(0L);
	}

	@Override
	public IntegralNumber one()
	{
		return IntegralNumberFactory.of(1L);
	}

	@Override
	public IntegralNumber multiply(final IntegralNumber other)
	{
		return IntegralNumberFactory.of(Math.multiplyExact(value, other.value()));
	}

	@Override
	public DivisionResult<IntegralNumber> divideFloor(final IntegralNumber divisor)
	{
		return DivisionResult.of(IntegralNumberFactory.of(Math.floorDiv(value, divisor.value())),
				IntegralNumberFactory.of(Math.floorMod(value, divisor.value())));
	}
}