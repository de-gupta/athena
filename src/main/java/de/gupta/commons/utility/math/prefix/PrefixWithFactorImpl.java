package de.gupta.commons.utility.math.prefix;

import java.util.Optional;

record PrefixWithFactorImpl(Prefix prefix, double factor) implements PrefixWithFactor
{
	private static final PrefixArithmetic prefixArithmetic = PrefixArithmeticFactory.create();

	static PrefixWithFactor of(final Prefix prefix, double factor)
	{
		return new PrefixWithFactorImpl(prefix, factor);
	}

	static PrefixWithFactor of(final Prefix prefix)
	{
		return new PrefixWithFactorImpl(prefix, 1);
	}

	@Override
	public double effectiveFactor()
	{
		return prefix.factor() * factor;
	}

	@Override
	public PrefixWithFactor multiply(final PrefixWithFactor other)
	{
		return prefixArithmetic.multiply(this, other);
	}

	@Override
	public PrefixWithFactor power(final int power)
	{
		return prefixArithmetic.power(this, power);
	}

	@Override
	public Optional<PrefixWithFactor> normalize()
	{
		return prefixArithmetic.normalize(this).cleanIfUnity();
	}

	@Override
	public Optional<PrefixWithFactor> humanReadable()
	{
		return prefixArithmetic.humanReadable(this).cleanIfUnity();
	}

	@Override
	public Optional<PrefixWithFactor> cleanIfUnity()
	{
		return (prefix == Prefix.UNITY && factor == 1) ? Optional.empty() : Optional.of(this);
	}

	@Override
	public boolean isUnity()
	{
		return prefix == Prefix.UNITY && factor == 1;
	}

	@Override
	public boolean isPrefixUnity()
	{
		return prefix == Prefix.UNITY;
	}

	@Override
	public boolean equals(Object o)
	{
		return o == this || o instanceof PrefixWithFactor that && this.prefix() == that.prefix() && this.factor() == that.factor();
	}
}