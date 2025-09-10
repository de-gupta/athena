package de.gupta.commons.utility.collection;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class SetUtility
{
	public static Set<String> splitToSet(final String input, final String delimiter)
	{
		return StreamUtility.splitToStream(input, delimiter).collect(Collectors.toSet());
	}

	public static Set<String> splitToSetWithComma(final String input)
	{
		return splitToSet(input, ",");
	}

	public static Set<String> removeBlankStrings(final Set<String> set)
	{
		if (set == null)
		{
			throw new IllegalArgumentException("Input set cannot be null");
		}
		return set.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toSet());
	}

	public static <T> Set<T> unionOf(final Collection<Set<T>> sets)
	{
		return sets.stream()
				   .flatMap(Set::stream)
				   .collect(Collectors.toSet());
	}

	private SetUtility()
	{
	}
}