package de.gupta.commons.utility.math.ordering.bound;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bounds")
final class BoundsTest
{
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

	private record IntElement(int value) implements TotallyOrdered<IntElement>
	{
		@Override
		public OrderRelation compare(final IntElement other)
		{
			return OrderRelation.from(Integer.compare(value, other.value));
		}
	}

	@Nested
	@DisplayName("when checking bound equality")
	final class WhenCheckingBoundEquality
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherBoundsAreEqualCases")
		@DisplayName("reports whether the bounds are equal")
		void reportsWhetherBoundsAreEqual(final String as, final Bound<IntElement> first,
		                                  final Bound<IntElement> second,
		                                  final boolean expected)
		{
			assertThat(Bounds.areEqual(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherBoundsAreEqualCases()
		{
			var sameOpen = open(3);
			var sameClosed = closed(3);

			return Stream.of(
					Arguments.of("same open instance is equal", sameOpen, sameOpen, true),
					Arguments.of("same closed instance is equal", sameClosed, sameClosed, true),
					Arguments.of("open bounds with same value are equal", open(3), open(3), true),
					Arguments.of("closed bounds with same value are equal", closed(3), closed(3), true),
					Arguments.of("different openness with same value is not equal", closed(3), open(3), false),
					Arguments.of("different values are not equal", closed(3), closed(4), false)
			);
		}
	}

	@Nested
	@DisplayName("when checking bound shape")
	final class WhenCheckingBoundShape
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherBothBoundsAreOpenCases")
		@DisplayName("reports whether both bounds are open")
		void reportsWhetherBothBoundsAreOpen(final String as, final Bound<IntElement> first,
		                                     final Bound<IntElement> second, final boolean expected)
		{
			assertThat(Bounds.areBothOpen(first, second)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherBothBoundsAreClosedCases")
		@DisplayName("reports whether both bounds are closed")
		void reportsWhetherBothBoundsAreClosed(final String as, final Bound<IntElement> first,
		                                       final Bound<IntElement> second, final boolean expected)
		{
			assertThat(Bounds.areBothClosed(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherBothBoundsAreOpenCases()
		{
			return Stream.of(
					Arguments.of("two open bounds are both open", open(1), open(2), true),
					Arguments.of("closed lower makes the pair not both open", closed(1), open(2), false),
					Arguments.of("closed upper makes the pair not both open", open(1), closed(2), false),
					Arguments.of("two closed bounds are not both open", closed(1), closed(2), false)
			);
		}

		private static Stream<Arguments> reportsWhetherBothBoundsAreClosedCases()
		{
			return Stream.of(
					Arguments.of("two closed bounds are both closed", closed(1), closed(2), true),
					Arguments.of("open lower makes the pair not both closed", open(1), closed(2), false),
					Arguments.of("open upper makes the pair not both closed", closed(1), open(2), false),
					Arguments.of("two open bounds are not both closed", open(1), open(2), false)
			);
		}
	}

	@Nested
	@DisplayName("when comparing bound values")
	final class WhenComparingBoundValues
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherBoundValuesAreEqualCases")
		@DisplayName("reports whether the bound values are equal")
		void reportsWhetherBoundValuesAreEqual(final String as, final Bound<IntElement> first,
		                                       final Bound<IntElement> second, final boolean expected)
		{
			assertThat(Bounds.areValuesEqual(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherBoundValuesAreEqualCases()
		{
			return Stream.of(
					Arguments.of("equal values compare equal", closed(3), open(3), true),
					Arguments.of("smaller and larger values are not equal", closed(3), closed(4), false),
					Arguments.of("larger and smaller values are not equal", open(4), open(3), false)
			);
		}
	}
}