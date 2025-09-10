package de.gupta.commons.utility.math.prefix;

public interface PrefixArithmetic
{
	PrefixWithFactor multiply(final Prefix left, final Prefix right);

	PrefixWithFactor multiply(final PrefixWithFactor left, final PrefixWithFactor right);

	PrefixWithFactor power(final Prefix prefix, final int exponent);

	PrefixWithFactor power(final PrefixWithFactor prefix, final int exponent);

	PrefixWithFactor divide(final Prefix left, final Prefix right);

	PrefixWithFactor divide(final PrefixWithFactor left, final PrefixWithFactor right);

	PrefixWithFactor normalize(final PrefixWithFactor prefixWithFactor);

	PrefixWithFactor humanReadable(PrefixWithFactor prefixWithFactor);
}