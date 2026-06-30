package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

import java.util.function.UnaryOperator;

public final class RadicalStructureFactory
{
	public static <E extends Ring<E>> RadicalStructure<E> using(final Estimator<E> estimator)
	{
		return (element, degree, whenToStop, rounding) -> compute(element, degree, element.zero(),
				element.one(), estimator, whenToStop, rounding);
	}

	private static <E> E compute(final E radicand, final int degree, final E zero, final E one,
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
		                .infuse(e -> iterate(e, estimator.estimate(e, degree, rounding), whenToStop));
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

	public static <E> RadicalStructure<E> using(final Estimator<E> estimator, final RingStructure<E> ringStructure)
	{
		return ((element, degree, whenToStop, rounding) -> compute(element, degree, ringStructure.zero(),
				ringStructure.one(), estimator, whenToStop, rounding));
	}
}