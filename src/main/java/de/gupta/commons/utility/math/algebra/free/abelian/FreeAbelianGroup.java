package de.gupta.commons.utility.math.algebra.free.abelian;

public sealed interface FreeAbelianGroup<V extends Enum<V>> extends FreeAbelianGroupStructure<V>
		permits FreeAbelianGroupCanonicalImplementation
{
}