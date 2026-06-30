package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.function.UnaryOperator;

public interface Radical<E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> whenToStop, final RoundingStrategy<E> rounding)
	{
		return root(2, whenToStop, rounding);
	}

	default E root(final int degree, final ApproximationStrategy<E> whenToStop, final RoundingStrategy<E> rounding)
	{
		Unfolding.beckon(degree)
		         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + degree));

		return Unfolding.beckon(self())
		                .cleave()
		                .when(e -> e.equals(e.zero()), self().zero())
		                .when(e -> e.equals(e.one()), self().one())
		                .infuse(e -> iterate(e, estimator().estimate(e, degree, rounding), whenToStop));
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	private static <E> E iterate(final E start, final UnaryOperator<E> estimator,
	                             final ApproximationStrategy<E> whenToStop)
	{
		E current = start;
		while (true)
		{
			final E next = estimator.apply(current);
			if (whenToStop.converged(start, current, next)) return next;
			current = next;
		}
	}

	default Estimator<E> estimator()
	{
		return NewtonEstimator.instance();
	}

	E elementQuotient(final E divisor);
}