package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.math.ordering.OrderRelation;

public enum IntegerNaturalOrder implements TotalOrderStructure<Integer>
{
	INSTANCE;

	@Override
	public OrderRelation compare(final Integer left, final Integer right)
	{
		return Unfolding.beckon(left)
		                .trifurcate(l -> l.compareTo(right), OrderRelation.LESS_THAN, OrderRelation.EQUAL,
								OrderRelation.GREATER_THAN);
	}
}