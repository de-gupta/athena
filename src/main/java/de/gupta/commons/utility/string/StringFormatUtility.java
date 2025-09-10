package de.gupta.commons.utility.string;

public final class StringFormatUtility
{
	private StringFormatUtility()
	{
	}

	public static boolean startsWithUppercase(final String text)
	{
		return !text.isEmpty() && Character.isUpperCase(text.charAt(0));
	}
}