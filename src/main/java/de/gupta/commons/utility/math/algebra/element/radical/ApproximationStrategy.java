package de.gupta.commons.utility.math.algebra.element.radical;

@FunctionalInterface
public interface ApproximationStrategy<E>
{
	boolean converged(final E original, final E previous, final E current);
}