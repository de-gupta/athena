package de.gupta.commons.utility.map.enumMap;

import de.gupta.commons.utility.map.MapCleaner;

import java.util.EnumMap;
import java.util.function.BiFunction;

public final class EnumMapArithmetic
{
	public static <K extends Enum<K>, V extends Number> EnumMap<K, V> mergeAndCleanIfValueEqualsGivenValue(
			final EnumMap<K, V> a,
			final EnumMap<K, V> b,
			final BiFunction<V, V, V> mergeFunction, final V givenValue)
	{
		return MapCleaner.removeKeyIfValueEquals(merge(a, b, mergeFunction), givenValue);
	}

	public static <K extends Enum<K>, V extends Number> EnumMap<K, V> merge(final EnumMap<K, V> a,
																			final EnumMap<K, V> b,
																			final BiFunction<V, V, V> mergeFunction)
	{
		EnumMap<K, V> result = new EnumMap<>(a);
		b.forEach((key, value) -> result.merge(key, value, mergeFunction));
		return result;
	}

	public static <K extends Enum<K>, V extends Number, M> EnumMap<K, V> manipulateAndCleanIfValueEqualsGivenValue(
			final EnumMap<K, V> a,
			final M manipulationArgument,
			final BiFunction<V, M, V> manipulationFunction,
			final V givenValue)
	{
		return MapCleaner.removeKeyIfValueEquals(manipulate(a, manipulationArgument, manipulationFunction),
				givenValue);
	}

	public static <K extends Enum<K>, V extends Number, M> EnumMap<K, V> manipulate(final EnumMap<K, V> a,
																					final M manipulationArgument,
																					final BiFunction<V, M, V> manipulationFunction)
	{
		EnumMap<K, V> result = new EnumMap<>(a);
		result.replaceAll((_, value) -> manipulationFunction.apply(value, manipulationArgument));
		return result;
	}


	private EnumMapArithmetic()
	{
	}


}