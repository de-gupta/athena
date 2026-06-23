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

@DisplayName("BoundedIntervalImpl")
final class BoundedIntervalImplTest
{
	private static final IntervalOrderStructure<IntElement> ORDER = IntervalOrderStructure.forElements();

	private static Bound<IntElement> closed(final int value)
	{
		return new Bound.Closed<>(element(value));
	}

	private static IntElement element(final int value)
	{
		return new IntElement(value);
	}

	private static Bound<IntElement> open(final int value)
	{
		return new Bound.Open<>(element(value));
	}

	private static BoundedIntervalImpl<IntElement> subject(final Bound<IntElement> lower, final Bound<IntElement> upper)
	{
		return BoundedIntervalImpl.of(lower, upper, ORDER);
	}

	private static UnboundedIntervalImpl<IntElement> upperBounded(final Bound<IntElement> upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(upper), ORDER);
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
		void implementsEqualityConsistently(final String as, final BoundedIntervalImpl<IntElement> left,
		                                    final Object right, final boolean expected)
		{
			assertThat(left.equals(right)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("equal instances produce the same hash code")
		void equalInstancesProduceTheSameHashCode()
		{
			var left = subject(closed(1), closed(5));
			var right = subject(closed(1), closed(5));

			assertThat(left.hashCode()).as("equal intervals hashCode").isEqualTo(right.hashCode());
		}

		private static Stream<Arguments> implementsEqualityConsistentlyCases()
		{
			var self = subject(closed(1), closed(5));

			return Stream.of(
					Arguments.of("same instance is equal", self, self, true),
					Arguments.of("same bounds are equal", subject(closed(1), closed(5)), subject(closed(1), closed(5)),
							true),
					Arguments.of("different lower bound is not equal", subject(closed(1), closed(5)),
							subject(open(1), closed(5)), false),
					Arguments.of("different upper bound is not equal", subject(closed(1), closed(5)),
							subject(closed(1), open(5)), false),
					Arguments.of("null is not equal", subject(closed(1), closed(5)), null, false),
					Arguments.of("arbitrary object is not equal", subject(closed(1), closed(5)), "interval", false)
			);
		}
	}

	@Nested
	@DisplayName("when checking whether the interval is a point")
	final class WhenCheckingWhetherTheIntervalIsAPoint
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheIntervalIsAPointCases")
		@DisplayName("reports whether the interval is a point")
		void reportsWhetherTheIntervalIsAPoint(final String as, final BoundedIntervalImpl<IntElement> interval,
		                                       final boolean expected)
		{
			assertThat(interval.isPoint()).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherTheIntervalIsAPointCases()
		{
			return Stream.of(
					Arguments.of("closed equal bounds define a point", subject(closed(3), closed(3)), true),
					Arguments.of("open lower equal bounds do not define a point", subject(open(3), closed(3)), false),
					Arguments.of("open upper equal bounds do not define a point", subject(closed(3), open(3)), false),
					Arguments.of("different closed bounds do not define a point", subject(closed(3), closed(4)), false)
			);
		}
	}

	@Nested
	@DisplayName("when combining with upper-bounded intervals")
	final class WhenCombiningWithUpperBoundedIntervals
	{
		@Test
		@DisplayName("abuts through the upper bound of the unbounded interval")
		void abutsThroughTheUpperBoundOfTheUnboundedInterval()
		{
			var interval = subject(open(3), closed(5));
			var upperBoundedInterval = upperBounded(open(3));

			assertThat(interval.abuts(upperBoundedInterval)).as("abuts").isEqualTo(false);
		}

		@Test
		@DisplayName("abuts through the upper bound of the unbounded interval")
		void abutsThroughTheUpperBoundOfTheUnboundedIntervalClosed()
		{
			var interval = subject(open(3), closed(5));
			var upperBoundedInterval = upperBounded(closed(3));

			assertThat(interval.abuts(upperBoundedInterval)).as("abuts").isEqualTo(true);
		}

		@Test
		@DisplayName("span with an upper-bounded interval stays unbounded")
		void spanWithAnUpperBoundedIntervalStaysUnbounded()
		{
			var interval = subject(closed(4), closed(6));
			var upperBoundedInterval = upperBounded(closed(3));

			assertThat(interval.span(upperBoundedInterval)).as("span")
			                                               .isEqualTo(UnboundedIntervalImpl.of(Optional.empty(),
																   Optional.of(closed(6)), ORDER));
		}
	}

	@Nested
	@DisplayName("when replacing bounds")
	final class WhenReplacingBounds
	{
		@Test
		@DisplayName("returns a bounded interval with the requested bounds")
		void returnsABoundedIntervalWithTheRequestedBounds()
		{
			var interval = subject(closed(1), closed(5));

			assertThat(interval.withBounds(open(2), open(4))).as("withBounds")
			                                                 .isEqualTo(subject(open(2), open(4)));
		}
	}
}