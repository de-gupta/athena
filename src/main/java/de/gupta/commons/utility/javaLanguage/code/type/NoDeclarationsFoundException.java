package de.gupta.commons.utility.javaLanguage.code.type;

public final class NoDeclarationsFoundException extends RuntimeException
{
	public static NoDeclarationsFoundException withMessage(final String message)
	{
		return new NoDeclarationsFoundException(message);
	}

	private NoDeclarationsFoundException(final String message)
	{
		super(message);
	}
}