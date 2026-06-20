package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;

@FunctionalInterface
public interface PartiallyOrdered<E extends PartiallyOrdered<E>>
{
	default boolean isEqualTo(E other)
	{
		return compare(other).isEqualTo();
	}

	OrderRelation compare(E other);

	default boolean leq(E other)
	{
		return isLessThanOrEqualTo(other);
	}

	default boolean isLessThanOrEqualTo(E other)
	{
		return compare(other).isLessThanOrEqualTo();
	}

	default boolean lt(E other)
	{
		return isLessThan(other);
	}

	default boolean isLessThan(E other)
	{
		return compare(other).isLessThan();
	}

	default boolean geq(E other)
	{
		return isGreaterThanOrEqualTo(other);
	}

	default boolean isGreaterThanOrEqualTo(E other)
	{
		return compare(other).isGreaterThanOrEqualTo();
	}

	default boolean gt(E other)
	{
		return isGreaterThan(other);
	}

	default boolean isGreaterThan(E other)
	{
		return compare(other).isGreaterThan();
	}

	default boolean isComparableTo(E other)
	{
		return compare(other).isComparable();
	}

	default boolean isIncomparableTo(E other)
	{
		return compare(other).isIncomparable();
	}
}