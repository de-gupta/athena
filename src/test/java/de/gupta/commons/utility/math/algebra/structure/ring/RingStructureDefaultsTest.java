package de.gupta.commons.utility.math.algebra.structure.ring;

import de.gupta.aletheia.collection.folding.Loom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ring Structure Defaults")
class RingStructureDefaultsTest
{
	private static final RingStructure<Integer> INTEGER_RING = new RingStructure<>()
	{
		@Override
		public Integer additiveInverse(final Integer element)
		{
			return -element;
		}

		@Override
		public Integer zero()
		{
			return 0;
		}

		@Override
		public Integer one()
		{
			return 1;
		}

		@Override
		public Integer add(final Integer left, final Integer right)
		{
			return left + right;
		}

		@Override
		public Integer multiply(final Integer left, final Integer right)
		{
			return left * right;
		}
	};

	private static final FieldStructure<Double> RATIONAL_LIKE_FIELD = new FieldStructure<>()
	{
		@Override
		public Double multiplicativeInverse(final Double element)
		{
			return 1.0 / element;
		}

		@Override
		public Double additiveInverse(final Double element)
		{
			return -element;
		}

		@Override
		public Double zero()
		{
			return 0.0;
		}

		@Override
		public Double one()
		{
			return 1.0;
		}

		@Override
		public Double add(final Double left, final Double right)
		{
			return left + right;
		}

		@Override
		public Double multiply(final Double left, final Double right)
		{
			return left * right;
		}
	};

	private static final EuclideanDomainStructure<Integer> INTEGER_EUCLIDEAN_DOMAIN = new EuclideanDomainStructure<>()
	{
		@Override
		public DivisionResult<Integer> divideWithRemainder(final Integer dividend, final Integer divisor)
		{
			return DivisionResult.of(dividend / divisor, dividend % divisor);
		}

		@Override
		public Integer norm(final Integer element)
		{
			return Math.abs(element);
		}

		@Override
		public boolean isZero(final Integer element)
		{
			return element == 0;
		}

		@Override
		public Integer additiveInverse(final Integer element)
		{
			return -element;
		}

		@Override
		public Integer zero()
		{
			return 0;
		}

		@Override
		public Integer one()
		{
			return 1;
		}

		@Override
		public Integer add(final Integer left, final Integer right)
		{
			return left + right;
		}

		@Override
		public Integer multiply(final Integer left, final Integer right)
		{
			return left * right;
		}
	};

	@Test
	@DisplayName("semiring addAll and multiplyAll should fold correctly")
	void semiringAddAllAndMultiplyAllFoldCorrectly()
	{
		assertThat(INTEGER_RING.addAll(List.of(1, 2, 3, 4))).isEqualTo(10);
		assertThat(INTEGER_RING.multiplyAll(Loom.thread(List.of(2, 3, 4)))).isEqualTo(24);
		assertThat(INTEGER_RING.multiplyAll(List.of(2, 3, 4))).isEqualTo(24);
	}

	@Test
	@DisplayName("ring subtract should use additive inverse")
	void ringSubtractUsesAdditiveInverse()
	{
		assertThat(INTEGER_RING.subtract(10, 7)).isEqualTo(3);
	}

	@Test
	@DisplayName("field divide should use multiplicative inverse")
	void fieldDivideUsesMultiplicativeInverse()
	{
		assertThat(RATIONAL_LIKE_FIELD.divide(7.5, 2.5)).isEqualTo(3.0);
	}

	@Test
	@DisplayName("euclidean domain helpers should expose quotient remainder and gcd")
	void euclideanDomainHelpersExposeQuotientRemainderAndGcd()
	{
		assertThat(INTEGER_EUCLIDEAN_DOMAIN.quotient(17, 5)).isEqualTo(3);
		assertThat(INTEGER_EUCLIDEAN_DOMAIN.remainder(17, 5)).isEqualTo(2);
		assertThat(INTEGER_EUCLIDEAN_DOMAIN.gcd(84, 30)).isEqualTo(6);
		assertThat(INTEGER_EUCLIDEAN_DOMAIN.norm(-9)).isEqualTo(9);
	}
}