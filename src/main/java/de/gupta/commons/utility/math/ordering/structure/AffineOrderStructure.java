package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.algebra.structure.affine.AffineSpaceStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.util.function.BiFunction;

public interface AffineOrderStructure<A, D> extends AffineSpaceStructure<A, D>,
		TotalOrderStructure<A>
{
	static <A, D> AffineOrderStructure<A, D> of(final TotalOrderStructure<A> order,
	                                            final BiFunction<A, A, D> between,
	                                            final BiFunction<A, D, A> translate)
	{
		return new AffineOrderStructure<>()
		{
			@Override
			public OrderRelation compare(final A left, final A right)
			{
				return order.compare(left, right);
			}

			@Override
			public D displacement(final A from, final A to)
			{
				return between.apply(from, to);
			}

			@Override
			public A translate(final A point, final D displacement)
			{
				return translate.apply(point, displacement);
			}
		};
	}
}