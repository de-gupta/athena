package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntervalOrderStructure")
final class IntervalOrderStructureTest
{
	private static final IntervalOrderStructure<Integer> SUBJECT =
			IntervalOrderStructure.of(IntegerNaturalOrder.INSTANCE);

	private static Bound<Integer> closed(final int value)
	{
		return new Bound.Closed<>(value);
	}

	private static Bound<Integer> open(final int value)
	{
		return new Bound.Open<>(value);
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
	@DisplayName("when creating interval order structures")
	final class WhenCreatingIntervalOrderStructures
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("delegatesToTheSuppliedOrderCases")
		@DisplayName("delegates to the supplied order")
		void delegatesToTheSuppliedOrder(final String as, final int left, final int right,
		                                 final OrderRelation expected)
		{
			assertThat(SUBJECT.compare(left, right)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("comparesUsingTheElementOrderCases")
		@DisplayName("compares using the element order")
		void comparesUsingTheElementOrder(final String as, final IntElement left, final IntElement right,
		                                  final OrderRelation expected)
		{
			var structure = IntervalOrderStructure.<IntElement>forElements();

			assertThat(structure.compare(left, right)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> delegatesToTheSuppliedOrderCases()
		{
			return Stream.of(
					Arguments.of("smaller integer compares as LESS_THAN", 1, 3, OrderRelation.LESS_THAN),
					Arguments.of("equal integer compares as EQUAL", 3, 3, OrderRelation.EQUAL),
					Arguments.of("larger integer compares as GREATER_THAN", 5, 3, OrderRelation.GREATER_THAN)
			);
		}

		private static Stream<Arguments> comparesUsingTheElementOrderCases()
		{
			return Stream.of(
					Arguments.of("smaller element compares as LESS_THAN", new IntElement(1), new IntElement(3),
							OrderRelation.LESS_THAN),
					Arguments.of("equal element compares as EQUAL", new IntElement(3), new IntElement(3),
							OrderRelation.EQUAL),
					Arguments.of("larger element compares as GREATER_THAN", new IntElement(5), new IntElement(3),
							OrderRelation.GREATER_THAN)
			);
		}
	}

	@Nested
	@DisplayName("when checking containment against a single bound")
	final class WhenCheckingContainmentAgainstASingleBound
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheLowerBoundHarboursTheElementCases")
		@DisplayName("reports whether the lower bound harbours the element")
		void reportsWhetherTheLowerBoundHarboursTheElement(final String as, final Bound<Integer> bound,
		                                                   final int element, final boolean expected)
		{
			assertThat(SUBJECT.boundHarboursElementFromBelow(bound, element)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheUpperBoundHarboursTheElementCases")
		@DisplayName("reports whether the upper bound harbours the element")
		void reportsWhetherTheUpperBoundHarboursTheElement(final String as, final Bound<Integer> bound,
		                                                   final int element, final boolean expected)
		{
			assertThat(SUBJECT.boundHarboursElementFromAbove(bound, element)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherTheLowerBoundHarboursTheElementCases()
		{
			return Stream.of(
					Arguments.of("closed lower includes its value", closed(3), 3, true),
					Arguments.of("closed lower includes larger values", closed(3), 4, true),
					Arguments.of("closed lower excludes smaller values", closed(3), 2, false),
					Arguments.of("open lower excludes its value", open(3), 3, false),
					Arguments.of("open lower includes larger values", open(3), 4, true)
			);
		}

		private static Stream<Arguments> reportsWhetherTheUpperBoundHarboursTheElementCases()
		{
			return Stream.of(
					Arguments.of("closed upper includes its value", closed(3), 3, true),
					Arguments.of("closed upper includes smaller values", closed(3), 2, true),
					Arguments.of("closed upper excludes larger values", closed(3), 4, false),
					Arguments.of("open upper excludes its value", open(3), 3, false),
					Arguments.of("open upper includes smaller values", open(3), 2, true)
			);
		}
	}

	@Nested
	@DisplayName("when comparing interval boundary semantics")
	final class WhenComparingIntervalBoundarySemantics
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheContainerEnclosesTheLowerBoundCases")
		@DisplayName("reports whether the container encloses the lower bound")
		void reportsWhetherTheContainerEnclosesTheLowerBound(final String as, final Bound<Integer> container,
		                                                     final Bound<Integer> enclosed,
		                                                     final boolean expected)
		{
			assertThat(SUBJECT.enclosesLowerBound(container, enclosed)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheContainerEnclosesTheUpperBoundCases")
		@DisplayName("reports whether the container encloses the upper bound")
		void reportsWhetherTheContainerEnclosesTheUpperBound(final String as, final Bound<Integer> container,
		                                                     final Bound<Integer> enclosed,
		                                                     final boolean expected)
		{
			assertThat(SUBJECT.enclosesUpperBound(container, enclosed)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheEndPrecedesTheStartCases")
		@DisplayName("reports whether the end precedes the start")
		void reportsWhetherTheEndPrecedesTheStart(final String as, final Bound<Integer> end,
		                                          final Bound<Integer> start, final boolean expected)
		{
			assertThat(SUBJECT.endPrecedesStart(end, start)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheBoundsTouchCases")
		@DisplayName("reports whether the bounds touch")
		void reportsWhetherTheBoundsTouch(final String as, final Bound<Integer> end, final Bound<Integer> start,
		                                  final boolean expected)
		{
			assertThat(SUBJECT.touchesAt(end, start)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsWhetherTheBoundsDescribeANonEmptyIntervalCases")
		@DisplayName("reports whether the bounds describe a non-empty interval")
		void reportsWhetherTheBoundsDescribeANonEmptyInterval(final String as, final Bound<Integer> lower,
		                                                      final Bound<Integer> upper, final boolean expected)
		{
			assertThat(SUBJECT.isNonEmpty(lower, upper)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> reportsWhetherTheContainerEnclosesTheLowerBoundCases()
		{
			return Stream.of(
					Arguments.of("smaller lower bound encloses a larger lower bound", closed(1), closed(3), true),
					Arguments.of("larger lower bound does not enclose a smaller lower bound", closed(3), closed(1),
							false),
					Arguments.of("equal closed lower encloses equal closed lower", closed(3), closed(3), true),
					Arguments.of("equal closed lower encloses equal open lower", closed(3), open(3), true),
					Arguments.of("equal open lower encloses equal open lower", open(3), open(3), true),
					Arguments.of("equal open lower does not enclose equal closed lower", open(3), closed(3), false)
			);
		}

		private static Stream<Arguments> reportsWhetherTheContainerEnclosesTheUpperBoundCases()
		{
			return Stream.of(
					Arguments.of("larger upper bound encloses a smaller upper bound", closed(5), closed(3), true),
					Arguments.of("smaller upper bound does not enclose a larger upper bound", closed(3), closed(5),
							false),
					Arguments.of("equal closed upper encloses equal closed upper", closed(3), closed(3), true),
					Arguments.of("equal closed upper encloses equal open upper", closed(3), open(3), true),
					Arguments.of("equal open upper encloses equal open upper", open(3), open(3), true),
					Arguments.of("equal open upper does not enclose equal closed upper", open(3), closed(3), false)
			);
		}

		private static Stream<Arguments> reportsWhetherTheEndPrecedesTheStartCases()
		{
			return Stream.of(
					Arguments.of("strictly smaller end precedes the start", closed(3), closed(5), true),
					Arguments.of("strictly larger end does not precede the start", closed(5), closed(3), false),
					Arguments.of("equal closed and closed bounds do not precede", closed(3), closed(3), false),
					Arguments.of("equal open end precedes equal closed start", open(3), closed(3), true),
					Arguments.of("equal closed end precedes equal open start", closed(3), open(3), true),
					Arguments.of("equal open and open bounds precede", open(3), open(3), true)
			);
		}

		private static Stream<Arguments> reportsWhetherTheBoundsTouchCases()
		{
			return Stream.of(
					Arguments.of("closed then open at same value touch", closed(3), open(3), true),
					Arguments.of("open then closed at same value touch", open(3), closed(3), true),
					Arguments.of("closed then closed at same value do not touch", closed(3), closed(3), false),
					Arguments.of("open then open at same value do not touch", open(3), open(3), false),
					Arguments.of("different values do not touch", closed(3), open(4), false)
			);
		}

		private static Stream<Arguments> reportsWhetherTheBoundsDescribeANonEmptyIntervalCases()
		{
			return Stream.of(
					Arguments.of("smaller lower and larger upper are non-empty", closed(3), closed(5), true),
					Arguments.of("larger lower and smaller upper are empty", closed(5), closed(3), false),
					Arguments.of("equal closed bounds are non-empty", closed(3), closed(3), true),
					Arguments.of("equal open lower and closed upper are empty", open(3), closed(3), false),
					Arguments.of("equal closed lower and open upper are empty", closed(3), open(3), false),
					Arguments.of("equal open bounds are empty", open(3), open(3), false)
			);
		}
	}

	@Nested
	@DisplayName("when selecting tighter and looser bounds")
	final class WhenSelectingTighterAndLooserBounds
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("selectsTheTightestLowerBoundCases")
		@DisplayName("selects the tightest lower bound")
		void selectsTheTightestLowerBound(final String as, final Bound<Integer> first, final Bound<Integer> second,
		                                  final Bound<Integer> expected)
		{
			assertThat(SUBJECT.tightestLowerBound(first, second)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("selectsTheTightestUpperBoundCases")
		@DisplayName("selects the tightest upper bound")
		void selectsTheTightestUpperBound(final String as, final Bound<Integer> first, final Bound<Integer> second,
		                                  final Bound<Integer> expected)
		{
			assertThat(SUBJECT.tightestUpperBound(first, second)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("selectsTheLoosestLowerBoundCases")
		@DisplayName("selects the loosest lower bound")
		void selectsTheLoosestLowerBound(final String as, final Bound<Integer> first, final Bound<Integer> second,
		                                 final Bound<Integer> expected)
		{
			assertThat(SUBJECT.loosestLowerBound(first, second)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("selectsTheLoosestUpperBoundCases")
		@DisplayName("selects the loosest upper bound")
		void selectsTheLoosestUpperBound(final String as, final Bound<Integer> first, final Bound<Integer> second,
		                                 final Bound<Integer> expected)
		{
			assertThat(SUBJECT.loosestUpperBound(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> selectsTheTightestLowerBoundCases()
		{
			return Stream.of(
					Arguments.of("larger lower value is tighter", closed(5), closed(3), closed(5)),
					Arguments.of("smaller lower value is not tighter", closed(3), closed(5), closed(5)),
					Arguments.of("equal lower values prefer open semantics", closed(3), open(3), open(3)),
					Arguments.of("equal open lower values keep the original bound", open(3), open(3), open(3)),
					Arguments.of("equal closed lower values keep the original bound", closed(3), closed(3), closed(3))
			);
		}

		private static Stream<Arguments> selectsTheTightestUpperBoundCases()
		{
			return Stream.of(
					Arguments.of("smaller upper value is tighter", closed(5), closed(3), closed(3)),
					Arguments.of("larger upper value is not tighter", closed(3), closed(5), closed(3)),
					Arguments.of("equal upper values prefer open semantics", closed(3), open(3), open(3)),
					Arguments.of("equal open upper values keep the original bound", open(3), open(3), open(3)),
					Arguments.of("equal closed upper values keep the original bound", closed(3), closed(3), closed(3))
			);
		}

		private static Stream<Arguments> selectsTheLoosestLowerBoundCases()
		{
			return Stream.of(
					Arguments.of("smaller lower value is looser", closed(5), closed(3), closed(3)),
					Arguments.of("larger lower value is not looser", closed(3), closed(5), closed(3)),
					Arguments.of("equal lower values prefer closed semantics", open(3), closed(3), closed(3)),
					Arguments.of("equal open lower values keep the original bound", open(3), open(3), open(3)),
					Arguments.of("equal closed lower values keep the original bound", closed(3), closed(3), closed(3))
			);
		}

		private static Stream<Arguments> selectsTheLoosestUpperBoundCases()
		{
			return Stream.of(
					Arguments.of("larger upper value is looser", closed(3), closed(5), closed(5)),
					Arguments.of("smaller upper value is not looser", closed(5), closed(3), closed(5)),
					Arguments.of("equal upper values prefer closed semantics", open(3), closed(3), closed(3)),
					Arguments.of("equal open upper values keep the original bound", open(3), open(3), open(3)),
					Arguments.of("equal closed upper values keep the original bound", closed(3), closed(3), closed(3))
			);
		}
	}
}