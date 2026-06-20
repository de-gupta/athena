package de.gupta.commons.utility.comparison;

import java.util.Comparator;
import java.util.Set;

public enum ComparisonType
{
	LESS_THAN(Set.of(ComparisonResult.LESS_THAN)),
	LESS_THAN_OR_EQUAL(Set.of(ComparisonResult.LESS_THAN, ComparisonResult.EQUAL)),
	EQUAL(Set.of(ComparisonResult.EQUAL)),
	GREATER_THAN_OR_EQUAL(Set.of(ComparisonResult.GREATER_THAN, ComparisonResult.EQUAL)),
	GREATER_THAN(Set.of(ComparisonResult.GREATER_THAN)),
	NOT_EQUAL(Set.of(ComparisonResult.LESS_THAN, ComparisonResult.GREATER_THAN));

	private final Set<ComparisonResult> compatibleResults;

	public <T> boolean compare(final T value, final T threshold, final Comparator<T> comparator)
	{
		return isCompatibleWith(ComparisonResult.fromComparatorResult(comparator.compare(value, threshold)));
	}

	public boolean isCompatibleWith(final ComparisonResult result)
	{
		return compatibleResults.contains(result);
	}

	ComparisonType(final Set<ComparisonResult> compatibleResults)
	{
		this.compatibleResults = compatibleResults;
	}
}