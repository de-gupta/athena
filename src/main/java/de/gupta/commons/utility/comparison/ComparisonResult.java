package de.gupta.commons.utility.comparison;

public enum ComparisonResult
{
	LESS_THAN,
	EQUAL,
	GREATER_THAN;

	public static ComparisonResult fromComparatorResult(final int comparatorResult)
	{
		return comparatorResult < 0 ? LESS_THAN : comparatorResult > 0 ? GREATER_THAN : EQUAL;
	}
}