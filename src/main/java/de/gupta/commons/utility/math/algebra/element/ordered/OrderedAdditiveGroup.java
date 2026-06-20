package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

public interface OrderedAdditiveGroup<E extends OrderedAdditiveGroup<E>>
		extends AdditiveAbelianGroup<E>, TotallyOrdered<E>
{
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