package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.ring.Semiring;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScalarExtension")
final class ScalarExtensionTest
{
	private static ScalarExtension<RationalNumber, RationalNumber, RationalNumber> se(final long cNum,
	                                                                                  final long cDenom,
	                                                                                  final long eNum,
	                                                                                  final long eDenom)
	{
		return ScalarExtensionFactory.of(r(cNum, cDenom), r(eNum, eDenom));
	}

	private static RationalNumber r(final long num, final long denom)
	{
		return RationalNumberFactory.of(num, denom);
	}

	private static RationalNumber evaluate(final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext)
	{
		return ext.fold(RationalNumberFactory.zero(),
				Semiring::multiply,
				RationalNumber::add);
	}

	@Nested
	@DisplayName("when constructing")
	final class WhenConstructing
	{
		@Test
		@DisplayName("of() produces non-empty extension")
		void ofProducesNonEmptyExtension()
		{
			assertThat(se(1, 2, 3, 4).isEmpty()).isFalse();
		}

		@Test
		@DisplayName("empty() produces zero extension")
		void emptyProducesZeroExtension()
		{
			assertThat(ScalarExtensionFactory.empty().isEmpty()).isTrue();
		}

		@Test
		@DisplayName("fold evaluates a single-term extension correctly")
		void foldEvaluatesSingleTerm()
		{
			assertThat(evaluate(se(1, 2, 3, 4))).isEqualTo(r(3, 8));
		}
	}

	@Nested
	@DisplayName("when scaling (S-module action)")
	final class WhenScaling
	{
		@Test
		@DisplayName("scale by one is identity")
		void scaleByOneIsIdentity()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.scale(r(1, 1))).isEqualTo(ext);
		}

		@Test
		@DisplayName("scale multiplies the evaluation result")
		void scaleMultipliesEvaluationResult()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 1);
			assertThat(evaluate(ext.scale(r(2, 1)))).isEqualTo(evaluate(ext).multiply(r(2, 1)));
		}

		@Test
		@DisplayName("scale by zero produces empty extension")
		void scaleByZeroProducesEmpty()
		{
			assertThat(se(1, 2, 3, 4).scale(r(0, 1)).isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when adding (S-module addition)")
	final class WhenAdding
	{
		@Test
		@DisplayName("add produces extension whose evaluation is the sum of evaluations")
		void addProducesCorrectEvaluation()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> a = se(1, 2, 3, 1);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> b = se(1, 4, 5, 1);
			assertThat(evaluate(a.add(b))).isEqualTo(evaluate(a).add(evaluate(b)));
		}

		@Test
		@DisplayName("add with zero is identity")
		void addWithZeroIsIdentity()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.add(ext.zero())).isEqualTo(ext);
		}

		@Test
		@DisplayName("add normalizes same-element terms: evaluation is exact sum")
		void addNormalizesSameElementTerms()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> a = se(1, 4, 7, 1);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> b = se(1, 4, 7, 1);
			assertThat(evaluate(a.add(b))).isEqualTo(r(7, 2));
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@Test
		@DisplayName("evaluation of negated extension is negated evaluation")
		void evaluationOfNegatedIsNegated()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(evaluate(ext.negate())).isEqualTo(evaluate(ext).negate());
		}

		@Test
		@DisplayName("add with negate produces empty extension")
		void addWithNegateIsEmpty()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.add(ext.negate()).isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when projecting via fold")
	final class WhenProjecting
	{
		@Test
		@DisplayName("fold collapses weighted sum correctly")
		void foldCollapsesWeightedSum()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext =
					se(1, 2, 3, 1).add(se(1, 2, 5, 1));
			assertThat(evaluate(ext)).isEqualTo(r(4, 1));
		}

		@Test
		@DisplayName("project delegates to policy via fold")
		void projectDelegatesToPolicy()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext =
					se(1, 2, 3, 1).add(se(1, 4, 7, 1));
			final ProjectionPolicy<RationalNumber, RationalNumber, RationalNumber> policy =
					acc -> acc.fold(RationalNumberFactory.zero(),
							Semiring::multiply,
							RationalNumber::add);
			assertThat(ext.project(policy)).isEqualTo(r(13, 4));
		}

		@Test
		@DisplayName("EMA-style: accumulate exactly, project once at boundary")
		void emaStyleAccumulateThenProjectOnce()
		{
			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ema =
					ScalarExtensionFactory.of(r(1, 1), r(100, 1));
			ema = ScalarExtensionFactory.of(alpha, r(200, 1)).add(ema.scale(oneMinusAlpha));
			ema = ScalarExtensionFactory.of(alpha, r(100, 1)).add(ema.scale(oneMinusAlpha));

			final ProjectionPolicy<RationalNumber, RationalNumber, RationalNumber> policy =
					acc -> acc.fold(RationalNumberFactory.zero(),
							Semiring::multiply,
							RationalNumber::add);

			assertThat(ema.project(policy)).isEqualTo(r(125, 1));
		}
	}
}