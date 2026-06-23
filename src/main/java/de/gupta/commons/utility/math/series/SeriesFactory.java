package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Comparator;
import java.util.Map;

public final class SeriesFactory
{
	public static <T extends TotallyOrdered<T>, E> Series<T, E> of(final Map<T, E> entries)
	{
		return SeriesImpl.create(entries, (a, b) -> a.compare(b).signum());
	}

	public static <T extends TotallyOrdered<T>, E> Series<T, E> empty()
	{
		return SeriesImpl.create(Map.of(), (a, b) -> a.compare(b).signum());
	}

	public static <T> ForOrder<T> over(final TotalOrderStructure<T> order)
	{
		return new ForOrder<>((a, b) -> order.compare(a, b).signum());
	}

	private SeriesFactory()
	{
	}

	public record ForOrder<T>(Comparator<T> comparator)
	{
		public <E> Series<T, E> of(final Map<T, E> entries)
		{
			return SeriesImpl.create(entries, comparator);
		}

		public <E> Series<T, E> empty()
		{
			return SeriesImpl.create(Map.of(), comparator);
		}
	}
}