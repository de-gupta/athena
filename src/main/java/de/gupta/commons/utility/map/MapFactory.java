package de.gupta.commons.utility.map;

public final class MapFactory
{
	public static <K, V> SequencedMapBuilder<K, V> sequencedMapBuilder()
	{
		return SequencedMapBuilder.create();
	}

	private MapFactory()
	{
	}
}