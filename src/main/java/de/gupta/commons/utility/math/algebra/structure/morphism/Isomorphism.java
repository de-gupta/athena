package de.gupta.commons.utility.math.algebra.structure.morphism;

public interface Isomorphism<S, T> extends Homomorphism<S, T>
{
	Homomorphism<T, S> inverse();

	default S applyInverse(final T target)
	{
		return inverse().apply(target);
	}
}