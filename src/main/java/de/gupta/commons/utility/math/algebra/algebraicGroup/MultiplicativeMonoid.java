package de.gupta.commons.utility.math.algebra.algebraicGroup;

public interface MultiplicativeMonoid<E>
{
	E identity();

	E multiply(E other);
}