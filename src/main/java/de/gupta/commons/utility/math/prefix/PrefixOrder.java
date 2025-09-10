package de.gupta.commons.utility.math.prefix;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PrefixOrder
{
	default SortedSet<Prefix> prefixesInOrder()
	{
		return Stream.of(Prefix.values())
					 .collect(Collectors.toCollection(
							 () -> new TreeSet<>(Comparator.comparingInt(this::orderInSequence))));
	}

	int orderInSequence(Prefix prefix);
}