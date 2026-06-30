package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface Radical<E extends Ring<E> & ScalarDivisible<E, Long> & Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> whenToStop, final RoundingStrategy<E> rounding)
	{
		return root(2, whenToStop, rounding);
	}

	default E root(final int degree, final ApproximationStrategy<E> whenToStop, final RoundingStrategy<E> rounding)
	{
		return NewtonsMethod.root(self(), degree, whenToStop, rounding);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	E elementQuotient(final E divisor);
}