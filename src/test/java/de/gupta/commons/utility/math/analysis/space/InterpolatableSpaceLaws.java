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
						dynamicTest("compare(left, right) < 0", this::compareIsConsistentWithOrdering),
						dynamicTest("space is usable as Comparator<X>", this::spaceIsUsableAsComparator)
				)
		);
	}

	private void parameterAtLeftEndpointIsZero()
	{
		assertThat(subject.parameter(left, right, left)).isCloseTo(0.0, within(COMPARISON_THRESHOLD));
	}

	private void parameterAtRightEndpointIsOne()
	{
		assertThat(subject.parameter(left, right, right)).isCloseTo(1.0, within(COMPARISON_THRESHOLD));
	}

	private void parameterForInteriorPointIsInUnitInterval()
	{
		assertThat(subject.parameter(left, right, interior)).isBetween(0.0, 1.0);
	}

	private void compareIsConsistentWithOrdering()
	{
		assertThat(subject.compare(left, right)).isNegative();
		assertThat(subject.compare(right, left)).isPositive();
		assertThat(subject.compare(left, left)).isZero(); // called on itself? why?
	}

	private void spaceIsUsableAsComparator()
	{
		assertThat(subject.compare(left, right)).isNegative();
	}
}