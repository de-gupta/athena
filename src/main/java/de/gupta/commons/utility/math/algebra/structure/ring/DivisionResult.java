package de.gupta.commons.utility.math.algebra.structure.ring;

public record DivisionResult<E>(E quotient, E remainder)
{
	public static <E> DivisionResult<E> of(final E quotient, final E remainder)
	{
		return new DivisionResult<>(quotient, remainder);
	}
}