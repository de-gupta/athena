package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MetricSpace laws")
abstract class MetricSpaceContractTest<X>
{
	protected abstract MetricSpace<X> subject();

	protected abstract X pointA();

	protected abstract X pointB();

	protected abstract X pointC();

	@Test
	@DisplayName("distance from a point to itself is zero: d(a, a) == 0")
	void selfDistanceIsZero()
	{
		assertThat(subject().distance(pointA(), pointA())).isZero();
	}

	@Test
	@DisplayName("distance is non-negative: d(a, b) >= 0")
	void distanceIsNonNegative()
	{
		assertThat(subject().distance(pointA(), pointB())).isGreaterThanOrEqualTo(0.0);
	}

	@Test
	@DisplayName("distance is symmetric: d(a, b) == d(b, a)")
	void distanceIsSymmetric()
	{
		assertThat(subject().distance(pointA(), pointB()))
				.isEqualTo(subject().distance(pointB(), pointA()));
	}

	@Test
	@DisplayName("triangle inequality holds: d(a, c) <= d(a, b) + d(b, c)")
	void triangleInequalityHolds()
	{
		double distanceAB = subject().distance(pointA(), pointB());
		double distanceBC = subject().distance(pointB(), pointC());
		double distanceAC = subject().distance(pointA(), pointC());

		assertThat(distanceAC).isLessThanOrEqualTo(distanceAB + distanceBC + 1e-12);
	}
}
