package de.gupta.commons.utility.string;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class StringSanitizationUtility
{
	public static boolean isNonEmpty(final String input)
	{
		return !isAbsentOrEmpty(input);
	}

	public static boolean isAbsentOrEmpty(final String input)
	{
		return input == null || input.isEmpty();
	}

	public static boolean isNotBlank(final String input)
	{
		return !isAbsentOrBlank(input);
	}

	public static boolean isAbsentOrBlank(final String input)
	{
		return input == null || input.isBlank();
	}

	public static boolean isTrimmed(final String input)
	{
		return input != null && input.trim().equals(input);
	}

	public static String[] breakIntoLines(final String input)
	{
		return input.split("\\r?\\n", -1);
	}

	public static String[] breakIntoLines(final String input, final String delimiter)
	{
		return input.split(delimiter, -1);
	}

	public static void requireNotBlank(final String input, final Supplier<RuntimeException> exceptionSupplier)
	{
		if (isAbsentOrBlank(input))
		{
			throw exceptionSupplier.get();
		}
	}

	public static void requireNotBlank(final String input, final String message)
	{
		requireNotBlank(input, () -> new IllegalArgumentException(message));
	}

	public static String requireNotBlankAnd(final String input, final String message,
	                                        final UnaryOperator<String> operation)
	{
		requireNotBlank(input, message);
		return operation.apply(input);
	}

	private StringSanitizationUtility()
	{
	}
}