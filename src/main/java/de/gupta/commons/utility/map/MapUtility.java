package de.gupta.commons.utility.map;

import de.gupta.aletheia.functional.Unfolding;

import java.util.*;
import java.util.function.Supplier;

public final class MapUtility
{
	public static <K, V> V getOrThrow(final Map<K, ? extends V> map, final K key,
	                                  final Supplier<RuntimeException> exceptionSupplier)
	{
		return Unfolding.beckon(map)
		                .discern(m -> m.containsKey(key))
		                .metamorphose(m -> m.get(key))
		                .decree(exceptionSupplier);
	}

	public static <K, V> SequencedMap<K, V> withEntry(final SequencedMap<K, V> map, final K key, final V value)
	{
		Objects.requireNonNull(map, "map must not be null");
		Objects.requireNonNull(key, "key must not be null");

		var copy = new LinkedHashMap<>(map);
		copy.put(key, value);
		return Collections.unmodifiableSequencedMap(copy);
	}

	private MapUtility()
	{
	}
}