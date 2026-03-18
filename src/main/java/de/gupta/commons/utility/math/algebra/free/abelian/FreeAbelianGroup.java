package de.gupta.commons.utility.math.algebra.free.abelian;

import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public sealed interface FreeAbelianGroup<V extends Enum<V>> extends FreeAbelianGroupStructure<V>
		permits FreeAbelianGroupCanonicalImplementation
{
	EnumMap<V, Integer> zero();

	default EnumMap<V, Integer> scale(final EnumMap<V, Integer> a, final int n)
	{
		return n == 0 ? zero() : n < 0 ? scale(negate(a), -n) :
				IntStream.range(0, n).mapToObj(_ -> a).reduce(zero(), this::add);
	}

	default EnumMap<V, Integer> subtract(final EnumMap<V, Integer> a, final EnumMap<V, Integer> b)
	{
		return add(a, negate(b));
	}

	EnumMap<V, Integer> add(EnumMap<V, Integer> a, EnumMap<V, Integer> b);

	EnumMap<V, Integer> negate(EnumMap<V, Integer> a);

	@Override
	default EnumMap<V, Integer> identity()
	{
		return zero();
	}

	@Override
	default EnumMap<V, Integer> combine(final EnumMap<V, Integer> left, final EnumMap<V, Integer> right)
	{
		return add(left, right);
	}

	@Override
	default EnumMap<V, Integer> inverse(final EnumMap<V, Integer> element)
	{
		return negate(element);
	}

	default String toCanonicalString(final EnumMap<V, Integer> a)
	{
		return a.entrySet().stream()
				.filter(e -> e.getValue() != 0)
				.map(e -> e.getKey() + "^" + e.getValue())
				.collect(Collectors.joining("·"));
	}

	default int exponentOf(final EnumMap<V, Integer> a, final V v)
	{
		return a.getOrDefault(v, 0);
	}

	default boolean equals(final EnumMap<V, Integer> a, final EnumMap<V, Integer> b)
	{
		return a.equals(b);
	}

	default boolean isZero(final EnumMap<V, Integer> a)
	{
		return a.values().stream().allMatch(i -> i == 0);
	}
}
