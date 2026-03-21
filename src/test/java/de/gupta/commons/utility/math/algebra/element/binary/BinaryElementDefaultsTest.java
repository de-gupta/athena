package de.gupta.commons.utility.math.algebra.element.binary;

import de.gupta.aletheia.collection.folding.Loom;
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
	@DisplayName("group element helpers should support divide and power")
	void groupElementHelpersSupportDivideAndPower()
	{
		IntegerAdditionElement three = new IntegerAdditionElement(3);

		assertThat(three.divide(new IntegerAdditionElement(2))).isEqualTo(new IntegerAdditionElement(1));
		assertThat(three.power(4)).isEqualTo(new IntegerAdditionElement(12));
		assertThat(three.power(-2)).isEqualTo(new IntegerAdditionElement(-6));
		assertThat(three.power(0)).isEqualTo(new IntegerAdditionElement(0));
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

	private record IntegerAdditionElement(int value) implements AbelianGroup<IntegerAdditionElement>
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
