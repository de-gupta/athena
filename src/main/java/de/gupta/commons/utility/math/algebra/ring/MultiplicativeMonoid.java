package de.gupta.commons.utility.math.algebra.ring;

public interface MultiplicativeMonoid<E>
{
	E identity();

	E multiply(E other);
}