package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

//question: what are we really testing here? just linear combination? this class seems useless
@DisplayName("BlendableSpace contract")
final class BlendableSpaceContractTest
{
	private static final BlendableSpace<Double> REAL_BLEND = (a, b, lambda) -> (1.0 - lambda) * a + lambda * b;

	@Nested
	@DisplayName("blend(a, b, 0.0)")
	final class BlendAtZero
	{
		@Test
		@DisplayName("returns a")
		void returnsA()
		{
			assertThat(REAL_BLEND.blend(3.0, 9.0, 0.0)).isCloseTo(3.0, within(1e-12));
		}

		@Test
		@DisplayName("returns a regardless of b")
		void returnsARegardlessOfB()
		{
			assertThat(REAL_BLEND.blend(5.0, 1000.0, 0.0)).isCloseTo(5.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("blend(a, b, 1.0)")
	final class BlendAtOne
	{
		@Test
		@DisplayName("returns b")
		void returnsB()
		{
			assertThat(REAL_BLEND.blend(3.0, 9.0, 1.0)).isCloseTo(9.0, within(1e-12));
		}

		@Test
		@DisplayName("returns b regardless of a")
		void returnsBRegardlessOfA()
		{
			assertThat(REAL_BLEND.blend(1000.0, 7.0, 1.0)).isCloseTo(7.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("blend(a, b, 0.5)")
	final class BlendAtMidpoint
	{
		@Test
		@DisplayName("returns the arithmetic mean of a and b")
		void returnsArithmeticMean()
		{
			assertThat(REAL_BLEND.blend(2.0, 8.0, 0.5)).isCloseTo(5.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("blend(a, b, lambda) for lambda in (0, 1)")
	final class BlendInterior
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("blendCases")
		@DisplayName("returns the expected blended value")
		void returnsExpectedBlendedValue(final double a, final double b, final double lambda,
		                                 final String description, final double expected)
		{
			assertThat(REAL_BLEND.blend(a, b, lambda))
					.as(description)
					.isCloseTo(expected, within(1e-12));
		}

		private static Stream<Arguments> blendCases()
		{
			return Stream.of(
					Arguments.of(0.0, 10.0, 0.25, "one quarter from 0 to 10", 2.5),
					Arguments.of(0.0, 10.0, 0.75, "three quarters from 0 to 10", 7.5),
					Arguments.of(-4.0, 4.0, 0.5, "midpoint of symmetric interval", 0.0),
					Arguments.of(100.0, 200.0, 0.1, "ten percent of the way from 100 to 200", 110.0)
			);
		}
	}
}