package de.gupta.commons.utility.math.algebra.algebraicGroup;

public interface AdditiveGroup<E>
{
	E zero();

	E add(E other);

	E negative();
}