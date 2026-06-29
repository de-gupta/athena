package de.gupta.commons.utility.math.series;

import de.gupta.aletheia.collection.cascade.Cascade;
import de.gupta.commons.utility.math.ordering.interval.Interval;

import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.function.Function;

public sealed interface Series<T, E> permits SeriesImpl
{
	Optional<E> at(T index);

	Series<T, E> between(T from, T to);

	Series<T, E> between(Interval<T> interval);

	Optional<Map.Entry<T, E>> first();

	Optional<Map.Entry<T, E>> last();

	long size();

	boolean isEmpty();

	NavigableSet<T> indices();

	Cascade<E> values();

	<R> Series<T, R> map(Function<E, R> transform);
}