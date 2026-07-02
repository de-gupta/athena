package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;

import java.util.function.UnaryOperator;

public final class RationalNumberEstimators
{
	private static final long SCALE = 1_000_000_000L;

	public static RationalNumber initialEstimate(final RationalNumber radicand, final int degree)
	{
		final long num = Math.abs(radicand.numerator().value());
		final long den = Math.abs(radicand.denominator().value());
		final int numBits = Long.SIZE - Long.numberOfLeadingZeros(num);
		final int denBits = Long.SIZE - Long.numberOfLeadingZeros(den);
		final long estNum = Math.max(1L, 1L << Math.max(0, (numBits - 1) / degree));
		final long estDen = Math.max(1L, 1L << Math.max(0, (denBits - 1) / degree));
		return RationalNumberFactory.of(estNum, estDen);
	}

	public static Estimator<RationalNumber> adaptEstimator(final Estimator<RationalNumber> estimator)
	{
		return (radicand, degree, rounding) ->
		{
			final UnaryOperator<RationalNumber> step = estimator.estimate(radicand, degree, rounding);
			final double radicandD = toDouble(radicand);
			return current ->
			{
				try
				{
					return step.apply(current);
				}
				catch (final ArithmeticException ignored)
				{
					final double curr = toDouble(current);
					final double next = (curr * (degree - 1) + radicandD / Math.pow(curr, degree - 1)) / degree;
					return RationalNumberFactory.of(Math.round(next * SCALE), SCALE);
				}
			};
		};
	}

	private static double toDouble(final RationalNumber r)
	{
		return (double) r.numerator().value() / r.denominator().value();
	}

	public static ApproximationStrategy<RationalNumber> adapt(final ApproximationStrategy<RationalNumber> strategy)
	{
		return (original, prev, curr) ->
		{
			try
			{
				return strategy.converged(original, prev, curr);
			}
			catch (final ArithmeticException ignored)
			{
				return true;
			}
		};
	}

	private RationalNumberEstimators()
	{
	}
}