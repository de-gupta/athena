package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

record DifferentiableSpaceLaws<X>(DifferentiableSpace<X> subject, X left, X interior, X right)
{
	Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new InterpolatableSpaceLaws<>(subject, left, interior, right).tests(),
				Stream.of(
						dynamicTest("span(a, b) >= 0", this::spanIsNonNegative),
						dynamicTest("span(a, b) == span(b, a)", this::spanIsSymmetric),
						dynamicTest("span(a, a) == 0", this::spanOfEqualPointsIsZero),
						dynamicTest("span(null, b) throws NullPointerException", this::spanThrowsOnNullLeft),
						dynamicTest("span(a, null) throws NullPointerException", this::spanThrowsOnNullRight)
				)
		);
	}

	private void spanIsNonNegative()
	{
		assertThat(subject.span(left, right)).isGreaterThanOrEqualTo(0.0);
	}

	private void spanIsSymmetric()
	{
		assertThat(subject.span(left, right)).isCloseTo(subject.span(right, left), within(1e-12));
	}

	private void spanOfEqualPointsIsZero()
	{
		assertThat(subject.span(left, left)).isCloseTo(0.0, within(1e-12));
	}

	private void spanThrowsOnNullLeft()
	{
		assertThatNullPointerException()
				.isThrownBy(() -> subject.span(null, right))
				.withMessage("left");
	}

	private void spanThrowsOnNullRight()
	{
		assertThatNullPointerException()
				.isThrownBy(() -> subject.span(left, null))
				.withMessage("right");
	}
}