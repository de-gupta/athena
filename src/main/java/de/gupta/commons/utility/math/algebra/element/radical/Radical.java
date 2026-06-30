package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;

public interface Radical<E extends Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> whenToStop, final RoundingStrategy<E> rounding)
	{
		return root(2, whenToStop, rounding);
	}

	E root(int degree, ApproximationStrategy<E> whenToStop, RoundingStrategy<E> rounding);
}