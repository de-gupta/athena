package de.gupta.commons.utility.map;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class MapCleaner
{
	public static <K, V> Map<K, V> removeKeyIfValueEquals(final Map<K, V> map, final V value)
	{
		Map<K, V> result = new HashMap<>(map);
		result.entrySet().removeIf(entry ->
				(entry.getValue() != null && entry.getValue()
												  .equals(value)) || (entry.getValue() == null && value == null)
		);
		return result;
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> removeKeyIfValueEquals(final EnumMap<K, V> map, final V value)
	{
		EnumMap<K, V> result = new EnumMap<>(map);
		result.entrySet().removeIf(entry ->
				(entry.getValue() != null && entry.getValue()
												  .equals(value)) || (entry.getValue() == null && value == null)
		);
		return result;
	}

	private MapCleaner()
	{
	}
}