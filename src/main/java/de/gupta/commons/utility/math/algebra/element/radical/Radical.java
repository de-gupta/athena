package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;

public interface Radical<E extends Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> convergence, final RoundingStrategy<E> rounding)
	{
		return root(2, convergence, rounding);
	}

	E root(final int n, final ApproximationStrategy<E> convergence, final RoundingStrategy<E> rounding);
}