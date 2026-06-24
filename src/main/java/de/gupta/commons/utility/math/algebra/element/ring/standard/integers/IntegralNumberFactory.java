package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

public final class IntegralNumberFactory
{
	public static IntegralNumber of(final long value)
	{
		return new IntegersAsEuclideanDomain(value);
	}

	private IntegralNumberFactory()
	{
	}
}