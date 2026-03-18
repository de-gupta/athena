package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.math.algebra.structure.binary.AbelianGroupStructure;

import java.util.EnumMap;

public interface FreeAbelianGroupStructure<V extends Enum<V>> extends AbelianGroupStructure<EnumMap<V, Integer>>
{
	Class<V> generatorType();
}
