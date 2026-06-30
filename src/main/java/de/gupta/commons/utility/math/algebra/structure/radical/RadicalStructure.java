package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface RadicalStructure<E>
{
	static <E extends Ring<E>> RadicalStructure<E> using(final Estimator<E> estimator)
	{
		return (element, degree, whenToStop, rounding) ->
		{
			Unfolding.beckon(degree)
			         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + degree));

			return Unfolding.beckon(element)
			                .cleave()
			                .when(e -> e.equals(e.zero()), element.zero())
			                .when(e -> e.equals(e.one()), element.one())
			                .infuse(e -> iterate(e, estimator.estimate(e, degree, rounding), whenToStop));
		};
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

	default E squareRoot(final E element, final ApproximationStrategy<E> whenToStop,
	                     final RoundingStrategy<E> rounding)
	{
		return root(element, 2, whenToStop, rounding);
	}

	E root(E element, int degree, ApproximationStrategy<E> whenToStop, RoundingStrategy<E> rounding);
}