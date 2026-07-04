package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.DivisionConvention;

public interface Radical<E extends Radical<E>>
{
	default E squareRoot(final ApproximationStrategy<E> whenToStop, final DivisionConvention<E> rounding)
	{
		return root(2, whenToStop, rounding);
	}

	E root(int degree, ApproximationStrategy<E> whenToStop, DivisionConvention<E> rounding);
}