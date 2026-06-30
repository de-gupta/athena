package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApproximationStrategies")
final class ApproximationStrategiesTest
{
	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	private static RationalNumber r(final long n, final long d)
	{
		return RationalNumberFactory.of(n, d);
	}

	@Nested
	@DisplayName("byEquality")
	final class ByEquality
	{
		private final ApproximationStrategy<IntegralNumber> strategy = ApproximationStrategies.byEquality();

		@Test
		@DisplayName("converges when previous equals current")
		void convergesWhenPreviousEqualsCurrent()
		{
			assertThat(strategy.converged(i(0), i(5), i(5))).isTrue();
		}

		@Test
		@DisplayName("does not converge when previous differs from current")
		void doesNotConvergeWhenPreviousDiffersFromCurrent()
		{
			assertThat(strategy.converged(i(0), i(5), i(4))).isFalse();
		}

		@Test
		@DisplayName("original value does not affect convergence")
		void originalValueDoesNotAffectConvergence()
		{
			assertThat(strategy.converged(i(999), i(7), i(7))).isTrue();
			assertThat(strategy.converged(i(0), i(7), i(7))).isTrue();
		}

		@Test
		@DisplayName("converges regardless of element type")
		void convergesRegardlessOfElementType()
		{
			final ApproximationStrategy<RationalNumber> rationalStrategy = ApproximationStrategies.byEquality();
			assertThat(rationalStrategy.converged(r(0, 1), r(3, 4), r(3, 4))).isTrue();
			assertThat(rationalStrategy.converged(r(0, 1), r(3, 4), r(5, 6))).isFalse();
		}
	}

	@Nested
	@DisplayName("withinTolerance")
	final class WithinTolerance
	{
		private final ApproximationStrategy<RationalNumber> tenth = ApproximationStrategies.withinTolerance(r(1, 10));

		@Test
		@DisplayName("converges when difference equals tolerance exactly")
		void convergesWhenDifferenceEqualsToleranceExactly()
		{
			// |5/10 - 6/10| = 1/10 ≤ 1/10
			assertThat(tenth.converged(r(0, 1), r(5, 10), r(6, 10))).isTrue();
		}

		@Test
		@DisplayName("converges when difference is strictly below tolerance")
		void convergesWhenDifferenceIsStrictlyBelowTolerance()
		{
			// |5/10 - 51/100| = 1/100 < 1/10
			assertThat(tenth.converged(r(0, 1), r(5, 10), r(51, 100))).isTrue();
		}

		@Test
		@DisplayName("does not converge when difference exceeds tolerance")
		void doesNotConvergeWhenDifferenceExceedsTolerance()
		{
			// |5/10 - 8/10| = 3/10 > 1/10
			assertThat(tenth.converged(r(0, 1), r(5, 10), r(8, 10))).isFalse();
		}

		@Test
		@DisplayName("uses absolute difference so order of previous and current does not matter")
		void usesAbsoluteDifferenceRegardlessOfOrder()
		{
			// |6/10 - 5/10| = 1/10 — same as forward direction
			assertThat(tenth.converged(r(0, 1), r(6, 10), r(5, 10))).isTrue();
		}

		@Test
		@DisplayName("original value does not affect convergence")
		void originalValueDoesNotAffectConvergence()
		{
			assertThat(tenth.converged(r(999, 1), r(5, 10), r(6, 10))).isTrue();
			assertThat(tenth.converged(r(0, 1), r(5, 10), r(6, 10))).isTrue();
		}

		@Test
		@DisplayName("zero tolerance only converges on exact equality")
		void zeroToleranceOnlyConvergesOnExactEquality()
		{
			final ApproximationStrategy<RationalNumber> exact = ApproximationStrategies.withinTolerance(r(0, 1));
			assertThat(exact.converged(r(0, 1), r(3, 4), r(3, 4))).isTrue();
			assertThat(exact.converged(r(0, 1), r(3, 4), r(7, 8))).isFalse();
		}
	}
}
