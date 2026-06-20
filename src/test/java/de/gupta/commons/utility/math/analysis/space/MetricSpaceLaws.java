package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record MetricSpaceLaws<X>(MetricSpace<X> subject, X pointA, X pointB, X pointC)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("d(a, a) == 0", this::selfDistanceIsZero),
				dynamicTest("d(a, b) >= 0", this::distanceIsNonNegative),
				dynamicTest("d(a, b) == d(b, a)", this::distanceIsSymmetric),
				dynamicTest("d(a, c) <= d(a, b) + d(b, c)", this::triangleInequalityHolds)
		);
	}

	private void selfDistanceIsZero()
	{
		assertThat(subject.distance(pointA, pointA))
				.as("d(a, a) must be zero")
				.isZero();
	}

	private void distanceIsNonNegative()
	{
		assertThat(subject.distance(pointA, pointB))
				.as("d(a, b) must be non-negative")
				.isGreaterThanOrEqualTo(0.0);
	}

	private void distanceIsSymmetric()
	{
		assertThat(subject.distance(pointA, pointB))
				.as("d(a, b) must equal d(b, a)")
				.isEqualTo(subject.distance(pointB, pointA));
	}

	private void triangleInequalityHolds()
	{
		double distanceAB = subject.distance(pointA, pointB);
		double distanceBC = subject.distance(pointB, pointC);
		double distanceAC = subject.distance(pointA, pointC);

		assertThat(distanceAC)
				.as("d(a, c) must not exceed d(a, b) + d(b, c)")
				.isLessThanOrEqualTo(distanceAB + distanceBC + 1e-12);
	}
}
