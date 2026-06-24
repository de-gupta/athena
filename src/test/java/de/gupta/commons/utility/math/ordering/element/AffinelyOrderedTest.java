package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.algebra.element.affine.AffineSpaceLaws;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AffinelyOrdered")
final class AffinelyOrderedTest implements AffineSpaceLaws<IntegralNumber, IntegralNumber>
{
	private static IntegralNumber i(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	@Override
	@Provide
	public Arbitrary<IntegralNumber> elements()
	{
		return Arbitraries.longs().between(-1000, 1000).map(IntegralNumberFactory::of);
	}

	@Nested
	@DisplayName("when computing displacement")
	final class WhenComputingDisplacement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("displacementCases")
		@DisplayName("returns signed displacement from this to other")
		void returnsSignedDisplacementFromThisToOther(final String as,
		                                              final IntegralNumber from,
		                                              final IntegralNumber to,
		                                              final IntegralNumber expected)
		{
			assertThat(from.displacementTo(to)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> displacementCases()
		{
			return Stream.of(
					Arguments.of("3 to 7 = +4", i(3), i(7), i(4)),
					Arguments.of("7 to 3 = -4", i(7), i(3), i(-4)),
					Arguments.of("5 to 5 = 0", i(5), i(5), i(0)),
					Arguments.of("-3 to 3 = 6", i(-3), i(3), i(6))
			);
		}
	}

	@Nested
	@DisplayName("when translating")
	final class WhenTranslating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("translateCases")
		@DisplayName("returns point shifted by displacement")
		void returnsPointShiftedByDisplacement(final String as,
		                                       final IntegralNumber point,
		                                       final IntegralNumber displacement,
		                                       final IntegralNumber expected)
		{
			assertThat(point.translate(displacement)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> translateCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", i(3), i(4), i(7)),
					Arguments.of("7 + (-4) = 3", i(7), i(-4), i(3)),
					Arguments.of("5 + 0 = 5", i(5), i(0), i(5))
			);
		}
	}

	@Nested
	@DisplayName("when verifying ordering consistency")
	final class WhenVerifyingOrderingConsistency
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("orderCorrelationCases")
		@DisplayName("displacement sign correlates with order: a < b iff displacement is positive")
		void displacementSignCorrelatesWithOrder(final String as,
		                                         final IntegralNumber from,
		                                         final IntegralNumber to,
		                                         final boolean expectedPositive,
		                                         final boolean expectedNegative)
		{
			assertThat(from.displacementTo(to).isPositive()).as("%s: positive", as).isEqualTo(expectedPositive);
			assertThat(from.displacementTo(to).isNegative()).as("%s: negative", as).isEqualTo(expectedNegative);
		}

		private static Stream<Arguments> orderCorrelationCases()
		{
			return Stream.of(
					Arguments.of("3 < 7: positive displacement", i(3), i(7), true, false),
					Arguments.of("7 > 3: negative displacement", i(7), i(3), false, true),
					Arguments.of("-5 < 0: positive displacement", i(-5), i(0), true, false),
					Arguments.of("0 > -5: negative displacement", i(0), i(-5), false, true)
			);
		}
	}
}
