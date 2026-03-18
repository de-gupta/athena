package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.math.algebra.structure.binary.AbelianGroupStructure;

import java.util.stream.Collectors;

public interface FreeAbelianGroupStructure<V extends Enum<V>> extends AbelianGroupStructure<FreeAbelianElement<V>>
{
	Class<V> generatorType();

	@Override
	default FreeAbelianElement<V> identity()
	{
		return FreeAbelianElement.zero(generatorType());
	}

	default FreeAbelianElement<V> zero()
	{
		return identity();
	}

	default FreeAbelianElement<V> generator(final V generator)
	{
		return FreeAbelianElement.generator(generator);
	}

	default FreeAbelianElement<V> generator(final V generator, final int exponent)
	{
		return FreeAbelianElement.generator(generator, exponent);
	}

	default FreeAbelianElement<V> add(final FreeAbelianElement<V> left, final FreeAbelianElement<V> right)
	{
		return combine(left, right);
	}

	default FreeAbelianElement<V> negate(final FreeAbelianElement<V> element)
	{
		return inverse(element);
	}

	default FreeAbelianElement<V> subtract(final FreeAbelianElement<V> left, final FreeAbelianElement<V> right)
	{
		return divide(left, right);
	}

	default FreeAbelianElement<V> scale(final FreeAbelianElement<V> element, final int n)
	{
		return power(element, n);
	}

	default String toCanonicalString(final FreeAbelianElement<V> element)
	{
		return element.isZero()
				? "0"
				: element.exponentStream()
						 .map(entry -> entry.getKey() + "^" + entry.getValue())
						 .collect(Collectors.joining("·"));
	}

	default int exponentOf(final FreeAbelianElement<V> element, final V generator)
	{
		return element.exponentOf(generator);
	}

	default boolean isZero(final FreeAbelianElement<V> element)
	{
		return element.isZero();
	}
}