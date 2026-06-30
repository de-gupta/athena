package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

public final class RationalNumberEstimators
{
	public static RationalNumber initialEstimate(final RationalNumber radicand, final int degree)
	{
		final long num = Math.abs(radicand.numerator().value());
		final long den = Math.abs(radicand.denominator().value());
		final int numBits = Long.SIZE - Long.numberOfLeadingZeros(num);
		final int denBits = Long.SIZE - Long.numberOfLeadingZeros(den);
		final long estNum = Math.max(1L, 1L << Math.max(0, (numBits - 1) / degree));
		final long estDen = Math.max(1L, 1L << Math.max(0, (denBits - 1) / degree));
		return RationalNumberFactory.of(estNum, estDen);
	}

	private RationalNumberEstimators()
	{
	}
}