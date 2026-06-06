package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DifferentiableSpace laws")
abstract class DifferentiableSpaceContractTest<X> extends InterpolatableSpaceContractTest<X>
{
	@Override
	protected abstract DifferentiableSpace<X> subject();

	@Test
	@DisplayName("span is non-negative: span(a, b) >= 0")
	void spanIsNonNegative()
	{
		assertThat(subject().span(pointA(), pointB())).isGreaterThanOrEqualTo(0.0);
	}

	@Test
	@DisplayName("span is symmetric: span(a, b) == span(b, a)")
	void spanIsSymmetric()
	{
		assertThat(subject().span(pointA(), pointB()))
				.isCloseTo(subject().span(pointB(), pointA()), within(1e-12));
	}

	@Test
	@DisplayName("span of equal points is zero: span(a, a) == 0")
	void spanOfEqualPointsIsZero()
	{
		assertThat(subject().span(pointA(), pointA())).isCloseTo(0.0, within(1e-12));
	}

	@Test
	@DisplayName("span throws NullPointerException when left is null")
	void spanThrowsWhenLeftIsNull()
	{
		assertThatNullPointerException()
				.isThrownBy(() -> subject().span(null, pointB()))
				.withMessage("left");
	}

	@Test
	@DisplayName("span throws NullPointerException when right is null")
	void spanThrowsWhenRightIsNull()
	{
		assertThatNullPointerException()
				.isThrownBy(() -> subject().span(pointA(), null))
				.withMessage("right");
	}
}