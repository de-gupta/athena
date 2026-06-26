package de.gupta.commons.utility.math.algebra.element.tensor;

@FunctionalInterface
public interface ProjectionPolicy<S, E>
{
	E project(final LinearCombination<S, E> linearCombination);
}