package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.function.UnaryOperator;

public final class EstimatorBasedRoot
{
	public static <E extends Ring<E>> E compute(final E element, final int degree, final Estimator<E> estimator,
	                                            final ApproximationStrategy<E> whenToStop,
	                                            final RoundingStrategy<E> rounding)
	{
		return doCompute(element, degree, element.zero(), element.one(), element, estimator, whenToStop, rounding);
	}

	private static <E> E doCompute(final E element, final int degree, final E zero, final E one,
	                               final E initialEstimate, final Estimator<E> estimator,
	                               final ApproximationStrategy<E> whenToStop,
	                               final RoundingStrategy<E> rounding)
	{
		Unfolding.beckon(degree)
		         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + degree));

		return Unfolding.beckon(element)
		                .cleave()
		                .when(e -> e.equals(zero), zero)
		                .when(e -> e.equals(one), one)
		                .infuse(e -> iterate(initialEstimate, estimator.estimate(e, degree, rounding), whenToStop));
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

	public static <E extends Ring<E>> E compute(final E element, final int degree, final E initialEstimate,
	                                            final Estimator<E> estimator,
	                                            final ApproximationStrategy<E> whenToStop,
	                                            final RoundingStrategy<E> rounding)
	{
		return doCompute(element, degree, element.zero(), element.one(), initialEstimate, estimator, whenToStop,
				rounding);
	}

	public static <E> E compute(final E element, final int degree, final E zero, final E one,
	                            final Estimator<E> estimator,
	                            final ApproximationStrategy<E> whenToStop,
	                            final RoundingStrategy<E> rounding)
	{
		return doCompute(element, degree, zero, one, element, estimator, whenToStop, rounding);
	}

	private EstimatorBasedRoot()
	{
	}
}