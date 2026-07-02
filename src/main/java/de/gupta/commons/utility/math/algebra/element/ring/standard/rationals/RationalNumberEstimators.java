package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.aletheia.trials.Fallible;
import de.gupta.aletheia.trials.Portent;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;

import java.util.List;
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
					Fallible.beckon(current)
					        .metamorphose(step::apply, List.of(Portent.on(ArithmeticException.class,
							        _ -> doubleStep(radicandD, degree, current))))
					        .summon();
		};
	}

	private static double toDouble(final RationalNumber r)
	{
		return (double) r.numerator().value() / r.denominator().value();
	}

	private static RationalNumber doubleStep(final double radicandD, final int degree,
	                                         final RationalNumber current)
	{
		final double curr = toDouble(current);
		final double next = (curr * (degree - 1) + radicandD / Math.pow(curr, degree - 1)) / degree;
		return RationalNumberFactory.of(Math.round(next * SCALE), SCALE);
	}

	public static ApproximationStrategy<RationalNumber> adapt(final ApproximationStrategy<RationalNumber> strategy)
	{
		return (original, prev, curr) ->
				Fallible.beckon(strategy)
				        .metamorphose(s -> s.converged(original, prev, curr),
								List.of(Portent.on(ArithmeticException.class, _ -> Fallible.beckon(strategy)
						                                                                   .metamorphose(
								                                                                   s -> s.converged(
										                                                                   scaled(original),
										                                                                   scaled(prev),
										                                                                   scaled(curr)),
															 List.of(Portent.on(ArithmeticException.class, _ -> false)))
								                     .summon())))
				        .summon();
	}

	private static RationalNumber scaled(final RationalNumber r)
	{
		return RationalNumberFactory.of(Math.round(toDouble(r) * SCALE), SCALE);
	}

	private RationalNumberEstimators()
	{
	}
}