package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveSemigroup;
import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Field;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public final class SeriesOperations
{
	public static <T, E extends AdditiveSemigroup<E> & ScalarDivisible<E>> Optional<E> average(
			final Series<T, E> series, final RoundingStrategy<E> rounding)
	{
		if (series.isEmpty()) return Optional.empty();
		return sum(series).map(s -> s.divide(series.size(), rounding).quotient());
	}

	public static <T, E extends AdditiveSemigroup<E>> Optional<E> sum(final Series<T, E> series)
	{
		return series.values().stream().reduce(E::add);
	}

	public static <T, E extends AdditiveGroup<E>> Series<T, E> changes(final Series<T, E> series)
	{
		return consecutivePairs(series, E::subtract);
	}

	private static <T, E, R> Series<T, R> consecutivePairs(final Series<T, E> series,
	                                                       final BiFunction<E, E, R> operation)
	{
		final SeriesImpl<T, E> impl = asImpl(series);
		final Map<T, R> result = new LinkedHashMap<>();
		T prevKey = null;
		E prevVal = null;
		for (final Map.Entry<T, E> entry : impl.data().entrySet())
		{
			if (prevKey != null)
				result.put(entry.getKey(), operation.apply(entry.getValue(), prevVal));
			prevKey = entry.getKey();
			prevVal = entry.getValue();
		}
		return impl.withMappedValues(result);
	}

	private static <T, E> SeriesImpl<T, E> asImpl(final Series<T, E> series)
	{
		if (series instanceof SeriesImpl<T, E> impl) return impl;
		throw new IllegalStateException("Unknown Series implementation");
	}

	public static <T, S extends Field<S>, E extends ScalarQuotientable<E, S>> Series<T, S> percentageChanges(
			final Series<T, E> series)
	{
		return ratios(series).map(s -> s.subtract(s.one()));
	}

	public static <T, S, E extends ScalarQuotientable<E, S>> Series<T, S> ratios(final Series<T, E> series)
	{
		return consecutivePairs(series, E::ratio);
	}

	private SeriesOperations()
	{
	}
}