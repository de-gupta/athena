package de.gupta.commons.utility.map.enumMap;

import java.util.*;
import java.util.function.Supplier;

public record ImmutableEnumMap<K extends Enum<K>, V>(Class<K> keyType, Map<K, V> values)
{
	public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> empty(final Class<K> keyType)
	{
		return new ImmutableEnumMap<>(keyType, Map.of());
	}

	public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> of(final Class<K> keyType,
	                                                               final Map<K, V> values)
	{
		return new ImmutableEnumMap<>(keyType, values);
	}

	public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> single(final K key, final V value)
	{
		Objects.requireNonNull(key, "key may not be null");
		return new ImmutableEnumMap<>(key.getDeclaringClass(), Map.of(key, value));
	}

	public ImmutableEnumMap(final Class<K> keyType, final Map<K, V> values)
	{
		Objects.requireNonNull(keyType, "keyType may not be null");
		Objects.requireNonNull(values, "values may not be null");

		this.keyType = keyType;
		this.values = Collections.unmodifiableMap(EnumMapConstruction.from(values, keyType));
	}

	public Optional<V> find(final K key)
	{
		return Optional.ofNullable(values.get(key));
	}

	public boolean contains(final K key)
	{
		return values.containsKey(key);
	}

	public V getOrThrow(final K key, final Supplier<? extends RuntimeException> exceptionSupplier)
	{
		if (values.containsKey(key)) return values.get(key);
		throw exceptionSupplier.get();
	}

	public ImmutableEnumMap<K, V> with(final K key, final V value)
	{
		Objects.requireNonNull(key, "key may not be null");

		final var copy = toMutableEnumMap();
		copy.put(key, value);
		return new ImmutableEnumMap<>(keyType, copy);
	}

	public ImmutableEnumMap<K, V> without(final K key)
	{
		Objects.requireNonNull(key, "key may not be null");

		final var copy = toMutableEnumMap();
		copy.remove(key);
		return new ImmutableEnumMap<>(keyType, copy);
	}

	public EnumMap<K, V> toMutableEnumMap()
	{
		return EnumMapConstruction.from(values, keyType);
	}
}