package de.gupta.commons.utility.comparison;

@FunctionalInterface
public interface DescriptivelyComparable<T>
{
	ComparisonResult compare(T other);

	default boolean isEqualTo(T other)
	{
		return compare(other) == ComparisonResult.EQUAL;
	}

	default boolean isNotEqualTo(T other)
	{
		return !isEqualTo(other);
	}

	default boolean isLessThan(T other)
	{
		return compare(other) == ComparisonResult.LESS_THAN;
	}

	default boolean isGreaterThan(T other)
	{
		return compare(other) == ComparisonResult.GREATER_THAN;
	}

	default boolean isLessThanOrEqualTo(T other)
	{
		return compare(other) == ComparisonResult.LESS_THAN_OR_EQUAL;
	}

	default boolean isGreaterThanOrEqualTo(T other)
	{
		return compare(other) == ComparisonResult.GREATER_THAN_OR_EQUAL;
	}

	default boolean isNotLessThan(T other)
	{
		return !isLessThan(other);
	}

	default boolean isNotGreaterThan(T other)
	{
		return !isGreaterThan(other);
	}

	default boolean isNotLessThanOrEqualTo(T other)
	{
		return !isLessThanOrEqualTo(other);
	}

	default boolean isNotGreaterThanOrEqualTo(T other)
	{
		return !isGreaterThanOrEqualTo(other);
	}
}