package de.gupta.commons.utility.math.algebra.algebraicGroup.freeAbelianGroup;

import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public sealed interface FreeAbelianGroup<V extends Enum<V>> permits FreeAbelianGroupCanonicalImplementation
{
	EnumMap<V, Integer> zero();

	default EnumMap<V, Integer> scale(EnumMap<V, Integer> a, int n)
	{
		return n == 0 ? zero() : n < 0 ? scale(negate(a), -n) :
				IntStream.range(0, n).mapToObj(_ -> a).reduce(zero(), this::add);
	}

	default EnumMap<V, Integer> subtract(EnumMap<V, Integer> a, EnumMap<V, Integer> b)
	{
		return add(a, negate(b));
	}

	EnumMap<V, Integer> add(EnumMap<V, Integer> a, EnumMap<V, Integer> b);

	EnumMap<V, Integer> negate(EnumMap<V, Integer> a);

	default String toCanonicalString(EnumMap<V, Integer> a)
	{
		return a.entrySet().stream()
				.filter(e -> e.getValue() != 0)
				.map(e -> e.getKey() + "^" + e.getValue())
				.collect(Collectors.joining("·"));
	}

	default int exponentOf(EnumMap<V, Integer> a, V v)
	{
		return a.getOrDefault(v, 0);
	}

	default boolean equals(EnumMap<V, Integer> a, EnumMap<V, Integer> b)
	{
		return a.equals(b);
	}

	default boolean isZero(EnumMap<V, Integer> a)
	{
		return a.values().stream().allMatch(i -> i == 0);
	}
}