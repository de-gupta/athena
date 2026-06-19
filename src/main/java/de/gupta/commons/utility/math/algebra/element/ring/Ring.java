package de.gupta.commons.utility.math.algebra.element.ring;

public interface Ring<E extends Ring<E>> extends Semiring<E>
{
	default E subtract(final E other)
	{
		return add(other.negate());
	}

	E negate();
}