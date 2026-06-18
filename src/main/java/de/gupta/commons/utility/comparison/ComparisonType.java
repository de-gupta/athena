package de.gupta.commons.utility.comparison;

import java.util.Comparator;

public enum ComparisonType
{
	LESS_THAN
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result == ComparisonResult.LESS_THAN;
				}
			},
	LESS_THAN_OR_EQUAL
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result == ComparisonResult.LESS_THAN || result == ComparisonResult.EQUAL;
				}
			},
	EQUAL
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result == ComparisonResult.EQUAL;
				}
			},
	GREATER_THAN_OR_EQUAL
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result == ComparisonResult.GREATER_THAN || result == ComparisonResult.EQUAL;
				}
			},
	GREATER_THAN
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result == ComparisonResult.GREATER_THAN;
				}
			},
	NOT_EQUAL
			{
				@Override
				public boolean isCompatibleWith(final ComparisonResult result)
				{
					return result != ComparisonResult.EQUAL;
				}
			};

	public <T> boolean compare(final T value, final T threshold, final Comparator<T> comparator)
	{
		return isCompatibleWith(ComparisonResult.fromComparatorResult(comparator.compare(value, threshold)));
	}

	public abstract boolean isCompatibleWith(final ComparisonResult result);
}