package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Incomparable;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.Ordering;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PartiallyOrdered")
final class PartiallyOrderedTest
{
	private static final Sample ALPHA_ONE = new Sample("alpha", 1);
	private static final Sample ALPHA_TWO = new Sample("alpha", 2);
	private static final Sample ALPHA_THREE = new Sample("alpha", 3);
	private static final Sample BETA_ONE = new Sample("beta", 1);

	private record Sample(String family, int rank) implements PartiallyOrdered<Sample>
	{
		@Override
		public Ordering compare(final Sample other)
		{
			if (!family.equals(other.family)) return Incomparable.INSTANCE;
			return OrderRelation.from(Integer.compare(rank, other.rank));
		}
	}

	@Nested
	@DisplayName("when deriving order predicates from compare")
	final class WhenDerivingOrderPredicatesFromCompare
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsDerivedPredicatesCases")
		@DisplayName("reports the derived predicates consistently")
		void reportsTheDerivedPredicatesConsistently(final String as, final PredicateCase tc)
		{
			assertThat(tc.left().isEqualTo(tc.right())).as("%s: isEqualTo", as).isEqualTo(tc.equal());
			assertThat(tc.left().leq(tc.right())).as("%s: leq", as).isEqualTo(tc.lessThanOrEqual());
			assertThat(tc.left().isLessThanOrEqualTo(tc.right())).as("%s: isLessThanOrEqualTo", as)
			                                                     .isEqualTo(tc.lessThanOrEqual());
			assertThat(tc.left().lt(tc.right())).as("%s: lt", as).isEqualTo(tc.lessThan());
			assertThat(tc.left().isLessThan(tc.right())).as("%s: isLessThan", as).isEqualTo(tc.lessThan());
			assertThat(tc.left().geq(tc.right())).as("%s: geq", as).isEqualTo(tc.greaterThanOrEqual());
			assertThat(tc.left().isGreaterThanOrEqualTo(tc.right())).as("%s: isGreaterThanOrEqualTo", as)
			                                                        .isEqualTo(tc.greaterThanOrEqual());
			assertThat(tc.left().gt(tc.right())).as("%s: gt", as).isEqualTo(tc.greaterThan());
			assertThat(tc.left().isGreaterThan(tc.right())).as("%s: isGreaterThan", as)
			                                               .isEqualTo(tc.greaterThan());
			assertThat(tc.left().isComparableTo(tc.right())).as("%s: isComparableTo", as).isEqualTo(tc.comparable());
			assertThat(tc.left().isIncomparableTo(tc.right())).as("%s: isIncomparableTo", as)
			                                                  .isEqualTo(!tc.comparable());
		}

		private static Stream<Arguments> reportsDerivedPredicatesCases()
		{
			return Stream.of(
					PredicateCase.of("equal elements", ALPHA_TWO, ALPHA_TWO, true, true, false, true, false, true),
					PredicateCase.of("strictly smaller comparable element", ALPHA_ONE, ALPHA_THREE, false, true, true,
							false, false, true),
					PredicateCase.of("strictly greater comparable element", ALPHA_THREE, ALPHA_ONE, false, false,
							false, true, true, true),
					PredicateCase.of("incomparable elements", ALPHA_ONE, BETA_ONE, false, false, false, false, false,
							false)
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record PredicateCase(String as, Sample left, Sample right, boolean equal, boolean lessThanOrEqual,
		                             boolean lessThan, boolean greaterThanOrEqual, boolean greaterThan,
		                             boolean comparable)
		{
			private static PredicateCase of(String as, Sample left, Sample right, boolean equal,
			                                boolean lessThanOrEqual, boolean lessThan,
			                                boolean greaterThanOrEqual, boolean greaterThan, boolean comparable)
			{
				return new PredicateCase(as, left, right, equal, lessThanOrEqual, lessThan, greaterThanOrEqual,
						greaterThan, comparable);
			}
		}
	}
}