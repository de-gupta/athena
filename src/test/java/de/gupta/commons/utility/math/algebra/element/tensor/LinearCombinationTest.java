package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LinearCombination")
final class LinearCombinationTest
{
	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	private static RationalNumber r(final long num, final long denominator)
	{
		return RationalNumberFactory.of(num, denominator);
	}

	@Nested
	@DisplayName("when constructing")
	final class WhenConstructing
	{
		@Test
		@DisplayName("empty() produces empty combination")
		void emptyProducesEmptyCombination()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.empty();
			assertThat(lc.isEmpty()).isTrue();
			assertThat(lc.size()).isEqualTo(0);
			assertThat(lc.terms()).isEmpty();
		}

		@Test
		@DisplayName("of() produces singleton combination")
		void ofProducesSingletonCombination()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.of(r(1, 2), i(100));
			assertThat(lc.size()).isEqualTo(1);
			assertThat(lc.terms().getFirst().coefficient()).isEqualTo(r(1, 2));
			assertThat(lc.terms().getFirst().element()).isEqualTo(i(100));
		}

		@Test
		@DisplayName("E is unconstrained — any type works as element")
		void eIsUnconstrainedAnyTypeWorksAsElement()
		{
			final LinearCombination<RationalNumber, String> lc = LinearCombinationFactory.of(r(1, 2), "price");
			assertThat(lc.terms().getFirst().coefficient()).isEqualTo(r(1, 2));
			assertThat(lc.terms().getFirst().element()).isEqualTo("price");
		}
	}

	@Nested
	@DisplayName("when adding entries")
	final class WhenAddingEntries
	{
		@Test
		@DisplayName("addEntry appends at end preserving insertion order")
		void addEntryAppendsAtEndPreservingInsertionOrder()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc =
					LinearCombinationFactory.<RationalNumber, IntegralNumber>empty()
					                        .addEntry(r(1, 2), i(100))
					                        .addEntry(r(1, 4), i(200))
					                        .addEntry(r(1, 4), i(300));

			assertThat(lc.size()).isEqualTo(3);
			assertThat(lc.terms().get(0).element()).isEqualTo(i(100));
			assertThat(lc.terms().get(1).element()).isEqualTo(i(200));
			assertThat(lc.terms().get(2).element()).isEqualTo(i(300));
		}

		@Test
		@DisplayName("addEntry with same element merges coefficients (left-linearity)")
		void addEntryWithSameElementMergesCoefficients()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.of(r(1, 2), i(100))
			                                                                                     .addEntry(r(1, 2),
					                                                                                     i(100));
			assertThat(lc.size()).isEqualTo(1);
			assertThat(lc.terms().getFirst().coefficient()).isEqualTo(r(1, 1));
		}

		@Test
		@DisplayName("addEntry is non-destructive — original unchanged")
		void addEntryIsNonDestructive()
		{
			final LinearCombination<RationalNumber, IntegralNumber> original =
					LinearCombinationFactory.of(r(1, 1), i(100));
			original.addEntry(r(1, 2), i(200));
			assertThat(original.size()).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("when removing entries")
	final class WhenRemovingEntries
	{
		@Test
		@DisplayName("removeEntries removes all terms matching the element")
		void removeEntriesRemovesAllMatchingTerms()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc =
					LinearCombinationFactory.<RationalNumber, IntegralNumber>empty()
					                        .addEntry(r(1, 4), i(100))
					                        .addEntry(r(1, 2), i(100))
					                        .addEntry(r(1, 4), i(200));

			final LinearCombination<RationalNumber, IntegralNumber> result = lc.removeEntries(i(100));
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.terms().getFirst().element()).isEqualTo(i(200));
		}

		@Test
		@DisplayName("removeEntries on absent element returns same combination")
		void removeEntriesOnAbsentElementReturnsSameCombination()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.of(r(1, 1), i(100));
			assertThat(lc.removeEntries(i(999))).isEqualTo(lc);
		}

		@Test
		@DisplayName("removeEntries on empty returns empty")
		void removeEntriesOnEmptyReturnsEmpty()
		{
			assertThat(
					LinearCombinationFactory.<RationalNumber, IntegralNumber>empty().removeEntries(i(100))
					                        .isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when combining")
	final class WhenCombining
	{
		@Test
		@DisplayName("combine merges terms in order: left then right")
		void combineMergesTermsInOrder()
		{
			final LinearCombination<RationalNumber, IntegralNumber> left = LinearCombinationFactory.of(r(1, 2), i(100));
			final LinearCombination<RationalNumber, IntegralNumber> right =
					LinearCombinationFactory.of(r(1, 2), i(200));

			final LinearCombination<RationalNumber, IntegralNumber> combined = left.combine(right);
			assertThat(combined.size()).isEqualTo(2);
			assertThat(combined.terms().get(0).element()).isEqualTo(i(100));
			assertThat(combined.terms().get(1).element()).isEqualTo(i(200));
		}

		@Test
		@DisplayName("combine with empty returns original terms")
		void combineWithEmptyReturnsOriginalTerms()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.of(r(3, 4), i(100));
			assertThat(lc.combine(LinearCombinationFactory.empty())).isEqualTo(lc);
			assertThat(LinearCombinationFactory.<RationalNumber, IntegralNumber>empty().combine(lc)).isEqualTo(lc);
		}
	}

	@Nested
	@DisplayName("when scaling coefficients")
	final class WhenScalingCoefficients
	{
		@Test
		@DisplayName("scaleCoefficients transforms all coefficients preserving elements")
		void scaleCoefficientsTransformsAllCoefficients()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc =
					LinearCombinationFactory.<RationalNumber, IntegralNumber>empty()
					                        .addEntry(r(1, 2), i(100))
					                        .addEntry(r(1, 4), i(200));

			final LinearCombination<RationalNumber, IntegralNumber> scaled =
					lc.transformCoefficients(c -> c.multiply(r(1, 2)));

			assertThat(scaled.terms().get(0).coefficient()).isEqualTo(r(1, 4));
			assertThat(scaled.terms().get(0).element()).isEqualTo(i(100));
			assertThat(scaled.terms().get(1).coefficient()).isEqualTo(r(1, 8));
			assertThat(scaled.terms().get(1).element()).isEqualTo(i(200));
		}

		@Test
		@DisplayName("scaleCoefficients on empty returns empty")
		void scaleCoefficientsOnEmptyReturnsEmpty()
		{
			assertThat(LinearCombinationFactory.<RationalNumber, IntegralNumber>empty()
			                                   .transformCoefficients(c -> c.multiply(r(1, 2))).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("EMA accumulation: repeated prices merge coefficients (left-linearity)")
		void emaAccumulationViaScaleCoefficientsAndCombine()
		{
			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			LinearCombination<RationalNumber, IntegralNumber> ema = LinearCombinationFactory.of(r(1, 1), i(100));
			ema = LinearCombinationFactory.of(alpha, i(200))
			                              .combine(ema.transformCoefficients(c -> c.multiply(oneMinusAlpha)));
			ema = LinearCombinationFactory.of(alpha, i(100))
			                              .combine(ema.transformCoefficients(c -> c.multiply(oneMinusAlpha)));

			final LinearCombination<RationalNumber, IntegralNumber> finalEma = ema;
			org.assertj.core.api.SoftAssertions.assertSoftly(softly ->
			{
				softly.assertThat(finalEma.size()).as("size: same price merges").isEqualTo(2);
				softly.assertThat(finalEma.terms().get(0).coefficient()).as("price 100 weight merged")
				      .isEqualTo(r(3, 4));
				softly.assertThat(finalEma.terms().get(0).element()).as("price 100").isEqualTo(i(100));
				softly.assertThat(finalEma.terms().get(1).coefficient()).as("price 200 weight").isEqualTo(r(1, 4));
				softly.assertThat(finalEma.terms().get(1).element()).as("price 200").isEqualTo(i(200));
			});
		}
	}

	@Nested
	@DisplayName("when checking equality")
	final class WhenCheckingEquality
	{
		@Test
		@DisplayName("equal when same terms in same order")
		void equalWhenSameTermsInSameOrder()
		{
			final LinearCombination<RationalNumber, IntegralNumber> a = LinearCombinationFactory.of(r(1, 2), i(100));
			final LinearCombination<RationalNumber, IntegralNumber> b = LinearCombinationFactory.of(r(1, 2), i(100));
			assertThat(a).isEqualTo(b);
		}

		@Test
		@DisplayName("not equal when same terms in different order")
		void notEqualWhenSameTermsInDifferentOrder()
		{
			final LinearCombination<RationalNumber, IntegralNumber> a = LinearCombinationFactory.of(r(1, 2), i(100))
			                                                                                    .addEntry(r(1, 2),
					                                                                                    i(200));
			final LinearCombination<RationalNumber, IntegralNumber> b = LinearCombinationFactory.of(r(1, 2), i(200))
			                                                                                    .addEntry(r(1, 2),
					                                                                                    i(100));
			assertThat(a).isNotEqualTo(b);
		}
	}
}