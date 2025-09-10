package de.gupta.commons.utility.math.prefix;

public final class PrefixArithmeticFactory
{
	public static PrefixArithmetic create()
	{
		return new PrefixArithmeticImpl();
	}

	private PrefixArithmeticFactory()
	{
	}
}