package de.gupta.commons.utility.map.enumMap;

import java.util.EnumMap;
import java.util.Map;

public final class EnumMapConstruction
{
	public static <K extends Enum<K>, V> EnumMap<K, V> single(final K key, final V value)
	{
		return from(Map.of(key, value), key.getDeclaringClass());
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> from(final Map<K, V> map, final Class<K> enumClass)
	{
		return map.isEmpty() ? new EnumMap<>(enumClass) : new EnumMap<>(map);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> copyOf(final EnumMap<K, V> map)
	{
		return new EnumMap<>(map);
	}

	private EnumMapConstruction()
	{
	}
}