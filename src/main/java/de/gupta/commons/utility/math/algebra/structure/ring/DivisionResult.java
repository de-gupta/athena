package de.gupta.commons.utility.math.algebra.structure.ring;

import java.util.function.Function;

public record DivisionResult<E>(E quotient, E remainder)
{
	public static <E> DivisionResult<E> of(final E quotient, final E remainder)
	{
		return new DivisionResult<>(quotient, remainder);
	}

	public <F> DivisionResult<F> map(final Function<E, F> mapper)
	{
		return new DivisionResult<>(mapper.apply(quotient), mapper.apply(remainder));
	}

	public <F> DivisionResult<F> map(final Function<E, F> quotientMapper, final Function<E, F> remainderMapper)
	{
		return new DivisionResult<>(quotientMapper.apply(quotient), remainderMapper.apply(remainder));
	}
}