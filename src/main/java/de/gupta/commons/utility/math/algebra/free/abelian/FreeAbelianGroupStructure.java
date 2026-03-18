package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.map.enumMap.EnumMapConstruction;
import de.gupta.commons.utility.math.algebra.structure.binary.AbelianGroupStructure;

import java.util.EnumMap;
import java.util.stream.Collectors;

public interface FreeAbelianGroupStructure<V extends Enum<V>> extends AbelianGroupStructure<EnumMap<V, Integer>>
{
	Class<V> generatorType();

	@Override
	default EnumMap<V, Integer> identity()
	{
		return new EnumMap<>(generatorType());
	}

	default EnumMap<V, Integer> zero()
	{
		return identity();
	}

	default EnumMap<V, Integer> generator(final V generator)
	{
		return generator(generator, 1);
	}

	default EnumMap<V, Integer> generator(final V generator, final int exponent)
	{
		return exponent == 0 ? zero() : EnumMapConstruction.single(generator, exponent);
	}

	default EnumMap<V, Integer> add(final EnumMap<V, Integer> left, final EnumMap<V, Integer> right)
	{
		return combine(left, right);
	}

	default EnumMap<V, Integer> negate(final EnumMap<V, Integer> element)
	{
		return inverse(element);
	}

	default EnumMap<V, Integer> subtract(final EnumMap<V, Integer> left, final EnumMap<V, Integer> right)
	{
		return divide(left, right);
	}

	default EnumMap<V, Integer> scale(final EnumMap<V, Integer> element, final int n)
	{
		return power(element, n);
	}

	default String toCanonicalString(final EnumMap<V, Integer> element)
	{
		return element.entrySet().stream()
					  .filter(e -> e.getValue() != 0)
					  .map(e -> e.getKey() + "^" + e.getValue())
					  .collect(Collectors.joining("·"));
	}

	default int exponentOf(final EnumMap<V, Integer> element, final V generator)
	{
		return element.getOrDefault(generator, 0);
	}

	default boolean isZero(final EnumMap<V, Integer> element)
	{
		return element.values().stream().allMatch(i -> i == 0);
	}
}