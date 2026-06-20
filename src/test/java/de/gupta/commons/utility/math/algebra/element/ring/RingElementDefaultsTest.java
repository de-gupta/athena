package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ring Element Defaults")
class RingElementDefaultsTest
{
	@Test
	@DisplayName("semiring element helpers should sum and multiply in order")
	void semiringElementHelpersShouldSumAndMultiplyInOrder()
	{
		IntRing seed = new IntRing(5);

		assertThat(seed.addAll(List.of(new IntRing(2), new IntRing(3)))).isEqualTo(new IntRing(10));
		assertThat(seed.multiplyAll(Loom.thread(List.of(new IntRing(2), new IntRing(3)))))
				.isEqualTo(new IntRing(30));
		assertThat(seed.multiplyAll(List.of(new IntRing(2), new IntRing(3)))).isEqualTo(new IntRing(30));
		assertThat(seed.subtract(new IntRing(2))).isEqualTo(new IntRing(3));
	}

	@Test
	@DisplayName("field element divide should use reciprocal")
	void fieldElementDivideUsesReciprocal()
	{
		assertThat(new Rational(3, 4).divide(new Rational(5, 6))).isEqualTo(new Rational(9, 10));
	}

	@Test
	@DisplayName("euclidean domain element helpers should expose quotient remainder and gcd")
	void euclideanDomainElementHelpersExposeQuotientRemainderAndGcd()
	{
		IntEuclidean dividend = new IntEuclidean(17);
		IntEuclidean divisor = new IntEuclidean(5);

		assertThat(dividend.quotient(divisor)).isEqualTo(new IntEuclidean(3));
		assertThat(dividend.remainder(divisor)).isEqualTo(new IntEuclidean(2));
		assertThat(new IntEuclidean(84).gcd(new IntEuclidean(30))).isEqualTo(new IntEuclidean(6));
		assertThat(new IntEuclidean(-9).norm()).isEqualTo(9);
	}

	private record IntRing(int value) implements Ring<IntRing>
	{
		@Override
		public IntRing zero()
		{
			return new IntRing(0);
		}

		@Override
		public IntRing one()
		{
			return new IntRing(1);
		}

		@Override
		public IntRing multiply(final IntRing other)
		{
			return new IntRing(value * other.value);
		}

		@Override
		public IntRing add(final IntRing other)
		{
			return new IntRing(value + other.value);
		}

		@Override
		public IntRing negate()
		{
			return new IntRing(-value);
		}
	}

	private record Rational(int numerator, int denominator) implements Field<Rational>
	{
		@Override
		public Rational zero()
		{
			return new Rational(0, 1);
		}

		@Override
		public Rational one()
		{
			return new Rational(1, 1);
		}

		@Override
		public Rational multiply(final Rational other)
		{
			return new Rational(numerator * other.numerator, denominator * other.denominator);
		}

		@Override
		public Rational add(final Rational other)
		{
			return new Rational(numerator * other.denominator + other.numerator * denominator,
					denominator * other.denominator);
		}

		@Override
		public Rational negate()
		{
			return new Rational(-numerator, denominator);
		}

		@Override
		public Rational reciprocal()
		{
			return new Rational(denominator, numerator);
		}

		private static int gcd(final int a, final int b)
		{
			return b == 0 ? a : gcd(b, a % b);
		}

		private Rational
		{
			if (denominator == 0)
			{
				throw new IllegalArgumentException("Denominator cannot be zero.");
			}

			int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
			numerator /= gcd;
			denominator /= gcd;
			if (denominator < 0)
			{
				numerator = -numerator;
				denominator = -denominator;
			}
		}
	}

	private record IntEuclidean(int value) implements EuclideanDomain<IntEuclidean>
	{
		@Override
		public IntEuclidean zero()
		{
			return new IntEuclidean(0);
		}

		@Override
		public IntEuclidean one()
		{
			return new IntEuclidean(1);
		}

		@Override
		public IntEuclidean multiply(final IntEuclidean other)
		{
			return new IntEuclidean(value * other.value);
		}

		@Override
		public IntEuclidean add(final IntEuclidean other)
		{
			return new IntEuclidean(value + other.value);
		}

		@Override
		public IntEuclidean negate()
		{
			return new IntEuclidean(-value);
		}

		@Override
		public boolean isZero()
		{
			return value == 0;
		}

		@Override
		public long norm()
		{
			return Math.abs(value);
		}

		@Override
		public DivisionResult<IntEuclidean> divideWithRemainder(final IntEuclidean divisor)
		{
			return DivisionResult.of(new IntEuclidean(value / divisor.value), new IntEuclidean(value % divisor.value));
		}
	}
}