package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.util.function.BiFunction;

public interface AffineOrderStructure<E, D> extends TotalOrderStructure<E>
{
	static <E, D> AffineOrderStructure<E, D> of(final TotalOrderStructure<E> order,
	                                            final BiFunction<E, E, D> between,
	                                            final BiFunction<E, D, E> translate)
	{
		return new AffineOrderStructure<>()
		{
			@Override
			public OrderRelation compare(final E left, final E right)
			{
				return order.compare(left, right);
			}

			@Override
			public D between(final E from, final E to)
			{
				return between.apply(from, to);
			}

			@Override
			public E translate(final E point, final D displacement)
			{
				return translate.apply(point, displacement);
			}
		};
	}

	D between(E from, E to);

	E translate(E point, D displacement);
}