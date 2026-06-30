package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.util.Collections;

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
	public IntegralNumber root(final int n, final ApproximationStrategy<IntegralNumber> convergence,
	                           final RoundingStrategy<IntegralNumber> rounding)
	{
		if (n < 2) throw new IllegalArgumentException("Root degree must be at least 2, got: " + n);
		if (isZero()) return zero();
		if (equals(one())) return one();
		IntegralNumber current = this;
		while (true)
		{
			final IntegralNumber xPow = current.multiplyAll(Collections.nCopies(n - 2, current));
			final IntegralNumber next = current.power(n - 1)
			                                   .add(divideFloor(xPow).quotient())
			                                   .divide((long) n, rounding).quotient();
			if (convergence.converged(this, current, next)) return next;
			current = next;
		}
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