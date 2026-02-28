package de.gupta.commons.utility.math.algebra.algebraicGroup.freeAbelianGroup;

@FunctionalInterface
public interface FreeAbelianGroupGeneratorType<V extends Enum<V>>
{
	Class<V> componentsEnum();
}