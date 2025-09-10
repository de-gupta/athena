package de.gupta.commons.utility.javaLanguage.code;

public final class EmptySourceCodeException extends RuntimeException
{
	public static EmptySourceCodeException withMessage(final String message)
	{
		return new EmptySourceCodeException(message);
	}

	private EmptySourceCodeException(final String message)
	{
		super(message);
	}
}