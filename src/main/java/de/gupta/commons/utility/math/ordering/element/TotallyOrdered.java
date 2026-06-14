package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.comparison.ComparisonResult;

public interface TotallyOrdered<E extends TotallyOrdered<E>> extends PartiallyOrdered<E>
{
	default ComparisonResult toComparisonResult(E other)
	{
		return compare(other).toComparisonResult()
		                     .orElseThrow(() -> new IllegalStateException(
									 "Total order may not produce an incomparable result."));
	}
}