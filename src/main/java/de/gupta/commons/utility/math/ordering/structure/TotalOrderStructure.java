package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.comparison.DescriptivelyComparableStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public interface TotalOrderStructure<E> extends PartialOrderStructure<E>
{
	default DescriptivelyComparableStructure<E> asDescriptivelyComparableStructure()
	{
		return (left, right) -> compare(left, right).toComparisonResult();
	}

	@Override
	OrderRelation compare(E left, E right);
}