package de.gupta.commons.utility.math.algebra.element.binary;

import java.util.stream.IntStream;

public interface Group<E extends Group<E>> extends Monoid<E>
{
	E inverse();

	default E divide(final E other)
	{
		return multiply(other.inverse());
	}

	default E power(final int exponent)
	{
		return exponent == 0 ? identity() :
				exponent < 0 ? self().inverse().power(-exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> self())
								 .reduce(self(), Group::multiply);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}