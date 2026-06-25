package de.gupta.commons.utility.math.algebra.element.algebra;

@FunctionalInterface
public interface ProjectionPolicy<F, E>
{
	E project(F accumulated);
}
