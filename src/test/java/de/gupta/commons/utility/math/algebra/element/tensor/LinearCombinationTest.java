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
		@DisplayName("works with any unconstrained types")
		void worksWithAnyUnconstrainedTypes()
		{
			final LinearCombination<String, Integer> lc = LinearCombinationFactory.of("weight", 42);
			assertThat(lc.terms().getFirst().coefficient()).isEqualTo("weight");
			assertThat(lc.terms().getFirst().element()).isEqualTo(42);
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
		@DisplayName("addEntry allows duplicate elements with different coefficients")
		void addEntryAllowsDuplicateElements()
		{
			final LinearCombination<RationalNumber, IntegralNumber> lc = LinearCombinationFactory.of(r(1, 2), i(100))
			                                                                                     .addEntry(r(1, 2),
					                                                                                     i(100));
			assertThat(lc.size()).isEqualTo(2);
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
					lc.scaleCoefficients(c -> c.multiply(r(1, 2)));

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
			                                   .scaleCoefficients(c -> c.multiply(r(1, 2))).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("EMA accumulation: α·embed(p) + (1-α)·prev via scaleCoefficients and combine")
		void emaAccumulationViaScaleCoefficientsAndCombine()
		{
			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			LinearCombination<RationalNumber, IntegralNumber> ema = LinearCombinationFactory.of(r(1, 1), i(100));
			ema = LinearCombinationFactory.of(alpha, i(200))
			                              .combine(ema.scaleCoefficients(c -> c.multiply(oneMinusAlpha)));
			ema = LinearCombinationFactory.of(alpha, i(100))
			                              .combine(ema.scaleCoefficients(c -> c.multiply(oneMinusAlpha)));

			assertThat(ema.size()).isEqualTo(3);
			assertThat(ema.terms().get(0)).isEqualTo(new LinearCombinationImpl.Entry<>(r(1, 2), i(100)));
			assertThat(ema.terms().get(1)).isEqualTo(new LinearCombinationImpl.Entry<>(r(1, 4), i(200)));
			assertThat(ema.terms().get(2)).isEqualTo(new LinearCombinationImpl.Entry<>(r(1, 4), i(100)));
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