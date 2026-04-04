package de.gupta.commons.utility.map;

import de.gupta.aletheia.functional.Unfolding;

import java.util.Map;
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

	private MapUtility()
	{
	}
}