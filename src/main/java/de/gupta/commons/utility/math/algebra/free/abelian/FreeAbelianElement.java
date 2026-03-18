package de.gupta.commons.utility.math.algebra.free.abelian;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public record FreeAbelianElement<V extends Enum<V>>(Class<V> generatorType, Map<V, Integer> exponents)
{
	public static <V extends Enum<V>> FreeAbelianElement<V> zero(final Class<V> generatorType)
	{
		return new FreeAbelianElement<>(generatorType, Map.of());
	}

	public static <V extends Enum<V>> FreeAbelianElement<V> generator(final V generator)
	{
		return generator(generator, 1);
	}

	public static <V extends Enum<V>> FreeAbelianElement<V> generator(final V generator, final int exponent)
	{
		Objects.requireNonNull(generator, "generator");
		return exponent == 0
				? zero(generator.getDeclaringClass())
				: new FreeAbelianElement<>(generator.getDeclaringClass(), Map.of(generator, exponent));
	}

	public FreeAbelianElement
	{
		Objects.requireNonNull(generatorType, "generatorType");
		Objects.requireNonNull(exponents, "exponents");
		final EnumMap<V, Integer> canonical = new EnumMap<>(generatorType);
		exponents.forEach((generator, exponent) ->
		{
			Objects.requireNonNull(generator, "generator");
			Objects.requireNonNull(exponent, "exponent");
			if (generator.getDeclaringClass() != generatorType)
			{
				throw new IllegalArgumentException("All generators must belong to " + generatorType.getName() + ".");
			}
			if (exponent != 0)
			{
				canonical.put(generator, exponent);
			}
		});
		exponents = Collections.unmodifiableMap(canonical);
	}

	public int exponentOf(final V generator)
	{
		return exponents.getOrDefault(generator, 0);
	}

	public boolean isZero()
	{
		return exponents.isEmpty();
	}

	public Stream<Map.Entry<V, Integer>> stream()
	{
		return exponents.entrySet().stream();
	}
}