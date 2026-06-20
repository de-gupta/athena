package de.gupta.commons.utility.math.algebra.free.abelian;

@FunctionalInterface
public interface FreeAbelianGroupGeneratorType<V extends Enum<V>>
{
	Class<V> componentsEnum();
}
