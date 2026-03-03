package de.gupta.commons.utility.math.algebra.ring;

public record DivisionResult<E>(E quotient, E remainder)
{
	public static <E> DivisionResult<E> of(E quotient, E remainder)
	{
		return new DivisionResult<>(quotient, remainder);
	}
}