package de.gupta.commons.utility.math.ordering;

public sealed interface Ordering permits OrderRelation, Incomparable
{
	default boolean isIncomparable()
	{
		return !isComparable();
	}

	boolean isComparable();
}