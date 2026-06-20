package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EuclideanRoundingStrategy")
final class EuclideanRoundingStrategyTest
{
	private static IntegersAsEuclideanDomain element(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when rounding with FLOOR")
	final class WhenRoundingWithFloor
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesFloorQuotientCases")
		@DisplayName("produces floor quotient")
		void producesFloorQuotient(final String as, final long dividend, final long divisor, final long expected)
		{
			assertThat(element(dividend).euclideanQuotient(element(divisor), EuclideanRoundingStrategy.FLOOR))
					.as("%s — element side", as).isEqualTo(element(expected));
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.euclideanQuotient(element(dividend), element(divisor),
					EuclideanRoundingStrategy.FLOOR))
					.as("%s — structure side", as).isEqualTo(element(expected));
		}

		private static Stream<Arguments> producesFloorQuotientCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L),
					Arguments.of("7 ÷ -3", 7L, -3L, -3L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with CEILING")
	final class WhenRoundingWithCeiling
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesCeilingQuotientCases")
		@DisplayName("produces ceiling quotient")
		void producesCeilingQuotient(final String as, final long dividend, final long divisor, final long expected)
		{
			assertThat(element(dividend).euclideanQuotient(element(divisor), EuclideanRoundingStrategy.CEILING))
					.as("%s — element side", as).isEqualTo(element(expected));
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.euclideanQuotient(element(dividend), element(divisor),
					EuclideanRoundingStrategy.CEILING))
					.as("%s — structure side", as).isEqualTo(element(expected));
		}

		private static Stream<Arguments> producesCeilingQuotientCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 3L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 3L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with TRUNCATE")
	final class WhenRoundingWithTruncate
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesTruncatedQuotientCases")
		@DisplayName("produces quotient truncated toward zero")
		void producesTruncatedQuotientCases(final String as, final long dividend, final long divisor,
		                                    final long expected)
		{
			assertThat(element(dividend).euclideanQuotient(element(divisor), EuclideanRoundingStrategy.TRUNCATE))
					.as("%s — element side", as).isEqualTo(element(expected));
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.euclideanQuotient(element(dividend), element(divisor),
					EuclideanRoundingStrategy.TRUNCATE))
					.as("%s — structure side", as).isEqualTo(element(expected));
		}

		private static Stream<Arguments> producesTruncatedQuotientCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L)
			);
		}
	}

	@Nested
	@DisplayName("when division is exact")
	final class WhenDivisionIsExact
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("allStrategiesAgreeOnExactDivisionCases")
		@DisplayName("all strategies produce the same quotient")
		void allStrategiesProduceTheSameQuotient(final String as, final long dividend, final long divisor,
		                                         final long expected)
		{
			IntegersAsEuclideanDomain d = element(dividend);
			IntegersAsEuclideanDomain v = element(divisor);

			for (EuclideanRoundingStrategy strategy : EuclideanRoundingStrategy.values())
			{
				assertThat(d.euclideanQuotient(v, strategy))
						.as("%s with %s — element side", as, strategy)
						.isEqualTo(element(expected));
				assertThat(IntegerEuclideanDomainStructure.INSTANCE.euclideanQuotient(d, v, strategy))
						.as("%s with %s — structure side", as, strategy)
						.isEqualTo(element(expected));
			}
		}

		private static Stream<Arguments> allStrategiesAgreeOnExactDivisionCases()
		{
			return Stream.of(
					Arguments.of("6 ÷ 3", 6L, 3L, 2L),
					Arguments.of("-6 ÷ 3", -6L, 3L, -2L),
					Arguments.of("6 ÷ -3", 6L, -3L, -2L),
					Arguments.of("-6 ÷ -3", -6L, -3L, 2L)
			);
		}
	}
}