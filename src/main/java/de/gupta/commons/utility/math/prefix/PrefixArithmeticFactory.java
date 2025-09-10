package de.gupta.commons.utility.math.prefix;

public class PrefixArithmeticFactory
{
	public static PrefixArithmetic create()
	{
		return new PrefixArithmeticImpl();
	}
}