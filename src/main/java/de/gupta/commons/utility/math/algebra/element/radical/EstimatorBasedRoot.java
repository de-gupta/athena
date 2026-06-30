package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.function.UnaryOperator;

public final class EstimatorBasedRoot
{
	public static <E extends Ring<E>> E compute(final E radicand, final int degree, final Estimator<E> estimator,
	                                            final ApproximationStrategy<E> whenToStop,
	                                            final RoundingStrategy<E> rounding)
	{
		return compute(radicand, degree, radicand.zero(), radicand.one(), estimator, whenToStop, rounding);
	}

	public static <E> E compute(final E radicand, final int degree, final E zero, final E one,
	                            final Estimator<E> estimator,
	                            final ApproximationStrategy<E> whenToStop,
	                            final RoundingStrategy<E> rounding)
	{

		Unfolding.beckon(degree)
		         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + degree));

		return Unfolding.beckon(radicand)
		                .cleave()
		                .when(e -> e.equals(zero), zero)
		                .when(e -> e.equals(one), one)
		                .infuse(e -> iterate(estimator.initialEstimate(e, degree),
								estimator.estimate(e, degree, rounding), whenToStop));
	}

	private static <E> E iterate(final E start, final UnaryOperator<E> step,
	                             final ApproximationStrategy<E> whenToStop)
	{
		E current = start;
		while (true)
		{
			final E next = step.apply(current);
			if (whenToStop.converged(start, current, next)) return next;
			current = next;
		}
	}

	private EstimatorBasedRoot()
	{
	}
}