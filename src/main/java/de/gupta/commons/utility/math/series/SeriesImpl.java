package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.interval.Interval;

import java.util.*;
import java.util.function.Function;

final class SeriesImpl<T, E> implements Series<T, E>
{
	private final NavigableMap<T, E> data;

	static <T, E> SeriesImpl<T, E> create(final Map<T, E> entries, final Comparator<T> comparator)
	{
		TreeMap<T, E> sorted = new TreeMap<>(comparator);
		sorted.putAll(entries);
		return new SeriesImpl<>(sorted);
	}

	@Override
	public Optional<E> at(final T index)
	{
		return Optional.ofNullable(data.get(index));
	}

	@Override
	public Series<T, E> between(final T from, final T to)
	{
		return new SeriesImpl<>(data.subMap(from, true, to, false));
	}

	@Override
	public Series<T, E> between(final Interval<T> interval)
	{
		NavigableMap<T, E> result = data;
		final Optional<Bound<T>> lower = interval.lowerBound();
		final Optional<Bound<T>> upper = interval.upperBound();
		if (lower.isPresent())
		{
			final Bound<T> lb = lower.get();
			result = result.tailMap(lb.value(), lb.isClosed());
		}
		if (upper.isPresent())
		{
			final Bound<T> ub = upper.get();
			result = result.headMap(ub.value(), ub.isClosed());
		}
		return new SeriesImpl<>(result);
	}

	@Override
	public Optional<Map.Entry<T, E>> first()
	{
		if (data.isEmpty()) return Optional.empty();
		final Map.Entry<T, E> entry = data.firstEntry();
		return Optional.of(Map.entry(entry.getKey(), entry.getValue()));
	}

	@Override
	public Optional<Map.Entry<T, E>> last()
	{
		if (data.isEmpty()) return Optional.empty();
		final Map.Entry<T, E> entry = data.lastEntry();
		return Optional.of(Map.entry(entry.getKey(), entry.getValue()));
	}

	@Override
	public int size()
	{
		return data.size();
	}

	@Override
	public boolean isEmpty()
	{
		return data.isEmpty();
	}

	@Override
	public NavigableSet<T> indices()
	{
		return Collections.unmodifiableNavigableSet(data.navigableKeySet());
	}

	@Override
	public Collection<E> values()
	{
		return Collections.unmodifiableCollection(data.values());
	}

	@Override
	public <R> Series<T, R> map(final Function<E, R> transform)
	{
		final TreeMap<T, R> mapped = new TreeMap<>(data.comparator());
		data.forEach((k, v) -> mapped.put(k, transform.apply(v)));
		return new SeriesImpl<>(mapped);
	}

	@Override
	public int hashCode()
	{
		return data.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof SeriesImpl<?, ?> other)) return false;
		return Objects.equals(data, other.data);
	}

	@Override
	public String toString()
	{
		return "Series" + data;
	}

	NavigableMap<T, E> data()
	{
		return Collections.unmodifiableNavigableMap(data);
	}

	<R> Series<T, R> withMappedValues(final Map<T, R> newEntries)
	{
		final TreeMap<T, R> sorted = new TreeMap<>(data.comparator());
		sorted.putAll(newEntries);
		return new SeriesImpl<>(sorted);
	}

	private SeriesImpl(final NavigableMap<T, E> data)
	{
		this.data = data;
	}
}
