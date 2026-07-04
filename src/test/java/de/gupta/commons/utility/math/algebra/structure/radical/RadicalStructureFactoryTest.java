package de.gupta.commons.utility.math.algebra.structure.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.DivisionConventions;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategies;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RadicalStructureFactory")
final class RadicalStructureFactoryTest
{
	private static final ApproximationStrategy<IntegralNumber> BY_EQUALITY =
			ApproximationStrategies.byEquality();

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	@Nested
	@DisplayName("when using Newton estimator for Ring element")
	final class WhenUsingNewtonEstimatorForRingElement
	{
		private final RadicalStructure<IntegralNumber> radical =
				RadicalStructureFactory.<IntegralNumber>using(Estimator.newton());

		@Test
		@DisplayName("zero returns zero")
		void zeroReturnsZero()
		{
			assertThat(radical.root(i(0), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(0));
		}

		@Test
		@DisplayName("one returns one")
		void oneReturnsOne()
		{
			assertThat(radical.root(i(1), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(1));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("rootCases")
		@DisplayName("computes root correctly")
		void computesRootCorrectly(final String as, final long radicand, final int degree, final long expected)
		{
			assertThat(radical.root(i(radicand), degree, BY_EQUALITY, DivisionConventions.floor()))
					.as(as).isEqualTo(i(expected));
		}

		@Test
		@DisplayName("squareRoot convenience method delegates to root with degree 2")
		void squareRootDelegatesToRootWithDegreeTwo()
		{
			assertThat(radical.squareRoot(i(9), BY_EQUALITY, DivisionConventions.floor()))
					.isEqualTo(i(9).root(2, BY_EQUALITY, DivisionConventions.floor()));
		}

		@Test
		@DisplayName("result matches element-level root method")
		void resultMatchesElementLevelRootMethod()
		{
			assertThat(radical.root(i(25), 2, BY_EQUALITY, DivisionConventions.floor()))
					.isEqualTo(i(25).root(2, BY_EQUALITY, DivisionConventions.floor()));
		}

		@Test
		@DisplayName("throws IllegalArgumentException for degree less than 2")
		void throwsForDegreelessThanTwo()
		{
			assertThatThrownBy(() -> radical.root(i(4), 1, BY_EQUALITY, DivisionConventions.floor()))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("structure is reusable across different elements")
		void structureIsReusableAcrossDifferentElements()
		{
			assertThat(radical.root(i(4), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(2));
			assertThat(radical.root(i(9), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(3));
			assertThat(radical.root(i(8), 3, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(2));
		}

		private static Stream<Arguments> rootCases()
		{
			return Stream.of(
					Arguments.of("√4 = 2", 4, 2, 2),
					Arguments.of("√9 = 3", 9, 2, 3),
					Arguments.of("√25 = 5", 25, 2, 5),
					Arguments.of("∛8 = 2", 8, 3, 2),
					Arguments.of("∛27 = 3", 27, 3, 3)
			);
		}
	}

	@Nested
	@DisplayName("when using Newton estimator with explicit ring structure")
	final class WhenUsingNewtonEstimatorWithExplicitRingStructure
	{
		private final RadicalStructure<IntegralNumber> radical =
				RadicalStructureFactory.using(Estimator.newton(), IntegerEuclideanDomainStructure.INSTANCE);

		@Test
		@DisplayName("zero returns zero using ring structure identity")
		void zeroReturnsZeroUsingRingStructureIdentity()
		{
			assertThat(radical.root(i(0), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(0));
		}

		@Test
		@DisplayName("one returns one using ring structure identity")
		void oneReturnsOneUsingRingStructureIdentity()
		{
			assertThat(radical.root(i(1), 2, BY_EQUALITY, DivisionConventions.floor())).isEqualTo(i(1));
		}

		@Test
		@DisplayName("produces same result as Ring-element variant")
		void producesSameResultAsRingElementVariant()
		{
			final RadicalStructure<IntegralNumber> ringVariant =
					RadicalStructureFactory.<IntegralNumber>using(Estimator.newton());
			assertThat(radical.root(i(16), 2, BY_EQUALITY, DivisionConventions.floor()))
					.isEqualTo(ringVariant.root(i(16), 2, BY_EQUALITY, DivisionConventions.floor()));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("rootCases")
		@DisplayName("computes root correctly via explicit ring structure")
		void computesRootCorrectlyViaExplicitRingStructure(final String as, final long radicand,
		                                                   final int degree, final long expected)
		{
			assertThat(radical.root(i(radicand), degree, BY_EQUALITY, DivisionConventions.floor()))
					.as(as).isEqualTo(i(expected));
		}

		private static Stream<Arguments> rootCases()
		{
			return Stream.of(
					Arguments.of("√4 = 2", 4, 2, 2),
					Arguments.of("√9 = 3", 9, 2, 3),
					Arguments.of("∛8 = 2", 8, 3, 2),
					Arguments.of("∛27 = 3", 27, 3, 3)
			);
		}
	}
}