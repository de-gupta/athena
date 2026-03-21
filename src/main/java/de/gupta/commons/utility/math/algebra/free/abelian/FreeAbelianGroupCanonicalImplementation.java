package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.map.enumMap.EnumMapArithmetic;
import de.gupta.commons.utility.map.enumMap.EnumMapConstruction;

record FreeAbelianGroupCanonicalImplementation<V extends Enum<V>>(
		FreeAbelianGroupGeneratorType<V> generatorTypeDefinition)
		implements FreeAbelianGroup<V>
{
	static <V extends Enum<V>> FreeAbelianGroup<V> of(final FreeAbelianGroupGeneratorType<V> generatorType)
	{
		return new FreeAbelianGroupCanonicalImplementation<>(generatorType);
	}

	@Override
	public FreeAbelianElement<V> add(final FreeAbelianElement<V> left, final FreeAbelianElement<V> right)
	{
		return FreeAbelianElement.from(
				generatorType(),
				EnumMapArithmetic.mergeAndCleanIfValueEqualsGivenValue(
						EnumMapConstruction.from(left.exponents(), generatorType()),
						EnumMapConstruction.from(right.exponents(), generatorType()),
						Integer::sum,
						0));
	}

	@Override
	public FreeAbelianElement<V> negate(final FreeAbelianElement<V> element)
	{
		return FreeAbelianElement.from(
				generatorType(),
				EnumMapArithmetic.manipulateAndCleanIfValueEqualsGivenValue(
						EnumMapConstruction.from(element.exponents(), generatorType()),
						-1,
						(i, n) -> i * n,
						0));
	}

	@Override
	public Class<V> generatorType()
	{
		return generatorTypeDefinition.componentsEnum();
	}

	@Override
	public FreeAbelianElement<V> zero()
	{
		return FreeAbelianElement.zero(generatorType());
	}
}
