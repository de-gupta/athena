package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public interface TotallyOrdered<E extends TotallyOrdered<E>> extends PartiallyOrdered<E>
{
	default ComparisonResult toComparisonResult(E other)
	{
		return compare(other).toComparisonResult();
	}

	@Override
	OrderRelation compare(E other);
}