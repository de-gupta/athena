package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveSemigroup;
import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.Field;
import de.gupta.commons.utility.math.algebra.element.ring.Normed;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.ordering.element.AffinelyOrdered;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public final class SeriesOperations
{
	public static <I, E extends AdditiveSemigroup<E> & ScalarDivisible<E, Long>> Optional<E> average(
			final Series<I, E> series, final RoundingStrategy<E> rounding)
	{
		if (series.isEmpty()) return Optional.empty();
		return sum(series).map(s -> s.divide(series.size(), rounding).quotient());
	}

	public static <I, E extends AdditiveSemigroup<E>> Optional<E> sum(final Series<I, E> series)
	{
		return series.values().reduce(E::add).optional();
	}

	public static <I, E extends AdditiveGroup<E>> Series<I, E> changes(final Series<I, E> series)
	{
		return consecutivePairs(series, E::subtract);
	}

	private static <I, E, R> Series<I, R> consecutivePairs(final Series<I, E> series,
	                                                       final BiFunction<E, E, R> operation)
	{
		return consecutiveEntryPairs(series, (prev, curr) -> operation.apply(curr.getValue(), prev.getValue()));
	}

	private static <I, E, R> Series<I, R> consecutiveEntryPairs(final Series<I, E> series,
	                                                            final BiFunction<Map.Entry<I, E>, Map.Entry<I, E>, R> operation)
	{
		final SeriesImpl<I, E> impl = asImpl(series);
		final Map<I, R> result = new LinkedHashMap<>();
		Map.Entry<I, E> prev = null;
		for (final Map.Entry<I, E> entry : impl.data().entrySet())
		{
			if (prev != null)
				result.put(entry.getKey(), operation.apply(prev, entry));
			prev = entry;
		}
		return impl.withMappedValues(result);
	}

	private static <I, E> SeriesImpl<I, E> asImpl(final Series<I, E> series)
	{
		if (series instanceof SeriesImpl<I, E> impl) return impl;
		throw new IllegalStateException("Unknown Series implementation");
	}

	public static <I extends AffinelyOrdered<I, D>, D extends Normed<N>, N,
			E extends AdditiveGroup<E> & ScalarDivisible<E, N>> Series<I, E> indexWeightedChanges(
			final Series<I, E> series, final RoundingStrategy<E> rounding)
	{
		return consecutiveEntryPairs(series, (previous, current) ->
		{
			final N norm = previous.getKey().displacementTo(current.getKey()).norm();
			return current.getValue().subtract(previous.getValue()).divide(norm, rounding).quotient();
		});
	}

	public static <I extends AffinelyOrdered<I, D>, D extends Normed<N>, N,
			S extends ScalarDivisible<S, N> & Ring<S>, E extends ScalarQuotientable<E, S>> Series<I, S> indexWeightedPercentChanges(
			final Series<I, E> series, final RoundingStrategy<S> rounding)
	{
		return consecutiveEntryPairs(series, (previous, current) ->
		{
			final N norm = previous.getKey().displacementTo(current.getKey()).norm();
			final S ratio = current.getValue().ratio(previous.getValue());
			return ratio.subtract(ratio.one()).divide(norm, rounding).quotient();
		});
	}

	public static <I extends AffinelyOrdered<I, D>, D extends Normed<N>, N,
			S extends ScalarDivisible<S, N>, E extends ScalarQuotientable<E, S>> Series<I, S> indexWeightedRatios(
			final Series<I, E> series, final RoundingStrategy<S> rounding)
	{
		return consecutiveEntryPairs(series, (previous, current) ->
		{
			final N norm = previous.getKey().displacementTo(current.getKey()).norm();
			return current.getValue().ratio(previous.getValue()).divide(norm, rounding).quotient();
		});
	}

	public static <I, S extends Field<S>, E extends ScalarQuotientable<E, S>> Series<I, S> percentageChanges(
			final Series<I, E> series)
	{
		return ratios(series).map(s -> s.subtract(s.one()));
	}

	public static <I, S, E extends ScalarQuotientable<E, S>> Series<I, S> ratios(final Series<I, E> series)
	{
		return consecutivePairs(series, E::ratio);
	}

	private SeriesOperations()
	{
	}
}