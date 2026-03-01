package de.gupta.commons.utility.math.algebra.algebraicGroup;

public interface Ring<E>
{
	E additiveIdentity();

	E multiplicativeIdentity();

	E additiveInverse();

	E add(E other);

	E multiply(E other);

	E subtract(E other);
}