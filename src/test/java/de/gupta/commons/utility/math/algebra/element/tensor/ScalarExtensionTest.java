package de.gupta.commons.utility.math.algebra.element.tensor;

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

	@Nested
	@DisplayName("when constructing")
	final class WhenConstructing
	{
		@Test
		@DisplayName("of() produces single-term extension backed by LC")
		void ofProducesSingleTermExtension()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.terms()).hasSize(1);
			assertThat(ext.terms().getFirst().coefficient()).isEqualTo(r(1, 2));
			assertThat(ext.terms().getFirst().element()).isEqualTo(r(3, 4));
		}

		@Test
		@DisplayName("empty() produces zero extension")
		void emptyProducesZeroExtension()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext =
					ScalarExtensionFactory.empty();
			assertThat(ext.isEmpty()).isTrue();
			assertThat(ext.terms()).isEmpty();
		}
	}

	@Nested
	@DisplayName("when scaling (S-module action)")
	final class WhenScaling
	{
		@Test
		@DisplayName("scale multiplies all S-coefficients")
		void scaleMultipliesAllCoefficients()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext =
					se(1, 2, 3, 4).add(se(1, 4, 5, 6));
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> scaled = ext.scale(r(2, 1));
			assertThat(scaled.terms()).hasSize(2);
			assertThat(scaled.terms().get(0).coefficient()).isEqualTo(r(1, 1));
			assertThat(scaled.terms().get(1).coefficient()).isEqualTo(r(1, 2));
		}

		@Test
		@DisplayName("scale by zero gives zero coefficients (normalized to empty)")
		void scaleByZeroGivesEmpty()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> scaled =
					se(1, 2, 3, 4).scale(r(0, 1));
			assertThat(scaled.isEmpty()).isTrue();
		}

		@Test
		@DisplayName("scale by one is identity")
		void scaleByOneIsIdentity()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.scale(r(1, 1))).isEqualTo(ext);
		}
	}

	@Nested
	@DisplayName("when adding (S-module addition)")
	final class WhenAdding
	{
		@Test
		@DisplayName("add combines terms from both extensions")
		void addCombinesTerms()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> a = se(1, 2, 3, 4);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> b = se(1, 4, 7, 8);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> sum = a.add(b);
			assertThat(sum.terms()).hasSize(2);
		}

		@Test
		@DisplayName("add normalizes same-element terms by summing coefficients")
		void addNormalizesSameElementTerms()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> a = se(1, 4, 3, 4);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> b = se(1, 4, 3, 4);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> sum = a.add(b);
			assertThat(sum.terms()).hasSize(1);
			assertThat(sum.terms().getFirst().coefficient()).isEqualTo(r(1, 2));
		}

		@Test
		@DisplayName("adding zero is identity")
		void addingZeroIsIdentity()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.add(ext.zero())).isEqualTo(ext);
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@Test
		@DisplayName("negate flips all S-coefficients")
		void negateFlipsCoefficients()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> negated = ext.negate();
			assertThat(negated.terms().getFirst().coefficient()).isEqualTo(r(-1, 2));
			assertThat(negated.terms().getFirst().element()).isEqualTo(r(3, 4));
		}

		@Test
		@DisplayName("add with negate gives zero")
		void addWithNegateGivesZero()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext = se(1, 2, 3, 4);
			assertThat(ext.add(ext.negate()).isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when projecting")
	final class WhenProjecting
	{
		@Test
		@DisplayName("project applies policy to terms and returns E")
		void projectAppliesPolicyToTerms()
		{
			final ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ext =
					se(1, 2, 3, 1).add(se(1, 2, 5, 1));

			final ProjectionPolicy<RationalNumber, RationalNumber, RationalNumber> policy =
					accumulation -> accumulation.terms().stream()
					                            .map(t -> t.coefficient().multiply(t.element()))
					                            .reduce(RationalNumberFactory.zero(), RationalNumber::add);

			assertThat(ext.project(policy)).isEqualTo(r(4, 1));
		}

		@Test
		@DisplayName("EMA-style accumulation: α·x + (1-α)·prev then project")
		void emaStyleAccumulationThenProject()
		{
			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			ScalarExtension<RationalNumber, RationalNumber, RationalNumber> ema =
					ScalarExtensionFactory.of(r(1, 1), r(100, 1));
			ema = ScalarExtensionFactory.of(alpha, r(200, 1)).add(ema.scale(oneMinusAlpha));
			ema = ScalarExtensionFactory.of(alpha, r(100, 1)).add(ema.scale(oneMinusAlpha));

			final ProjectionPolicy<RationalNumber, RationalNumber, RationalNumber> sumPolicy =
					acc -> acc.terms().stream()
					          .map(t -> t.coefficient().multiply(t.element()))
					          .reduce(RationalNumberFactory.zero(), RationalNumber::add);

			assertThat(ema.project(sumPolicy)).isEqualTo(r(125, 1));
		}
	}
}