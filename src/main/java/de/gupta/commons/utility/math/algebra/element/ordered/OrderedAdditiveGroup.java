package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.ordering.element.AffinelyOrdered;

public interface OrderedAdditiveGroup<E extends OrderedAdditiveGroup<E>>
		extends AdditiveAbelianGroup<E>, AffinelyOrdered<E, E>
{
	@Override
	default E displacementTo(final E other)
	{
		return other.subtract(self());
	}

	@Override
	default E translate(final E displacement)
	{
		return add(displacement);
	}

	default boolean isPositive()
	{
		return compare(zero()).isGreaterThan();
	}

	default boolean isNegative()
	{
		return compare(zero()).isLessThan();
	}

	default boolean isNonPositive()
	{
		return compare(zero()).isLessThanOrEqualTo();
	}

	default E abs()
	{
		return isNonNegative() ? self() : negate();
	}

	default E positivePart()
	{
		return isNonNegative() ? self() : zero();
	}

	default E negativePart()
	{
		return isNonPositive() ? negate() : zero();
	}

	default boolean isNonNegative()
	{
		return compare(zero()).isGreaterThanOrEqualTo();
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	default int signum()
	{
		return compare(zero()).signum();
	}
}
