package de.gupta.commons.utility.exception;

import java.util.function.Supplier;

public final class ExceptionHelper
{
	public Supplier<IllegalArgumentException> iaeFrom(final String message)
	{
		return () -> new IllegalArgumentException(message);
	}

	private ExceptionHelper()
	{
	}
}