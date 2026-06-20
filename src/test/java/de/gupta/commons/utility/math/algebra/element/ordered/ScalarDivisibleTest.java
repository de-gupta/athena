package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScalarDivisible")
final class ScalarDivisibleTest
{
	private static IntegersAsEuclideanDomain e(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("element side")
	final class ElementSide
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("scalarDivisionCases")
		@DisplayName("divides element by scalar and satisfies dividend = quotient * scalar + remainder")
		void dividesElementByScalarCorrectly(final String as, final long dividend, final long scalar,
		                                     final long expectedQ, final long expectedR)
		{
			DivisionResult<IntegersAsEuclideanDomain> result =
					e(dividend).divide(scalar, RoundingStrategies.floor());

			assertThat(result.quotient()).as("%s: quotient", as).isEqualTo(e(expectedQ));
			assertThat(result.remainder()).as("%s: remainder", as).isEqualTo(e(expectedR));
			assertThat(result.quotient().multiply(e(scalar)).add(result.remainder()))
					.as("%s: identity q*scalar + r == dividend", as).isEqualTo(e(dividend));
		}

		private static Stream<Arguments> scalarDivisionCases()
		{
			return Stream.of(
					Arguments.of("420 ÷ 20 (SMA example)", 420L, 20L, 21L, 0L),
					Arguments.of("100 ÷ 3", 100L, 3L, 33L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L, 2L),
					Arguments.of("0 ÷ 5", 0L, 5L, 0L, 0L),
					Arguments.of("1 ÷ 1", 1L, 1L, 1L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("structure side")
	final class StructureSide
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("scalarDivisionCases")
		@DisplayName("divides element by scalar and satisfies dividend = quotient * scalar + remainder")
		void dividesElementByScalarCorrectly(final String as, final long dividend, final long scalar,
		                                     final long expectedQ, final long expectedR)
		{
			DivisionResult<IntegersAsEuclideanDomain> result =
					IntegerEuclideanDomainStructure.INSTANCE.divide(e(dividend), scalar, RoundingStrategies.floor());

			assertThat(result.quotient()).as("%s: quotient", as).isEqualTo(e(expectedQ));
			assertThat(result.remainder()).as("%s: remainder", as).isEqualTo(e(expectedR));
			assertThat(result.quotient().multiply(e(scalar)).add(result.remainder()))
					.as("%s: identity q*scalar + r == dividend", as).isEqualTo(e(dividend));
		}

		private static Stream<Arguments> scalarDivisionCases()
		{
			return Stream.of(
					Arguments.of("420 ÷ 20 (SMA example)", 420L, 20L, 21L, 0L),
					Arguments.of("100 ÷ 3", 100L, 3L, 33L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L, 2L),
					Arguments.of("0 ÷ 5", 0L, 5L, 0L, 0L),
					Arguments.of("1 ÷ 1", 1L, 1L, 1L, 0L)
			);
		}
	}
}