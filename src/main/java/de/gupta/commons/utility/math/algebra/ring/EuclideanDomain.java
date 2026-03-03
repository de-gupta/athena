package de.gupta.commons.utility.math.algebra.ring;

public interface EuclideanDomain<E> extends Ring<E>
{
	DivisionResult<E> divide(E other);
}