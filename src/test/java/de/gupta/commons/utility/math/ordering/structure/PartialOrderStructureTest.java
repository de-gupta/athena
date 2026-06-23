package de.gupta.commons.utility.math.ordering.structure;

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

@DisplayName("PartialOrderStructure")
final class PartialOrderStructureTest
{
	private static final Sample ALPHA_ONE = new Sample("alpha", 1);
	private static final Sample ALPHA_TWO = new Sample("alpha", 2);
	private static final Sample ALPHA_THREE = new Sample("alpha", 3);
	private static final Sample BETA_ONE = new Sample("beta", 1);
	private static final PartialOrderStructure<Sample> SUBJECT = (left, right) ->
	{
		if (!left.family().equals(right.family())) return Incomparable.INSTANCE;
		return OrderRelation.from(Integer.compare(left.rank(), right.rank()));
	};

	private record Sample(String family, int rank)
	{
	}

	@Nested
	@DisplayName("when deriving structure predicates from compare")
	final class WhenDerivingStructurePredicatesFromCompare
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsDerivedPredicatesCases")
		@DisplayName("reports the derived predicates consistently")
		void reportsTheDerivedPredicatesConsistently(final String as, final PredicateCase tc)
		{
			assertThat(SUBJECT.compare(tc.left(), tc.right())).as("%s: compare", as).isEqualTo(tc.expectedOrdering());
			assertThat(SUBJECT.leq(tc.left(), tc.right())).as("%s: leq", as).isEqualTo(tc.lessThanOrEqual());
			assertThat(SUBJECT.lt(tc.left(), tc.right())).as("%s: lt", as).isEqualTo(tc.lessThan());
			assertThat(SUBJECT.geq(tc.left(), tc.right())).as("%s: geq", as).isEqualTo(tc.greaterThanOrEqual());
			assertThat(SUBJECT.gt(tc.left(), tc.right())).as("%s: gt", as).isEqualTo(tc.greaterThan());
			assertThat(SUBJECT.isComparable(tc.left(), tc.right())).as("%s: isComparable", as)
			                                                       .isEqualTo(tc.comparable());
			assertThat(SUBJECT.isIncomparable(tc.left(), tc.right())).as("%s: isIncomparable", as)
			                                                         .isEqualTo(!tc.comparable());
		}

		private static Stream<Arguments> reportsDerivedPredicatesCases()
		{
			return Stream.of(
					PredicateCase.of("equal elements", ALPHA_TWO, ALPHA_TWO, OrderRelation.EQUAL, true, false, true,
							false, true),
					PredicateCase.of("strictly smaller comparable element", ALPHA_ONE, ALPHA_THREE,
							OrderRelation.LESS_THAN, true, true, false, false, true),
					PredicateCase.of("strictly greater comparable element", ALPHA_THREE, ALPHA_ONE,
							OrderRelation.GREATER_THAN, false, false, true, true, true),
					PredicateCase.of("incomparable elements", ALPHA_ONE, BETA_ONE, Incomparable.INSTANCE, false, false,
							false, false, false)
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record PredicateCase(String as, Sample left, Sample right, Ordering expectedOrdering,
		                             boolean lessThanOrEqual, boolean lessThan, boolean greaterThanOrEqual,
		                             boolean greaterThan, boolean comparable)
		{
			private static PredicateCase of(String as, Sample left, Sample right, Ordering expectedOrdering,
			                                boolean lessThanOrEqual, boolean lessThan,
			                                boolean greaterThanOrEqual, boolean greaterThan, boolean comparable)
			{
				return new PredicateCase(as, left, right, expectedOrdering, lessThanOrEqual, lessThan,
						greaterThanOrEqual, greaterThan, comparable);
			}
		}
	}
}
