package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.DivisionConvention;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;

@FunctionalInterface
public interface RadicalStructure<E>
{
	default E squareRoot(final E element, final ApproximationStrategy<E> whenToStop,
	                     final DivisionConvention<E> convention)
	{
		return root(element, 2, whenToStop, convention);
	}

	E root(E element, int degree, ApproximationStrategy<E> whenToStop, DivisionConvention<E> convention);
}