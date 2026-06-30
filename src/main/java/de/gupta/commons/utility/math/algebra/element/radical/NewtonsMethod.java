package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Collections;
import java.util.function.UnaryOperator;

final class NewtonsMethod
{
	static <E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
	E root(final E element, final int degree, final ApproximationStrategy<E> whenToStop,
	       final RoundingStrategy<E> rounding)
	{
		Unfolding.beckon(degree)
		         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + degree));

		return Unfolding.beckon(element)
		                .cleave()
		                .when(e -> e.equals(e.zero()), element.zero())
		                .when(e -> e.equals(e.one()), element.one())
		                .infuse(e -> iterate(e, newtonStep(e, degree, rounding), whenToStop));
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

	private static <E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
	UnaryOperator<E> newtonStep(final E radicand, final int degree, final RoundingStrategy<E> rounding)
	{
		return estimate ->
				estimate.power(degree - 1)
				        .add(radicand.elementQuotient(estimate.multiplyAll(Collections.nCopies(degree - 2, estimate))))
				        .divide((long) degree, rounding).quotient();
	}

	private NewtonsMethod()
	{
	}
}