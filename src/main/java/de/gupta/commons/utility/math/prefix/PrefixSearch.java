package de.gupta.commons.utility.math.prefix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PrefixSearch
{
	private static final Map<String, Prefix> SYMBOL_LOOKUP = new HashMap<>();
	private static final Map<String, Prefix> CASE_SENSITIVE_LOOKUP = new HashMap<>();
	private static final Map<String, Prefix> CASE_INSENSITIVE_LOOKUP = new HashMap<>();

	static
	{
		for (Prefix prefix : Prefix.values())
		{
			SYMBOL_LOOKUP.put(prefix.symbol(), prefix);
			prefix.caseSensitiveAliases().forEach(alias -> CASE_SENSITIVE_LOOKUP.put(alias, prefix));
			prefix.caseInsensitiveAliases().forEach(alias -> CASE_INSENSITIVE_LOOKUP.put(alias.toLowerCase(), prefix));
		}
	}

	public static Optional<Prefix> fromSymbol(String symbol)
	{
		return Optional.ofNullable(symbol).map(SYMBOL_LOOKUP::get);
	}

	public static Optional<Prefix> fromCaseSensitiveAlias(String alias)
	{
		return Optional.ofNullable(alias).map(CASE_SENSITIVE_LOOKUP::get);
	}

	public static Optional<Prefix> fromCaseInsensitiveAlias(String alias)
	{
		return Optional.ofNullable(alias).map(String::toLowerCase)
					   .flatMap(str -> Optional.ofNullable(CASE_INSENSITIVE_LOOKUP.get(str)));
	}

	public static Optional<Prefix> from(String input)
	{
		return Optional.ofNullable(input)
					   .map(SYMBOL_LOOKUP::get)
					   .or(() -> Optional.ofNullable(input).map(CASE_SENSITIVE_LOOKUP::get))
					   .or(() -> Optional.ofNullable(input).map(String::toLowerCase)
										 .flatMap(str -> Optional.ofNullable(CASE_INSENSITIVE_LOOKUP.get(str))));
	}
}