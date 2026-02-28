package de.gupta.commons.utility.math.prefix;

public final class PrefixWithFactorFactory
{
	public static PrefixWithFactor unity()
	{
		return PrefixWithFactorImpl.of(Prefix.UNITY, 1.0);
	}

	public static PrefixWithFactor with(final double factor)
	{
		return PrefixWithFactorImpl.of(Prefix.UNITY, factor);
	}

	public static PrefixWithFactor with(final Prefix prefix, final double factor)
	{
		return PrefixWithFactorImpl.of(prefix, factor);
	}

	public static PrefixWithFactor with(final Prefix prefix)
	{
		return PrefixWithFactorImpl.of(prefix, 1.0);
	}

	private PrefixWithFactorFactory()
	{
	}
}