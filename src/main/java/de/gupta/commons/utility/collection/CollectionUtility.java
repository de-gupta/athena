package de.gupta.commons.utility.collection;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.map.MapUtility;

import java.util.*;

public final class CollectionUtility
{
	public static <K, V> List<SequencedMap<K, V>> cartesianProduct(final SequencedMap<K, List<V>> axes)
	{
		Objects.requireNonNull(axes, "axes must not be null");

		List<SequencedMap<K, V>> seed = List.of(new LinkedHashMap<>());

		return Loom.thread(axes.sequencedEntrySet())
		           .weave(seed, CollectionUtility::expand)
		           .stream()
		           .map(Collections::unmodifiableSequencedMap)
		           .toList();
	}

	private static <K, V> List<SequencedMap<K, V>> expand(
			final List<SequencedMap<K, V>> accumulator,
			final Map.Entry<K, List<V>> entry)
	{
		var key = entry.getKey();
		var values = entry.getValue();
		Objects.requireNonNull(values, "value list for key must not be null");
		return accumulator.stream()
		                  .flatMap(partial -> values.stream()
		                                            .map(value -> MapUtility.withEntry(partial, key, value)))
		                  .toList();
	}

	private CollectionUtility()
	{
	}
}
