package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.math.MathUtility;
import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class HomomorphicMapExtension
{
	public static <T, K extends Enum<K>> Set<T> preimagesFromPartialMapping(
			final GroupStructure<T> structure,
			final FreeAbelianGroupStructure<K> codomain,
			final Map<T, FreeAbelianElement<K>> partialMapping,
			final FreeAbelianElement<K> element)
	{
		return element.exponentStream()
					  .map(entry -> fromGeneratorSlice(entry, codomain, partialMapping)
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
			final FreeAbelianGroupStructure<K> codomain,
			final Map<T, FreeAbelianElement<K>> partialMapping)
	{
		return MathUtility.positiveDivisors(generatorSlice.getValue())
						  .stream()
						  .map(divisor -> new AbstractMap.SimpleEntry<>(
								  partialMapping.entrySet().stream()
												.filter(e -> e.getValue().equals(codomain.generator(
														generatorSlice.getKey(), divisor)))
												.map(Map.Entry::getKey)
												.collect(Collectors.toSet()),
								  generatorSlice.getValue() / divisor))
						  .filter(entry -> !entry.getKey().isEmpty())
						  .collect(
								  Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}

	private static <T> Set<T> fromPreimagesAndExponents(final GroupStructure<T> structure,
														final Set<T> elements,
														final int exponent)
	{
		return elements.stream()
					   .map(element -> structure.power(element, exponent))
					   .collect(Collectors.toSet());
	}

	private static <T, K extends Enum<K>> Set<T> fromDirectMapping(final FreeAbelianElement<K> element,
																   final Map<T, FreeAbelianElement<K>> partialMapping)
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