package de.gupta.commons.utility.math.ordering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ordering")
final class OrderingTest
{
	@Nested
	@DisplayName("when checking incomparability")
	final class WhenCheckingIncomparability
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherOrderingIsIncomparableCases")
		@DisplayName("reports whether the ordering is incomparable")
		void reportsWhetherOrderingIsIncomparable(final String as, final Ordering ordering, final boolean expected)
		{
			assertThat(ordering.isIncomparable()).as("%s: isIncomparable", as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherOrderingIsIncomparableCases()
		{
			return Stream.of(
					Arguments.of("LESS_THAN is comparable", OrderRelation.LESS_THAN, false),
					Arguments.of("EQUAL is comparable", OrderRelation.EQUAL, false),
					Arguments.of("GREATER_THAN is comparable", OrderRelation.GREATER_THAN, false),
					Arguments.of("Incomparable is incomparable", Incomparable.INSTANCE, true)
			);
		}
	}
}
