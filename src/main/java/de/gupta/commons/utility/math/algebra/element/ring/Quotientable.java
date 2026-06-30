package de.gupta.commons.utility.math.algebra.element.ring;

public interface Quotientable<E extends Quotientable<E>>
{
	E quotient(final E divisor);
}