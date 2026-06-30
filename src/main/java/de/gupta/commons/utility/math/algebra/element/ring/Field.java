package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;

public interface Field<E extends Field<E>> extends CommutativeRing<E>, Algebra<E, E>, Quotientable<E>
{
	@Override
	default E quotient(final E divisor)
	{
		return divide(divisor);
	}

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