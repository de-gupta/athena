package de.gupta.commons.utility.map.enumMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ImmutableEnumMapBuilder<K extends Enum<K>, V>
{
	private final Class<K> keyType;
	private final EnumMap<K, V> values;

	public static <K extends Enum<K>, V> ImmutableEnumMapBuilder<K, V> create(final Class<K> keyType)
	{
		return new ImmutableEnumMapBuilder<>(keyType);
	}

	public ImmutableEnumMapBuilder<K, V> add(final K key, final V value)
	{
		Objects.requireNonNull(key, "key may not be null");
		values.put(key, value);
		return this;
	}

	public ImmutableEnumMapBuilder<K, V> addAll(final Map<K, ? extends V> values)
	{
		Objects.requireNonNull(values, "values may not be null");
		this.values.putAll(values);
		return this;
	}

	public ImmutableEnumMap<K, V> build()
	{
		return ImmutableEnumMap.of(keyType, values);
	}

	private ImmutableEnumMapBuilder(final Class<K> keyType)
	{
		this.keyType = Objects.requireNonNull(keyType, "keyType may not be null");
		this.values = new EnumMap<>(keyType);
	}
}