package de.gupta.commons.utility.math;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MathUtility
{
	public static double round(double value, int places)
	{
		if (places < 0)
		{
			throw new IllegalArgumentException();
		}

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static double relativeDeviationFromPerfectValueWithinBounds(double value, double perfectValue,
																	   double floorGrace,
																	   double ceilingGrace)
	{
		return value < floorGrace ? (value - floorGrace) / (perfectValue - value)
				: value > ceilingGrace ? (ceilingGrace - value) / (value - perfectValue)
				: value <= perfectValue ? 1 - ((perfectValue - value) / (perfectValue - floorGrace))
				: 1 - ((value - perfectValue) / (ceilingGrace - perfectValue));
	}

	public static int gcd(int a, int b)
	{
		return b == 0 ? Math.abs(a) : gcd(b, a % b);
	}

	public static Set<Integer> positiveDivisors(int num)
	{
		return IntStream.rangeClosed(1, Math.abs(num))
						.filter(i -> num % i == 0)
						.boxed()
						.collect(Collectors.toSet());
	}

	private MathUtility()
	{
	}
}