package de.gupta.commons.utility.math;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public final class FunctionUtility
{
	public static DoubleUnaryOperator multiplyWithExponents(
			final Map<DoubleUnaryOperator, Integer> functionExponentsMap)
	{
		return value -> functionExponentsMap.entrySet().stream()
											.mapToDouble(entry -> Math.pow(entry.getKey().applyAsDouble(value),
													entry.getValue()))
											.reduce(1, (a, b) -> a * b);
	}

	public static DoubleUnaryOperator fromMultiplication(final double multiplier)
	{
		return value -> value * multiplier;
	}

	public static DoubleUnaryOperator fromAdditionAndMultiplication(final double summand, final double multiplier)
	{
		return value -> (value + summand) * multiplier;
	}

	private FunctionUtility()
	{
	}
}