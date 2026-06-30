package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface Radical<E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> convergence, final RoundingStrategy<E> rounding)
	{
		return root(2, convergence, rounding);
	}

	default E root(final int n, final ApproximationStrategy<E> convergence, final RoundingStrategy<E> rounding)
	{
		return NewtonsMethod.root(self(), n, convergence, rounding);
//		if (n < 2) throw new IllegalArgumentException("Root degree must be at least 2, got: " + n);
//		if (self().equals(self().zero())) return self().zero();
//		if (self().equals(self().one())) return self().one();
//		E current = self();
//		while (true)
//		{
//			final E xPow = current.multiplyAll(Collections.nCopies(n - 2, current));
//			final E next = current.power(n - 1)
//			                      .add(self().elementQuotient(xPow))
//			                      .divide((long) n, rounding).quotient();
//			if (convergence.converged(self(), current, next)) return next;
//			current = next;
//		}
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	E elementQuotient(final E divisor);
}