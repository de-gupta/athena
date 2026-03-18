package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.map.enumMap.EnumMapConstruction;
import de.gupta.commons.utility.math.MathUtility;
import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

import java.util.*;
import java.util.stream.Collectors;

public final class HomomorphicMapExtension
{
	public static <T, K extends Enum<K>> Set<T> preimagesFromPartialMapping(
			final GroupStructure<T> structure,
			final Map<T, EnumMap<K, Integer>> partialMapping,
			final EnumMap<K, Integer> element)
	{
		return element.entrySet()
					  .stream()
					  .map(entry -> fromGeneratorSlice(entry, partialMapping)
							  .entrySet().stream()
							  .map(e -> fromPreimagesAndExponents(structure, e.getKey(), e.getValue()))
							  .flatMap(Set::stream)
							  .collect(Collectors.toSet()))
					  .reduce((a, b) -> a.stream()
										 .flatMap(aElement -> b.stream()
															   .map(bElement -> structure.combine(aElement, bElement)))
										 .collect(Collectors.toSet()))
					  .map(a ->
					  {
						  final Set<T> result = new HashSet<>(a);
						  result.addAll(fromDirectMapping(element, partialMapping));
						  return result;
					  })
					  .orElseThrow(() -> new IllegalArgumentException(
							  "No constituents found in the given element."));
	}

	private static <T, K extends Enum<K>> Map<Set<T>, Integer> fromGeneratorSlice(
			final Map.Entry<K, Integer> generatorSlice,
			final Map<T, EnumMap<K, Integer>> partialMapping)
	{
		return MathUtility.positiveDivisors(generatorSlice.getValue())
						  .stream()
						  .map(divisor -> new AbstractMap.SimpleEntry<>(
								  partialMapping.entrySet().stream()
												.filter(e -> e.getValue().equals(EnumMapConstruction.single(
														generatorSlice.getKey(), divisor)))
												.map(Map.Entry::getKey)
												.collect(Collectors.toSet()),
								  generatorSlice.getValue() / divisor))
						  .filter(entry -> !entry.getKey().isEmpty())
						  .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}

	private static <T> Set<T> fromPreimagesAndExponents(final GroupStructure<T> structure,
														final Set<T> elements,
														final int exponent)
	{
		return elements.stream()
					   .map(element -> structure.power(element, exponent))
					   .collect(Collectors.toSet());
	}

	private static <T, K extends Enum<K>> Set<T> fromDirectMapping(final EnumMap<K, Integer> element,
																   final Map<T, EnumMap<K, Integer>> partialMapping)
	{
		return partialMapping.entrySet()
							 .stream()
							 .filter(entry -> entry.getValue().equals(element))
							 .map(Map.Entry::getKey)
							 .collect(Collectors.toSet());
	}

	private HomomorphicMapExtension()
	{
	}
}