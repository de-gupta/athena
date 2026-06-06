package de.gupta.commons.utility.math.analysis.space;

import de.gupta.aletheia.collection.folding.Loom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

//question: what are we really testing here? just usual arithmetic? this class seems useless
@DisplayName("RealLinearSpace contract")
final class RealLinearSpaceContractTest
{
	private static final RealLinearSpace<Double> REAL_DOUBLE_SPACE = new RealLinearSpace<>()
	{
		@Override
		public Double scale(final double scalar, final Double vector)
		{
			return scalar * vector;
		}

		@Override
		public Double add(final Double left, final Double right)
		{
			return left + right;
		}

		@Override
		public Double negate(final Double element)
		{
			return -element;
		}

		@Override
		public Double zero()
		{
			return 0.0;
		}
	};

	@Nested
	@DisplayName("scale(scalar, vector)")
	final class Scale
	{
		@Test
		@DisplayName("scale by zero returns zero element")
		void scaleByZeroReturnsZeroElement()
		{
			assertThat(REAL_DOUBLE_SPACE.scale(0.0, 7.0))
					.isCloseTo(REAL_DOUBLE_SPACE.zero(), within(1e-12));
		}

		@Test
		@DisplayName("scale by one returns the vector unchanged")
		void scaleByOneReturnsSameVector()
		{
			assertThat(REAL_DOUBLE_SPACE.scale(1.0, 5.0)).isCloseTo(5.0, within(1e-12));
		}

		@Test
		@DisplayName("scale by minus one returns the negated vector")
		void scaleByMinusOneReturnsNegatedVector()
		{
			assertThat(REAL_DOUBLE_SPACE.scale(-1.0, 5.0))
					.isCloseTo(REAL_DOUBLE_SPACE.negate(5.0), within(1e-12));
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("scalingCases")
		@DisplayName("applies scalar multiplication correctly")
		void appliesScalarMultiplication(final double scalar, final double vector, final String description,
		                                 final double expected)
		{
			assertThat(REAL_DOUBLE_SPACE.scale(scalar, vector))
					.as(description)
					.isCloseTo(expected, within(1e-12));
		}

		private static Stream<Arguments> scalingCases()
		{
			return Stream.of(
					Arguments.of(2.0, 3.0, "2 * 3 = 6", 6.0),
					Arguments.of(0.5, 10.0, "0.5 * 10 = 5", 5.0),
					Arguments.of(-3.0, 4.0, "-3 * 4 = -12", -12.0),
					Arguments.of(1.5, -2.0, "1.5 * (-2) = -3", -3.0)
			);
		}
	}

	@Nested
	@DisplayName("blend(a, b, lambda) — default implementation")
	final class Blend
	{
		@Test
		@DisplayName("blend at lambda zero returns a")
		void blendAtZeroReturnsA()
		{
			assertThat(REAL_DOUBLE_SPACE.blend(3.0, 9.0, 0.0)).isCloseTo(3.0, within(1e-12));
		}

		@Test
		@DisplayName("blend at lambda one returns b")
		void blendAtOneReturnsB()
		{
			assertThat(REAL_DOUBLE_SPACE.blend(3.0, 9.0, 1.0)).isCloseTo(9.0, within(1e-12));
		}

		@Test
		@DisplayName("blend at lambda one half returns the midpoint")
		void blendAtOneHalfReturnsMidpoint()
		{
			assertThat(REAL_DOUBLE_SPACE.blend(2.0, 8.0, 0.5)).isCloseTo(5.0, within(1e-12));
		}

		@ParameterizedTest(name = "{3}")
		@MethodSource("blendConsistencyCases")
		@DisplayName("is consistent with scale and add")
		void isConsistentWithScaleAndAdd(final double a, final double b, final double lambda,
		                                 final String description, final double expected)
		{
			double blendResult = REAL_DOUBLE_SPACE.blend(a, b, lambda);
			double scaleAddResult = REAL_DOUBLE_SPACE.add(
					REAL_DOUBLE_SPACE.scale(1.0 - lambda, a),
					REAL_DOUBLE_SPACE.scale(lambda, b)
			);

			assertThat(blendResult)
					.as(description)
					.isCloseTo(expected, within(1e-12))
					.isCloseTo(scaleAddResult, within(1e-12));
		}

		@Test
		@DisplayName("throws NullPointerException when a is null")
		void throwsWhenFirstArgumentIsNull()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> REAL_DOUBLE_SPACE.blend(null, 5.0, 0.5))
					.withMessage("a");
		}

		@Test
		@DisplayName("throws NullPointerException when b is null")
		void throwsWhenSecondArgumentIsNull()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> REAL_DOUBLE_SPACE.blend(5.0, null, 0.5))
					.withMessage("b");
		}

		private static Stream<Arguments> blendConsistencyCases()
		{
			return Stream.of(
					Arguments.of(0.0, 10.0, 0.25, "one quarter of the way", 2.5),
					Arguments.of(0.0, 10.0, 0.75, "three quarters of the way", 7.5),
					Arguments.of(-6.0, 6.0, 0.5, "midpoint of symmetric interval", 0.0),
					Arguments.of(100.0, 200.0, 0.3, "thirty percent from 100 to 200", 130.0)
			);
		}
	}

	@Nested
	@DisplayName("inherited group operations")
	final class InheritedGroupOperations
	{
		@Test
		@DisplayName("zero is the additive identity")
		void zeroIsAdditiveIdentity()
		{
			assertThat(REAL_DOUBLE_SPACE.add(5.0, REAL_DOUBLE_SPACE.zero())).isCloseTo(5.0, within(1e-12));
			assertThat(REAL_DOUBLE_SPACE.add(REAL_DOUBLE_SPACE.zero(), 5.0)).isCloseTo(5.0, within(1e-12));
		}

		@Test
		@DisplayName("adding a vector and its negation returns zero")
		void vectorPlusNegationIsZero()
		{
			double result = REAL_DOUBLE_SPACE.add(7.0, REAL_DOUBLE_SPACE.negate(7.0));

			assertThat(result).isCloseTo(REAL_DOUBLE_SPACE.zero(), within(1e-12));
		}

		@Test
		@DisplayName("subtract is consistent with add and negate")
		void subtractIsConsistentWithAddAndNegate()
		{
			double subtractResult = REAL_DOUBLE_SPACE.subtract(10.0, 3.0);
			double addNegateResult = REAL_DOUBLE_SPACE.add(10.0, REAL_DOUBLE_SPACE.negate(3.0));

			assertThat(subtractResult).isCloseTo(addNegateResult, within(1e-12));
		}

		@Test
		@DisplayName("addAll over a non-empty collection returns the sum")
		void addAllReturnsSum()
		{
			double result = REAL_DOUBLE_SPACE.addAll(Loom.thread(java.util.List.of(1.0, 2.0, 3.0, 4.0)));

			assertThat(result).isCloseTo(10.0, within(1e-12));
		}
	}
}