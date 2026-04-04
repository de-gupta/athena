package de.gupta.commons.utility.string;

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

	private StringSanitizationUtility()
	{
	}
}