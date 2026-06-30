package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;

@FunctionalInterface
public interface RadicalStructure<E>
{
	default E squareRoot(final E element, final ApproximationStrategy<E> whenToStop,
	                     final RoundingStrategy<E> rounding)
	{
		return root(element, 2, whenToStop, rounding);
	}

	E root(E element, int degree, ApproximationStrategy<E> whenToStop, RoundingStrategy<E> rounding);
}