package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("InterpolatableSpace laws")
abstract class InterpolatableSpaceContractTest<X> extends MetricSpaceContractTest<X>
{
	@Override
	protected abstract InterpolatableSpace<X> subject();

	protected abstract X interior();

	@Test
	@DisplayName("parameter at left endpoint is zero: parameter(a, b, a) == 0")
	void parameterAtLeftEndpointIsZero()
	{
		X left = pointA();
		X right = pointB();

		assertThat(subject().parameter(left, right, left)).isCloseTo(0.0, within(1e-12));
	}

	@Test
	@DisplayName("parameter at right endpoint is one: parameter(a, b, b) == 1")
	void parameterAtRightEndpointIsOne()
	{
		X left = pointA();
		X right = pointB();

		assertThat(subject().parameter(left, right, right)).isCloseTo(1.0, within(1e-12));
	}

	@Test
	@DisplayName("parameter for interior point is in (0, 1)")
	void parameterForInteriorPointIsInUnitInterval()
	{
		double result = subject().parameter(pointA(), pointB(), interior());

		assertThat(result).isBetween(0.0, 1.0);
	}

	@Test
	@DisplayName("compare is consistent with ordering: a < b implies compare(a, b) < 0")
	void compareIsConsistentWithOrdering()
	{
		assertThat(subject().compare(pointA(), pointB())).isNegative();
		assertThat(subject().compare(pointB(), pointA())).isPositive();
		assertThat(subject().compare(pointA(), pointA())).isZero();
	}

	@Test
	@DisplayName("the space itself satisfies the Comparator contract")
	void spaceIsUsableAsComparator()
	{
		java.util.Comparator<X> comparator = subject();

		assertThat(comparator.compare(pointA(), pointB())).isNegative();
	}
}