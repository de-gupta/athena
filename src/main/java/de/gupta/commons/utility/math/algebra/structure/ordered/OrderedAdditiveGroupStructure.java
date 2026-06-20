package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

public interface OrderedAdditiveGroupStructure<E>
		extends AdditiveAbelianGroupStructure<E>, TotalOrderStructure<E>
{
	default boolean isPositive(final E element)
	{
		return compare(element, zero()).isGreaterThan();
	}

	default boolean isNegative(final E element)
	{
		return compare(element, zero()).isLessThan();
	}

	default boolean isNonPositive(final E element)
	{
		return compare(element, zero()).isLessThanOrEqualTo();
	}

	default E abs(final E element)
	{
		return isNonNegative(element) ? element : negate(element);
	}

	default boolean isNonNegative(final E element)
	{
		return compare(element, zero()).isGreaterThanOrEqualTo();
	}

	default int signum(final E element)
	{
		return compare(element, zero()).signum();
	}
}