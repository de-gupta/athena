package de.gupta.commons.utility.collection;

import java.util.Optional;
import java.util.SequencedCollection;
import java.util.function.Predicate;

public final class SequencedCollectionUtility
{
	public static <T> Optional<T> firstNotMatching(final SequencedCollection<T> collection,
	                                               final Predicate<T> predicate)
	{
		return collection.stream()
		                 .filter(predicate.negate())
		                 .findFirst();
	}

	private SequencedCollectionUtility()
	{
	}
}