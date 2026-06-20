package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;

@FunctionalInterface
public interface PartialOrderStructure<E>
{
	OrderRelation compare(E left, E right);

	default boolean leq(E left, E right)
	{
		return compare(left, right).isLessThanOrEqualTo();
	}

	default boolean lt(E left, E right)
	{
		return compare(left, right).isLessThan();
	}

	default boolean geq(E left, E right)
	{
		return compare(left, right).isGreaterThanOrEqualTo();
	}

	default boolean gt(E left, E right)
	{
		return compare(left, right).isGreaterThan();
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