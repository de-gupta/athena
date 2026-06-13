package de.gupta.commons.utility.comparison;

import java.util.Comparator;

@FunctionalInterface
public interface DescriptivelyComparableStructure<T>
{
	static <T> DescriptivelyComparableStructure<T> of(final Comparator<T> comparator)
	{
		return (left, right) ->
		{
			int result = comparator.compare(left, right);
			return result < 0 ? ComparisonResult.LESS_THAN
					: result > 0 ? ComparisonResult.GREATER_THAN
					  : ComparisonResult.EQUAL;
		};
	}

	ComparisonResult compare(T left, T right);

	default boolean isEqualTo(T left, T right)
	{
		return compare(left, right) == ComparisonResult.EQUAL;
	}

	default boolean isNotEqualTo(T left, T right)
	{
		return !isEqualTo(left, right);
	}

	default boolean isLessThan(T left, T right)
	{
		return compare(left, right) == ComparisonResult.LESS_THAN;
	}

	default boolean isGreaterThan(T left, T right)
	{
		return compare(left, right) == ComparisonResult.GREATER_THAN;
	}

	default boolean isLessThanOrEqualTo(T left, T right)
	{
		return compare(left, right) != ComparisonResult.GREATER_THAN;
	}

	default boolean isGreaterThanOrEqualTo(T left, T right)
	{
		return compare(left, right) != ComparisonResult.LESS_THAN;
	}

	default boolean isNotLessThan(T left, T right)
	{
		return !isLessThan(left, right);
	}

	default boolean isNotGreaterThan(T left, T right)
	{
		return !isGreaterThan(left, right);
	}

	default boolean isNotLessThanOrEqualTo(T left, T right)
	{
		return !isLessThanOrEqualTo(left, right);
	}

	default boolean isNotGreaterThanOrEqualTo(T left, T right)
	{
		return !isGreaterThanOrEqualTo(left, right);
	}
}