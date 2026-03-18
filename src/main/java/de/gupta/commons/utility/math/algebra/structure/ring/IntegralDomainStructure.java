package de.gupta.commons.utility.math.algebra.structure.ring;

public interface IntegralDomainStructure<E> extends CommutativeRingStructure<E>
{
	boolean isZero(E element);
}
