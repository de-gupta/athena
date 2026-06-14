package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;

@FunctionalInterface
public interface PartiallyOrdered<E extends PartiallyOrdered<E>>
{
	OrderRelation compare(E other);

	default boolean leq(E other)
	{
		return compare(other).isLessThanOrEqualTo();
	}

	default boolean lt(E other)
	{
		return compare(other).isLessThan();
	}

	default boolean geq(E other)
	{
		return compare(other).isGreaterThanOrEqualTo();
	}

	default boolean gt(E other)
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