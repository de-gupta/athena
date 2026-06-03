package de.gupta.commons.utility.collection;

import java.util.*;

public final class CollectionUtility
{
	public static <K, V> List<SequencedMap<K, V>> cartesianProduct(final SequencedMap<K, List<V>> axes)
	{
		Objects.requireNonNull(axes, "axes must not be null");

		List<SequencedMap<K, V>> accumulator = List.of(new LinkedHashMap<>());

		for (var entry : axes.sequencedEntrySet())
		{
			var key = entry.getKey();
			var values = entry.getValue();
			Objects.requireNonNull(values, "value list for key must not be null");

			if (values.isEmpty())
			{
				return List.of();
			}

			List<SequencedMap<K, V>> expanded = new ArrayList<>();
			for (var partial : accumulator)
			{
				for (var value : values)
				{
					var combo = new LinkedHashMap<>(partial);
					combo.put(key, value);
					expanded.add(combo);
				}
			}
			accumulator = expanded;
		}

		return accumulator.stream()
		                  .map(Collections::unmodifiableSequencedMap)
		                  .toList();
	}

	private CollectionUtility()
	{
	}
}