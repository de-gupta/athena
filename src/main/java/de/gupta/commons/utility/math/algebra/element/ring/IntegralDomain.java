package de.gupta.commons.utility.math.algebra.element.ring;

public interface IntegralDomain<E extends IntegralDomain<E>> extends CommutativeRing<E>
{
	boolean isZero();
}
