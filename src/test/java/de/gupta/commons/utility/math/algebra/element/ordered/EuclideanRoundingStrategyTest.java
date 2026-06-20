package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoundingStrategy")
final class EuclideanRoundingStrategyTest
{
	private void assertDivision(final String as, final long dividend, final long divisor,
	                            final RoundingStrategy<IntegersAsEuclideanDomain> strategy,
	                            final long expectedQuotient, final long expectedRemainder)
	{
		DivisionResult<IntegersAsEuclideanDomain> element = e(dividend).divide(e(divisor), strategy);
		assertThat(element.quotient()).as("%s element quotient", as).isEqualTo(e(expectedQuotient));
		assertThat(element.remainder()).as("%s element remainder", as).isEqualTo(e(expectedRemainder));

		DivisionResult<IntegersAsEuclideanDomain> structure =
				IntegerEuclideanDomainStructure.INSTANCE.divide(e(dividend), e(divisor), strategy);
		assertThat(structure.quotient()).as("%s structure quotient", as).isEqualTo(e(expectedQuotient));
		assertThat(structure.remainder()).as("%s structure remainder", as).isEqualTo(e(expectedRemainder));
	}

	private static IntegersAsEuclideanDomain e(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when rounding with floor()")
	final class WhenRoundingWithFloor
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("floorDivisionCases")
		@DisplayName("produces floor quotient and remainder")
		void producesFloorQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                       final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, RoundingStrategies.floor(), expectedQ, expectedR);
		}

		private static Stream<Arguments> floorDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L, 2L),
					Arguments.of("7 ÷ -3", 7L, -3L, -3L, -2L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L, -1L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with ceiling()")
	final class WhenRoundingWithCeiling
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("ceilingDivisionCases")
		@DisplayName("produces ceiling quotient and remainder")
		void producesCeilingQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                         final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, RoundingStrategies.ceiling(), expectedQ, expectedR);
		}

		private static Stream<Arguments> ceilingDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 3L, -2L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L, -1L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L, 1L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 3L, 2L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with truncate()")
	final class WhenRoundingWithTruncate
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("truncateDivisionCases")
		@DisplayName("produces truncated quotient and remainder")
		void producesTruncatedQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                           final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, RoundingStrategies.truncate(), expectedQ, expectedR);
		}

		private static Stream<Arguments> truncateDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L, -1L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L, 1L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L, -1L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when division is exact")
	final class WhenDivisionIsExact
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("exactDivisionCases")
		@DisplayName("all strategies produce zero remainder and the same quotient")
		void allStrategiesProduceZeroRemainderAndTheSameQuotient(final String as, final long dividend,
		                                                         final long divisor, final long expectedQ)
		{
			List<RoundingStrategy<IntegersAsEuclideanDomain>> strategies = List.of(
					RoundingStrategies.floor(),
					RoundingStrategies.ceiling(),
					RoundingStrategies.truncate()
			);
			for (RoundingStrategy<IntegersAsEuclideanDomain> strategy : strategies)
				assertDivision(as, dividend, divisor, strategy, expectedQ, 0L);
		}

		private static Stream<Arguments> exactDivisionCases()
		{
			return Stream.of(
					Arguments.of("6 ÷ 3", 6L, 3L, 2L),
					Arguments.of("-6 ÷ 3", -6L, 3L, -2L),
					Arguments.of("6 ÷ -3", 6L, -3L, -2L),
					Arguments.of("-6 ÷ -3", -6L, -3L, 2L)
			);
		}
	}

	@Nested
	@DisplayName("division identity")
	final class DivisionIdentity
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("identityCases")
		@DisplayName("dividend = quotient * divisor + remainder for all strategies")
		void dividendEqualsQuotientTimesDivisorPlusRemainder(final String as, final long dividend, final long divisor)
		{
			for (RoundingStrategy<IntegersAsEuclideanDomain> strategy : List.<RoundingStrategy<IntegersAsEuclideanDomain>>of(
					RoundingStrategies.floor(), RoundingStrategies.ceiling(), RoundingStrategies.truncate()))
			{
				DivisionResult<IntegersAsEuclideanDomain> result = e(dividend).divide(e(divisor), strategy);
				IntegersAsEuclideanDomain reconstructed =
						result.quotient().multiply(e(divisor)).add(result.remainder());
				assertThat(reconstructed).as("%s: dividend = q*d + r", as).isEqualTo(e(dividend));
			}
		}

		private static Stream<Arguments> identityCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L),
					Arguments.of("-7 ÷ 3", -7L, 3L),
					Arguments.of("7 ÷ -3", 7L, -3L),
					Arguments.of("-7 ÷ -3", -7L, -3L),
					Arguments.of("6 ÷ 3", 6L, 3L)
			);
		}
	}
}