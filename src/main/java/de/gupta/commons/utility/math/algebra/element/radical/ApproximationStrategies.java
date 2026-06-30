package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;

public final class ApproximationStrategies
{
	public static <E extends OrderedAdditiveGroup<E>> ApproximationStrategy<E> withinTolerance(final E tolerance)
	{
		return (_, prev, curr) -> prev.subtract(curr).abs().compare(tolerance).isLessThanOrEqualTo();
	}

	public static <E> ApproximationStrategy<E> byEquality()
	{
		return (_, prev, curr) -> prev.equals(curr);
	}

	private ApproximationStrategies()
	{
	}
}