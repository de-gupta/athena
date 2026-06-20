package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Incomparable;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.Ordering;

@FunctionalInterface
public interface PartiallyOrdered<E extends PartiallyOrdered<E>>
{
	default boolean isEqualTo(E other)
	{
		return switch (compare(other))
		{
			case OrderRelation r -> r.isEqualTo();
			case Incomparable ignored -> false;
		};
	}

	Ordering compare(E other);

	default boolean leq(E other)
	{
		return isLessThanOrEqualTo(other);
	}

	default boolean isLessThanOrEqualTo(E other)
	{
		return switch (compare(other))
		{
			case OrderRelation r -> r.isLessThanOrEqualTo();
			case Incomparable _ -> false;
		};
	}

	default boolean lt(E other)
	{
		return isLessThan(other);
	}

	default boolean isLessThan(E other)
	{
		return switch (compare(other))
		{
			case OrderRelation r -> r.isLessThan();
			case Incomparable _ -> false;
		};
	}

	default boolean geq(E other)
	{
		return isGreaterThanOrEqualTo(other);
	}

	default boolean isGreaterThanOrEqualTo(E other)
	{
		return switch (compare(other))
		{
			case OrderRelation r -> r.isGreaterThanOrEqualTo();
			case Incomparable _ -> false;
		};
	}

	default boolean gt(E other)
	{
		return isGreaterThan(other);
	}

	default boolean isGreaterThan(E other)
	{
		return switch (compare(other))
		{
			case OrderRelation r -> r.isGreaterThan();
			case Incomparable _ -> false;
		};
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