package de.gupta.commons.utility.math.algebra.free.abelian;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class FreeAbelianElement<V extends Enum<V>>
{
	private final Class<V> generatorType;
	private final Map<V, Integer> exponents;

	static <V extends Enum<V>> FreeAbelianElement<V> generator(final V generator)
	{
		return generator(generator, 1);
	}

	static <V extends Enum<V>> FreeAbelianElement<V> generator(final V generator, final int exponent)
	{
		Objects.requireNonNull(generator, "generator");
		return exponent == 0
				? zero(generator.getDeclaringClass())
				: from(generator.getDeclaringClass(), Map.of(generator, exponent));
	}

	static <V extends Enum<V>> FreeAbelianElement<V> zero(final Class<V> generatorType)
	{
		Objects.requireNonNull(generatorType, "generatorType");
		return new FreeAbelianElement<>(generatorType, new EnumMap<>(generatorType));
	}

	static <V extends Enum<V>> FreeAbelianElement<V> from(final Class<V> generatorType,
														  final Map<? extends V, Integer> exponents)
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
		return new FreeAbelianElement<>(generatorType, canonical);
	}

	public int exponentOf(final V generator)
	{
		return exponents.getOrDefault(generator, 0);
	}

	public boolean isZero()
	{
		return exponents.isEmpty();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(generatorType, exponents);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof final FreeAbelianElement<?> that)) return false;
		return Objects.equals(generatorType, that.generatorType) && Objects.equals(exponents,
				that.exponents);
	}

	Class<V> generatorType()
	{
		return generatorType;
	}

	Map<V, Integer> exponents()
	{
		return exponents;
	}

	Stream<Map.Entry<V, Integer>> exponentStream()
	{
		return exponents.entrySet().stream();
	}

	private FreeAbelianElement(final Class<V> generatorType, final EnumMap<V, Integer> canonicalExponents)
	{
		this.generatorType = Objects.requireNonNull(generatorType, "generatorType");
		this.exponents = Collections.unmodifiableMap(new EnumMap<>(canonicalExponents));
	}
}