package de.gupta.commons.utility.math.algebra.ring;

public interface Field<E>
{
	E additiveIdentity();

	E multiplicativeIdentity();

	E additiveInverse();

	E multiplicativeInverse();

	E add(E other);

	E multiply(E other);

	E subtract(E other);

	E divide(E other);
}