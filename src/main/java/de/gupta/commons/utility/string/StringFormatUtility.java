package de.gupta.commons.utility.string;

public final class StringFormatUtility
{
	public static boolean startsWithUppercase(final String text)
	{
		return !text.isEmpty() && Character.isUpperCase(text.charAt(0));
	}

	public static String toSuperscript(int number)
	{
		String numStr = String.valueOf(Math.abs(number));
		StringBuilder superscript = new StringBuilder();

		if (number < 0)
		{
			superscript.append("⁻");
		}

		for (char digit : numStr.toCharArray())
		{
			switch (digit)
			{
				case '0' -> superscript.append('⁰');
				case '1' -> superscript.append('¹');
				case '2' -> superscript.append('²');
				case '3' -> superscript.append('³');
				case '4' -> superscript.append('⁴');
				case '5' -> superscript.append('⁵');
				case '6' -> superscript.append('⁶');
				case '7' -> superscript.append('⁷');
				case '8' -> superscript.append('⁸');
				case '9' -> superscript.append('⁹');
			}
		}

		return superscript.toString();
	}

	private StringFormatUtility()
	{
	}
}