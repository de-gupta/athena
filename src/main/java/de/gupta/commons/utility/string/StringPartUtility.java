package de.gupta.commons.utility.string;

import de.gupta.aletheia.functional.Unfolding;

public final class StringPartUtility
{
	public static String firstN(final String input, final int n)
	{
		return Unfolding.beckon(input)
						.discern(_ -> n > 0, () -> new IllegalArgumentException("n must be greater than zero"))
						.metamorphose(s -> s.substring(0, Math.min(n, s.length())))
						.decree(() -> new IllegalArgumentException("input must be non-null"));
	}

	private StringPartUtility()
	{
	}
}