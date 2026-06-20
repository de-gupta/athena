package de.gupta.commons.utility.string;

import de.gupta.commons.utility.collection.SequencedCollectionUtility;

import java.util.Optional;
import java.util.SequencedCollection;

public final class StringSearchUtility
{
	public static String afterSearchString(final String input, final String searchString)
	{
		return Optional.of(input)
		               .filter(i -> i.contains(searchString))
		               .map(i -> i.substring(i.indexOf(searchString) + searchString.length()))
		               .orElse(input);
	}

	public static Optional<String> firstNonBlank(final SequencedCollection<String> strings)
	{
		return SequencedCollectionUtility.firstNotMatching(strings, s -> s == null || s.isBlank());
	}

	private StringSearchUtility()
	{
	}
}