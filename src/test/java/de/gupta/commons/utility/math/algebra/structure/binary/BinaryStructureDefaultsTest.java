package de.gupta.commons.utility.math.algebra.structure.binary;

import de.gupta.aletheia.collection.folding.Loom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Binary Structure Defaults")
final class BinaryStructureDefaultsTest
{
	private static final SemigroupStructure<String> CONCATENATION = String::concat;

	private static final MonoidStructure<String> CONCATENATION_MONOID = new MonoidStructure<>()
	{
		@Override
		public String identity()
		{
			return "";
		}

		@Override
		public String multiply(final String left, final String right)
		{
			return left + right;
		}
	};

	private static final AbelianGroupStructure<Integer> INTEGER_ADDITION = new AbelianGroupStructure<>()
	{
		@Override
		public Integer inverse(final Integer element)
		{
			return -element;
		}

		@Override
		public Integer identity()
		{
			return 0;
		}

		@Override
		public Integer multiply(final Integer left, final Integer right)
		{
			return left + right;
		}
	};

	@Test
	@DisplayName("semigroup multiplyAll should preserve order for loom inputs")
	void semigroupMultiplyAllPreservesOrderForLoomInputs()
	{
		String result = CONCATENATION.multiplyAll(Loom.thread(List.of("a", "b", "c")));

		assertThat(result).isEqualTo("abc");
	}

	@Test
	@DisplayName("semigroup multiplyAll should reject empty inputs")
	void semigroupMultiplyAllRejectsEmptyInputs()
	{
		assertThatThrownBy(() -> CONCATENATION.multiplyAll(List.of()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("A semigroup requires at least one element to multiply.");
	}

	@Test
	@DisplayName("monoid multiplyAll should return identity for empty input")
	void monoidMultiplyAllReturnsIdentityForEmptyInput()
	{
		String result = CONCATENATION_MONOID.multiplyAll(List.of());

		assertThat(result).isEqualTo("");
	}

	@Test
	@DisplayName("group divide and power should use inverse and identity correctly")
	void groupDivideAndPowerUseInverseAndIdentityCorrectly()
	{
		assertThat(INTEGER_ADDITION.divide(7, 4)).isEqualTo(3);
		assertThat(INTEGER_ADDITION.power(3, 4)).isEqualTo(12);
		assertThat(INTEGER_ADDITION.power(3, -2)).isEqualTo(-6);
		assertThat(INTEGER_ADDITION.power(9, 0)).isEqualTo(0);
	}

	@Test
	@DisplayName("abelian group multiplyAll should support iterable overload")
	void abelianGroupMultiplyAllSupportsIterableOverload()
	{
		Integer result = INTEGER_ADDITION.multiplyAll(List.of(1, 2, 3, 4));

		assertThat(result).isEqualTo(10);
	}
}