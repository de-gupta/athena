package de.gupta.commons.utility.map;

import de.gupta.aletheia.functional.Unfolding;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public final class SequencedMapBuilder<K, V>
{
	private final LinkedHashMap<K, V> map = new LinkedHashMap<>();

	static <K, V> SequencedMapBuilder<K, V> create()
	{
		return new SequencedMapBuilder<>();
	}

	public SequencedMapBuilder<K, V> add(final K key, final V value)
	{
		Unfolding.beckon(key)
		         .discern(k -> !map.containsKey(k), () -> new IllegalArgumentException("Duplicate key: " + key))
		         .unlace(k -> map.put(k, value))
		         .decree(() -> new IllegalArgumentException("Key cannot be null"));
		return this;
	}

	public SequencedMapBuilder<K, V> addAll(final SequencedMap<K, V> map)
	{
		map.forEach(this::add);
		return this;
	}

	public SequencedMap<K, V> build()
	{
		return Collections.unmodifiableSequencedMap(new LinkedHashMap<>(map));
	}

	private SequencedMapBuilder()
	{
	}
}