package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;

public interface Field<E extends Field<E>> extends CommutativeRing<E>, Algebra<E, E>
{
	default E divide(final E other)
	{
		return multiply(other.reciprocal());
	}

	E reciprocal();

	@Override
	default E embed(final E scalar)
	{
		return scalar;
	}
}