package de.gupta.commons.utility.math.ordering;

import de.gupta.commons.utility.comparison.ComparisonResult;

import java.util.Optional;

public enum OrderRelation
{
	LESS_THAN,
	EQUAL,
	GREATER_THAN,
	INCOMPARABLE;

	public static OrderRelation from(final ComparisonResult result)
	{
		return switch (result)
		{
			case LESS_THAN -> LESS_THAN;
			case EQUAL -> EQUAL;
			case GREATER_THAN -> GREATER_THAN;
		};
	}

	public boolean isComparable()
	{
		return this != INCOMPARABLE;
	}

	public boolean isIncomparable()
	{
		return this == INCOMPARABLE;
	}

	public Optional<ComparisonResult> toComparisonResult()
	{
		return switch (this)
		{
			case LESS_THAN -> Optional.of(ComparisonResult.LESS_THAN);
			case EQUAL -> Optional.of(ComparisonResult.EQUAL);
			case GREATER_THAN -> Optional.of(ComparisonResult.GREATER_THAN);
			case INCOMPARABLE -> Optional.empty();
		};
	}

	public boolean isLessThan()
	{
		return this == LESS_THAN;
	}

	public boolean isLessThanOrEqualTo()
	{
		return this == LESS_THAN || this == EQUAL;
	}

	public boolean isEqualTo()
	{
		return this == EQUAL;
	}

	public boolean isGreaterThan()
	{
		return this == GREATER_THAN;
	}

	public boolean isGreaterThanOrEqualTo()
	{
		return this == GREATER_THAN || this == EQUAL;
	}
}