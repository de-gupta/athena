package de.gupta.commons.utility.math.prefix;

import java.util.Arrays;

final class PrefixArithmeticImpl implements PrefixArithmetic
{
	@Override
	public PrefixWithFactor multiply(final Prefix left, final Prefix right)
	{
		return largestPrefixAndRemainingFactor(left.factor() * right.factor());
	}

	@Override
	public PrefixWithFactor multiply(final PrefixWithFactor left, final PrefixWithFactor right)
	{
		return largestPrefixAndRemainingFactor(left.effectiveFactor() * right.effectiveFactor());
	}

	@Override
	public PrefixWithFactor power(final Prefix prefix, final int exponent)
	{
		return largestPrefixAndRemainingFactor(Math.pow(prefix.factor(), exponent));
	}

	@Override
	public PrefixWithFactor power(final PrefixWithFactor prefix, final int exponent)
	{
		return largestPrefixAndRemainingFactor(Math.pow(prefix.effectiveFactor(), exponent));
	}

	@Override
	public PrefixWithFactor divide(final Prefix left, final Prefix right)
	{
		return largestPrefixAndRemainingFactor(left.factor() / right.factor());
	}

	@Override
	public PrefixWithFactor divide(final PrefixWithFactor left, final PrefixWithFactor right)
	{
		return largestPrefixAndRemainingFactor(left.effectiveFactor() / right.effectiveFactor());
	}

	@Override
	public PrefixWithFactor normalize(final PrefixWithFactor prefixWithFactor)
	{
		return largestPrefixAndRemainingFactor(prefixWithFactor.effectiveFactor());
	}

	@Override
	public PrefixWithFactor humanReadable(final PrefixWithFactor prefixWithFactor)
	{
		return humanReadablePrefixAndRemainingFactor(prefixWithFactor.effectiveFactor());
	}

	private static PrefixWithFactor humanReadablePrefixAndRemainingFactor(final double totalFactor)
	{
		if ((totalFactor > 1 && totalFactor < 1000) || (totalFactor < 1 && totalFactor > 0.1))
		{
			return PrefixWithFactorFactory.with(Prefix.UNITY, totalFactor);
		}
		return largestPrefixAndRemainingFactor(totalFactor);
	}

	private static PrefixWithFactor largestPrefixAndRemainingFactor(final double totalFactor)
	{
		return Arrays.stream(Prefix.values())
					 .filter(prefix -> prefix.factor() <= Math.abs(totalFactor))
					 .max(Prefix::compareByFactor)
					 .map(prefix -> PrefixWithFactorFactory.with(prefix, totalFactor / prefix.factor()))
					 .orElseGet(() -> remainderFromSmallestPrefix(totalFactor));
	}

	private static PrefixWithFactor remainderFromSmallestPrefix(final double totalFactor)
	{
		return Arrays.stream(Prefix.values())
					 .min(Prefix::compareByFactor)
					 .map(prefix -> PrefixWithFactorFactory.with(prefix, totalFactor / prefix.factor()))
					 .orElseThrow(
							 () -> new IllegalArgumentException(
									 "Unexpected exception: No prefix found for factor: " + totalFactor));
	}

	PrefixArithmeticImpl()
	{
	}
}