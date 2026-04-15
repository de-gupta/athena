package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Additive Element Notation")
class AdditiveElementNotationTest
{
	@Test
	@DisplayName("additive semigroup and monoid aliases should delegate cleanly")
	void additiveSemigroupAndMonoidAliasesShouldDelegateCleanly()
	{
		AdditiveNaturalNumber two = new AdditiveNaturalNumber(2);
		AdditiveNaturalNumber one = new AdditiveNaturalNumber(1);

		assertThat(two.add(one)).isEqualTo(new AdditiveNaturalNumber(3));
		assertThat(two.addAll(Loom.thread(List.of(one, new AdditiveNaturalNumber(3))))).isEqualTo(
				new AdditiveNaturalNumber(4));
		assertThat(two.zero()).isEqualTo(new AdditiveNaturalNumber(0));
	}

	@Test
	@DisplayName("additive group aliases should delegate to the neutral core")
	void additiveGroupAliasesShouldDelegateToTheNeutralCore()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);

		assertThat(three.add(new IntegerAdditionElement(2))).isEqualTo(new IntegerAdditionElement(5));
		assertThat(three.negate()).isEqualTo(new IntegerAdditionElement(-3));
		assertThat(three.subtract(new IntegerAdditionElement(1))).isEqualTo(new IntegerAdditionElement(2));
		assertThat(three.zero()).isEqualTo(new IntegerAdditionElement(0));
	}

	@Test
	@DisplayName("additive abelian group should remain commutative")
	void additiveAbelianGroupShouldRemainCommutative()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);
		IntegerAdditionElement two = new IntegerAdditionElement(2);

		assertThat(three.add(two)).isEqualTo(two.add(three));
	}

	private record AdditiveNaturalNumber(int value) implements AdditiveMonoid<AdditiveNaturalNumber>
	{
		@Override
		public AdditiveNaturalNumber add(final AdditiveNaturalNumber other)
		{
			return new AdditiveNaturalNumber(value + other.value);
		}

		@Override
		public AdditiveNaturalNumber zero()
		{
			return new AdditiveNaturalNumber(0);
		}
	}

	private record IntegerAdditionElement(int value) implements AdditiveAbelianGroup<IntegerAdditionElement>
	{
		@Override
		public IntegerAdditionElement add(final IntegerAdditionElement other)
		{
			return new IntegerAdditionElement(value + other.value);
		}

		@Override
		public IntegerAdditionElement zero()
		{
			return new IntegerAdditionElement(0);
		}

		@Override
		public IntegerAdditionElement negate()
		{
			return new IntegerAdditionElement(-value);
		}
	}
}