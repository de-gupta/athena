package de.gupta.commons.utility.math.algebra.element.binary;

public interface Monoid<E extends Monoid<E>> extends Semigroup<E>
{
	E identity();
}