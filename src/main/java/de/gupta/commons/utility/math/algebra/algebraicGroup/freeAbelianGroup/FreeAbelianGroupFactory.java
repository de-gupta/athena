package de.gupta.commons.utility.math.algebra.algebraicGroup.freeAbelianGroup;

public final class FreeAbelianGroupFactory
{
	public static <V extends Enum<V>> FreeAbelianGroup<V> create(final Class<V> enumClass)
	{
		return FreeAbelianGroupCanonicalImplementation.of(() -> enumClass);
	}

	private FreeAbelianGroupFactory()
	{
	}
}