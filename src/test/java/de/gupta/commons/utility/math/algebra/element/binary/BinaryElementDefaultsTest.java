package de.gupta.commons.utility.math.algebra.element.binary;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveMonoid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Binary Element Defaults")
class BinaryElementDefaultsTest
{
	@Test
	@DisplayName("semigroup and monoid element helpers should preserve order and identity")
	void semigroupAndMonoidElementHelpersPreserveOrderAndIdentity()
	{
		Word seed = new Word("a");

		assertThat(seed.combineAll(Loom.harness(List.of(new Word("b"), new Word("c")))))
				.isEqualTo(new Word("abc"));
		assertThat(seed.combineAll(List.of(new Word("b"), new Word("c"))))
				.isEqualTo(new Word("abc"));
		assertThat(seed.identity()).isEqualTo(new Word(""));
	}

	@Test
	@DisplayName("additive semigroup and monoid aliases should delegate cleanly")
	void additiveSemigroupAndMonoidAliasesShouldDelegateCleanly()
	{
		AdditiveNaturalNumber two = new AdditiveNaturalNumber(2);
		AdditiveNaturalNumber one = new AdditiveNaturalNumber(1);

		assertThat(two.add(one)).isEqualTo(new AdditiveNaturalNumber(3));
		assertThat(two.addAll(Loom.harness(List.of(one, new AdditiveNaturalNumber(3))))).isEqualTo(
				new AdditiveNaturalNumber(6));
		assertThat(two.zero()).isEqualTo(new AdditiveNaturalNumber(0));
	}

	@Test
	@DisplayName("group element helpers should support divide and power")
	void groupElementHelpersSupportDivideAndPower()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);

		assertThat(three.divide(new IntegerAdditionElement(2))).isEqualTo(new IntegerAdditionElement(1));
		assertThat(three.power(4)).isEqualTo(new IntegerAdditionElement(12));
		assertThat(three.power(-2)).isEqualTo(new IntegerAdditionElement(-6));
		assertThat(three.power(0)).isEqualTo(new IntegerAdditionElement(0));
	}

	@Test
	@DisplayName("additive group element aliases should delegate to the neutral core")
	void additiveGroupElementAliasesShouldDelegateToTheNeutralCore()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);

		assertThat(three.add(new IntegerAdditionElement(2))).isEqualTo(new IntegerAdditionElement(5));
		assertThat(three.negate()).isEqualTo(new IntegerAdditionElement(-3));
		assertThat(three.subtract(new IntegerAdditionElement(1))).isEqualTo(new IntegerAdditionElement(2));
		assertThat(three.zero()).isEqualTo(new IntegerAdditionElement(0));
	}

	@Test
	@DisplayName("additive abelian group element should remain commutative")
	void additiveAbelianGroupElementShouldRemainCommutative()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);
		IntegerAdditionElement two = new IntegerAdditionElement(2);

		assertThat(three.add(two)).isEqualTo(two.add(three));
	}

	private record Word(String value) implements Monoid<Word>
	{
		@Override
		public Word combine(final Word other)
		{
			return new Word(value + other.value);
		}

		@Override
		public Word identity()
		{
			return new Word("");
		}
	}

	private record AdditiveNaturalNumber(int value) implements AdditiveMonoid<AdditiveNaturalNumber>
	{
		@Override
		public AdditiveNaturalNumber combine(final AdditiveNaturalNumber other)
		{
			return new AdditiveNaturalNumber(value + other.value);
		}

		@Override
		public AdditiveNaturalNumber identity()
		{
			return new AdditiveNaturalNumber(0);
		}
	}

	private record IntegerAdditionElement(int value) implements AdditiveAbelianGroup<IntegerAdditionElement>
	{
		@Override
		public IntegerAdditionElement combine(final IntegerAdditionElement other)
		{
			return new IntegerAdditionElement(value + other.value);
		}

		@Override
		public IntegerAdditionElement identity()
		{
			return new IntegerAdditionElement(0);
		}

		@Override
		public IntegerAdditionElement inverse()
		{
			return new IntegerAdditionElement(-value);
		}
	}
}