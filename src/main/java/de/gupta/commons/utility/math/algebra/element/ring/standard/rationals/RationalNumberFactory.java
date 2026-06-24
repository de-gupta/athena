package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;

public final class RationalNumberFactory
{
	public static RationalNumber zero()
	{
		return of(0, 1);
	}

	public static RationalNumber of(final int numerator, final int denominator)
	{
		return of(numerator, (long) denominator);
	}

	public static RationalNumber of(final long numerator, final long denominator)
	{
		return of(IntegralNumberFactory.of(numerator), IntegralNumberFactory.of(denominator));
	}

	public static RationalNumber of(final IntegralNumber numerator, final IntegralNumber denominator)
	{
		return RationalNumberImpl.of(numerator, denominator);
	}

	public static RationalNumber one()
	{
		return of(1, 1);
	}

	private RationalNumberFactory()
	{
	}
}