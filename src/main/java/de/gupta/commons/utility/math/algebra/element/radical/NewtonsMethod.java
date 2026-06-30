package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Collections;

public final class NewtonsMethod
{
	public static <E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
	E root(final E element, final int root, final ApproximationStrategy<E> convergence,
	       final RoundingStrategy<E> rounding)
	{
		Unfolding.beckon(root)
		         .interdict(r -> r < 2, ExceptionHelper.iaeFrom("Root degree must be at least 2, got: " + root));

		return Unfolding.beckon(element)
		                .cleave()
		                .when(e -> e.equals(e.zero()), element.zero())
		                .when(e -> e.equals(e.one()), element.one())
		                .infuse(e -> newton(e, root, convergence, rounding));
	}

	private static <E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>> E newton(final E element, final int root,
	                                                                                    final ApproximationStrategy<E> convergence,
	                                                                                    final RoundingStrategy<E> rounding)
	{
		E current = element;
		while (true)
		{
			final E xPow = current.multiplyAll(Collections.nCopies(root - 2, current));
			final E next = current.power(root - 1)
			                      .add(element.elementQuotient(xPow))
			                      .divide((long) root, rounding).quotient();
			if (convergence.converged(element, current, next)) return next;
			current = next;
		}
	}
}