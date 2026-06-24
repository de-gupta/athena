package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.commons.utility.math.ordering.structure.AffineOrderStructure;

public interface OrderedAdditiveGroupStructure<E>
		extends AdditiveAbelianGroupStructure<E>, AffineOrderStructure<E, E>
{
	@Override
	default E displacement(final E from, final E to)
	{
		return subtract(to, from);
	}

	@Override
	default E translate(final E point, final E displacement)
	{
		return add(point, displacement);
	}

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