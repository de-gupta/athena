package de.gupta.commons.utility.math.algebra.element.ring;

public interface Field<E extends Field<E>> extends CommutativeRing<E>
{
	E reciprocal();

	default E divide(final E other)
	{
		return multiply(other.reciprocal());
	}
}
