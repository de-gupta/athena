package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.DifferentiableSpaceLaws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.stream.Stream;

@DisplayName("BigDecimalInterpolatableSpace")
final class BigDecimalInterpolatableSpaceTest
{
	private static final BigDecimal ONE = BigDecimal.ONE;
	private static final BigDecimal THREE = new BigDecimal("3");
	private static final BigDecimal FIVE = new BigDecimal("5");
	private static final BigDecimal NINE = new BigDecimal("9");

	@TestFactory
	@DisplayName("LINEAR — satisfies all differentiable space laws")
	Stream<DynamicTest> linearLaws()
	{
		return new DifferentiableSpaceLaws<>(BigDecimalInterpolatableSpace.LINEAR, ONE, FIVE, NINE).tests();
	}

	@TestFactory
	@DisplayName("LOG — satisfies all differentiable space laws")
	Stream<DynamicTest> logLaws()
	{
		return new DifferentiableSpaceLaws<>(BigDecimalInterpolatableSpace.LOG, ONE, THREE, NINE).tests();
	}
}
