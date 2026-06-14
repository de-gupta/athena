package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.comparison.DescriptivelyComparableStructure;

public interface TotalOrderStructure<E> extends PartialOrderStructure<E>
{
	default DescriptivelyComparableStructure<E> asDescriptivelyComparableStructure()
	{
		return (left, right) -> compare(left, right).toComparisonResult()
		                                            .orElseThrow(() -> new IllegalStateException(
															"Total order may not produce an incomparable result."));
	}
}