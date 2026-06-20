package de.gupta.commons.utility.comparison;

import java.util.Comparator;
import java.util.Optional;

public final class ComparisonUtility
{
	public static <T> boolean doesThisValueSatisfyTheComparison(final T value, final T reference,
																final ComparisonType comparison,
																final Comparator<T> comparator
	)
	{
		return Optional.ofNullable(value)
					   .filter(_ -> reference != null)
					   .map(v -> comparison.compare(v, reference, comparator))
					   .orElse(false);
	}

	private ComparisonUtility()
	{
	}
}