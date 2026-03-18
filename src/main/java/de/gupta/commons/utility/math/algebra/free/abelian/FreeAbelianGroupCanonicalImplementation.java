package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.map.enumMap.EnumMapArithmetic;

import java.util.EnumMap;

record FreeAbelianGroupCanonicalImplementation<V extends Enum<V>>(
		FreeAbelianGroupGeneratorType<V> generatorTypeDefinition)
		implements FreeAbelianGroup<V>
{
	static <V extends Enum<V>> FreeAbelianGroup<V> of(final FreeAbelianGroupGeneratorType<V> generatorType)
	{
		return new FreeAbelianGroupCanonicalImplementation<>(generatorType);
	}

	@Override
	public EnumMap<V, Integer> zero()
	{
		return new EnumMap<>(generatorTypeDefinition.componentsEnum());
	}

	@Override
	public EnumMap<V, Integer> add(final EnumMap<V, Integer> a, final EnumMap<V, Integer> b)
	{
		return EnumMapArithmetic.mergeAndCleanIfValueEqualsGivenValue(a, b, Integer::sum, 0);
	}

	@Override
	public EnumMap<V, Integer> negate(final EnumMap<V, Integer> a)
	{
		return EnumMapArithmetic.manipulateAndCleanIfValueEqualsGivenValue(a, -1, (i, n) -> i * n, 0);
	}

	@Override
	public Class<V> generatorType()
	{
		return generatorTypeDefinition.componentsEnum();
	}
}