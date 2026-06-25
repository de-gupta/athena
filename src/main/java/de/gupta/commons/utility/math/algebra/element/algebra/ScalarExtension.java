package de.gupta.commons.utility.math.algebra.element.algebra;

@FunctionalInterface
public interface ScalarExtension<E, F>
{
	F embed(E element);

	default E project(final F accumulated, final ProjectionPolicy<F, E> policy)
	{
		return policy.project(accumulated);
	}
}
