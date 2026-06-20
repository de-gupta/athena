package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.Incomparable;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.Ordering;

@FunctionalInterface
public interface PartialOrderStructure<E>
{
	default boolean leq(E left, E right)
	{
		return switch (compare(left, right))
		{
			case OrderRelation r -> r.isLessThanOrEqualTo();
			case Incomparable ignored -> false;
		};
	}

	Ordering compare(E left, E right);

	default boolean lt(E left, E right)
	{
		return switch (compare(left, right))
		{
			case OrderRelation r -> r.isLessThan();
			case Incomparable ignored -> false;
		};
	}

	default boolean geq(E left, E right)
	{
		return switch (compare(left, right))
		{
			case OrderRelation r -> r.isGreaterThanOrEqualTo();
			case Incomparable ignored -> false;
		};
	}

	default boolean gt(E left, E right)
	{
		return switch (compare(left, right))
		{
			case OrderRelation r -> r.isGreaterThan();
			case Incomparable ignored -> false;
		};
	}

	default boolean isComparable(E left, E right)
	{
		return compare(left, right).isComparable();
	}

	default boolean isIncomparable(E left, E right)
	{
		return compare(left, right).isIncomparable();
	}
}