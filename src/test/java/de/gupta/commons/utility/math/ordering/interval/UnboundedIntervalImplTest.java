package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UnboundedIntervalImpl")
final class UnboundedIntervalImplTest
{
	private static final IntervalOrderStructure<IntElement> ORDER = IntervalOrderStructure.forElements();

	private static UnboundedIntervalImpl<IntElement> lowerBounded(final int value)
	{
		return UnboundedIntervalImpl.of(Optional.of(closed(value)), Optional.empty(), ORDER);
	}

	private static Bound<IntElement> closed(final int value)
	{
		return new Bound.Closed<>(element(value));
	}

	private static IntElement element(final int value)
	{
		return new IntElement(value);
	}

	private static UnboundedIntervalImpl<IntElement> upperBounded(final int value)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(closed(value)), ORDER);
	}

	private record IntElement(int value) implements TotallyOrdered<IntElement>
	{
		@Override
		public OrderRelation compare(final IntElement other)
		{
			return OrderRelation.from(Integer.compare(value, other.value));
		}
	}

	@Nested
	@DisplayName("when comparing identity")
	final class WhenComparingIdentity
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("implementsEqualityConsistentlyCases")
		@DisplayName("implements equality consistently")
		void implementsEqualityConsistently(final String as, final UnboundedIntervalImpl<IntElement> left,
		                                    final Object right, final boolean expected)
		{
			assertThat(left.equals(right)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("equal instances produce the same hash code")
		void equalInstancesProduceTheSameHashCode()
		{
			var left = lowerBounded(3);
			var right = lowerBounded(3);

			assertThat(left.hashCode()).as("equal intervals hashCode").isEqualTo(right.hashCode());
		}

		private static Stream<Arguments> implementsEqualityConsistentlyCases()
		{
			var self = lowerBounded(3);

			return Stream.of(
					Arguments.of("same instance is equal", self, self, true),
					Arguments.of("same lower bound is equal", lowerBounded(3), lowerBounded(3), true),
					Arguments.of("same upper bound is equal", upperBounded(5), upperBounded(5), true),
					Arguments.of("different side is not equal", lowerBounded(3), upperBounded(3), false),
					Arguments.of("different bound value is not equal", lowerBounded(3), lowerBounded(4), false),
					Arguments.of("null is not equal", lowerBounded(3), null, false),
					Arguments.of("arbitrary object is not equal", lowerBounded(3), "interval", false)
			);
		}
	}

	@Nested
	@DisplayName("when spanning with valid interval shapes")
	final class WhenSpanningWithValidIntervalShapes
	{
		@Test
		@DisplayName("span with a bounded interval stays unbounded")
		void spanWithABoundedIntervalStaysUnbounded()
		{
			var interval = lowerBounded(3);
			var bounded = BoundedIntervalImpl.of(closed(4), closed(6), ORDER);

			assertThat(interval.span(bounded)).as("span")
			                                  .isEqualTo(lowerBounded(3));
		}

		@Test
		@DisplayName("span with the opposite unbounded side becomes all")
		void spanWithTheOppositeUnboundedSideBecomesAll()
		{
			var interval = lowerBounded(3);
			var other = upperBounded(5);

			assertThat(interval.span(other)).as("span")
			                                .isEqualTo(UnboundedIntervalImpl.of(Optional.empty(), Optional.empty(),
													ORDER));
		}
	}

	@Nested
	@DisplayName("when replacing bounds")
	final class WhenReplacingBounds
	{
		@Test
		@DisplayName("returns an unbounded interval with the requested bounds")
		void returnsAnUnboundedIntervalWithTheRequestedBounds()
		{
			var interval = lowerBounded(3);

			assertThat(interval.withBounds(Optional.empty(), Optional.of(closed(5)))).as("withBounds")
			                                                                         .isEqualTo(upperBounded(5));
		}
	}
}