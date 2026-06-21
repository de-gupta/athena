package de.gupta.commons.utility.math.ordering.element.interval.bound;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

public final class Bounds
{
	public static <F extends TotallyOrdered<F>> boolean areEqual(final Bound<F> first, final Bound<F> second)
	{
		return first == second || ((areBothOpen(first, second) || areBothClosed(first, second)) && areValuesEqual(first,
				second));
	}

	public static <F extends TotallyOrdered<F>> boolean areBothOpen(final Bound<F> first, final Bound<F> second)
	{
		return first.isOpen() && second.isOpen();
	}

	public static <F extends TotallyOrdered<F>> boolean areBothClosed(final Bound<F> first, final Bound<F> second)
	{
		return first.isClosed() && second.isClosed();
	}

	public static <F extends TotallyOrdered<F>> boolean areValuesEqual(final Bound<F> first, final Bound<F> second)
	{
		return first.value().compare(second.value()).isEqualTo();
	}

	private Bounds()
	{
	}
}