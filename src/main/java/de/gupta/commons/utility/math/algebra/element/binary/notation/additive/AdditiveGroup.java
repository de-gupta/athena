package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import java.util.stream.IntStream;

public interface AdditiveGroup<E extends AdditiveGroup<E>> extends AdditiveMonoid<E>
{
	E negate();

	default E subtract(final E other)
	{
		return add(other.negate());
	}

	default E power(final int exponent)
	{
		return exponent == 0 ? zero() :
				exponent < 0 ? negate().power(-exponent) :
						IntStream.range(0, exponent - 1)
								 .mapToObj(_ -> self())
								 .reduce(self(), AdditiveGroup::add);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}