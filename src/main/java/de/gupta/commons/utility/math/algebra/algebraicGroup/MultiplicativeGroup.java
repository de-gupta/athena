package de.gupta.commons.utility.math.algebra.algebraicGroup;

public interface MultiplicativeGroup<E>
{
	E identity();

	E inverse();

	E multiply(E other);

	E divide(E other);
}