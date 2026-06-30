package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.aletheia.collection.Dyad;
import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.IntegralDomain;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.util.Collections;

record RationalNumberImpl(IntegralNumber numerator, IntegralNumber denominator) implements RationalNumber
{
	@Override
	public RationalNumber reciprocal()
	{
		return of(denominator, numerator);
	}

	static RationalNumber of(final IntegralNumber numerator, final IntegralNumber denominator)
	{
		return Unfolding.beckon(denominator)
		                .interdict(IntegralDomain::isZero, ExceptionHelper.iaeFrom("Denominator may not be zero"))
		                .metamorphose(_ -> normalize(numerator, denominator))
		                .coronate(pair -> new RationalNumberImpl(pair.first(), pair.second()));
	}

	private static Dyad<IntegralNumber, IntegralNumber> normalize(final IntegralNumber numerator,
	                                                              final IntegralNumber denominator)
	{
		final IntegralNumber gcd = numerator.abs().gcd(denominator.abs());

		var normalizedNumerator = numerator.divideFloor(gcd).quotient();
		var normalizedDenominator = denominator.divideFloor(gcd).quotient();

		if (normalizedDenominator.isNegative())
		{
			return Dyad.of(normalizedNumerator.negate(), normalizedDenominator.negate());
		}

		return Dyad.of(normalizedNumerator, normalizedDenominator);
	}

	@Override
	public OrderRelation compare(final RationalNumber other)
	{
		return numerator.multiply(other.denominator()).compare(other.numerator().multiply(denominator));
	}

	@Override
	public RationalNumber negate()
	{
		return of(numerator.negate(), denominator);
	}

	@Override
	public RationalNumber one()
	{
		return of(IntegralNumberFactory.of(1), IntegralNumberFactory.of(1));
	}

	@Override
	public RationalNumber multiply(final RationalNumber other)
	{
		return of(numerator.multiply(other.numerator()), denominator.multiply(other.denominator()));
	}

	@Override
	public RationalNumber add(final RationalNumber other)
	{
		return of(numerator.multiply(other.denominator()).add(denominator.multiply(other.numerator())),
				denominator.multiply(other.denominator()));
	}

	@Override
	public RationalNumber ratio(final RationalNumber denominator)
	{
		return divide(denominator);
	}

	@Override
	public DivisionResult<RationalNumber> divide(final Long scalar, final RoundingStrategy<RationalNumber> strategy)
	{
		return DivisionResult.of(divide(RationalNumberFactory.of(scalar, 1)), zero());
	}

	@Override
	public RationalNumber zero()
	{
		return of(IntegralNumberFactory.of(0), IntegralNumberFactory.of(1));
	}

	@Override
	public RationalNumber root(final int n, final ApproximationStrategy<RationalNumber> convergence,
	                           final RoundingStrategy<RationalNumber> rounding)
	{
		if (n < 2) throw new IllegalArgumentException("Root degree must be at least 2, got: " + n);
		if (equals(zero())) return zero();
		if (equals(one())) return one();
		RationalNumber current = this;
		while (true)
		{
			final RationalNumber xPow = current.multiplyAll(Collections.nCopies(n - 2, current));
			final RationalNumber next = current.power(n - 1)
			                                   .add(divide(xPow))
			                                   .divide((long) n, rounding).quotient();
			if (convergence.converged(this, current, next)) return next;
			current = next;
		}
	}
}