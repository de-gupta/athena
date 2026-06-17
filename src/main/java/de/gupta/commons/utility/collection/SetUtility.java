package de.gupta.commons.utility.collection;

import de.gupta.aletheia.collection.cascade.Cascade;
import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class SetUtility
{
	public static Set<String> splitToSetWithComma(final String input)
	{
		return splitToSet(input, ",");
	}

	public static Set<String> splitToSet(final String input, final String delimiter)
	{
		return StreamUtility.splitToStream(input, delimiter).collect(Collectors.toSet());
	}

	public static Set<String> removeBlankStrings(final Set<String> set)
	{
		return Unfolding.beckon(set)
		                .metamorphose(s -> Cascade.beckon(s)
		                                          .discern(StringSanitizationUtility::isNotBlank)
		                                          .precipitate(Collectors.toSet()))
		                .decree(ExceptionHelper.iaeFrom("Input set cannot be null"));
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