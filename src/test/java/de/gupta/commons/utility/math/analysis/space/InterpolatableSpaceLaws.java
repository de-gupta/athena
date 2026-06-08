package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

record InterpolatableSpaceLaws<X>(InterpolatableSpace<X> subject, X left, X interior, X right)
{
	private static final double COMPARISON_THRESHOLD = 1e-12;

	Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new MetricSpaceLaws<>(subject, left, right, interior).tests(),
				Stream.of(
						dynamicTest("parameter(left, right, left) == 0", this::parameterAtLeftEndpointIsZero),
						dynamicTest("parameter(left, right, right) == 1", this::parameterAtRightEndpointIsOne),
						dynamicTest("parameter for interior point is in (0, 1)",
								this::parameterForInteriorPointIsInUnitInterval),
						dynamicTest("compare(left, right) < 0 and compare(right, left) > 0",
								this::compareIsConsistentWithOrdering)
				)
		);
	}

	private void parameterAtLeftEndpointIsZero()
	{
		assertThat(subject.parameter(left, right, left))
				.as("parameter at left endpoint must be 0")
				.isCloseTo(0.0, within(COMPARISON_THRESHOLD));
	}

	private void parameterAtRightEndpointIsOne()
	{
		assertThat(subject.parameter(left, right, right))
				.as("parameter at right endpoint must be 1")
				.isCloseTo(1.0, within(COMPARISON_THRESHOLD));
	}

	private void parameterForInteriorPointIsInUnitInterval()
	{
		assertThat(subject.parameter(left, right, interior))
				.as("parameter for interior point must be in (0, 1)")
				.isBetween(0.0, 1.0);
	}

	private void compareIsConsistentWithOrdering()
	{
		assertThat(subject.compare(left, right))
				.as("compare(left, right) must be negative")
				.isNegative();
		assertThat(subject.compare(right, left))
				.as("compare(right, left) must be positive")
				.isPositive();
		assertThat(subject.compare(left, left))
				.as("compare(a, a) must be zero")
				.isZero();
	}
}