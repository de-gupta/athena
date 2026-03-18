package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Additive Structure Notation")
class AdditiveStructureNotationTest
{
	private static final AdditiveSemigroupStructure<Integer> ADDITIVE_SEMIGROUP = Integer::sum;

	private static final AdditiveMonoidStructure<Integer> ADDITIVE_MONOID = new AdditiveMonoidStructure<>()
	{
		@Override
		public Integer identity()
		{
			return 0;
		}

		@Override
		public Integer combine(final Integer left, final Integer right)
		{
			return left + right;
		}
	};

	private static final AdditiveAbelianGroupStructure<Integer> INTEGER_ADDITION = new AdditiveAbelianGroupStructure<>()
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
		public Integer combine(final Integer left, final Integer right)
		{
			return left + right;
		}
	};

	@Test
	@DisplayName("additive semigroup aliases should delegate to combine semantics")
	void additiveSemigroupAliasesShouldDelegateToCombineSemantics()
	{
		assertThat(ADDITIVE_SEMIGROUP.add(2, 5)).isEqualTo(7);
		assertThat(ADDITIVE_SEMIGROUP.addAll(Loom.harness(List.of(1, 2, 3)))).isEqualTo(6);
		assertThat(ADDITIVE_SEMIGROUP.addAll(List.of(1, 2, 3))).isEqualTo(6);
	}

	@Test
	@DisplayName("additive monoid aliases should expose zero and empty-safe bulk addition")
	void additiveMonoidAliasesShouldExposeZeroAndEmptySafeBulkAddition()
	{
		assertThat(ADDITIVE_MONOID.zero()).isEqualTo(0);
		assertThat(ADDITIVE_MONOID.addAll(List.of())).isEqualTo(0);
		assertThat(ADDITIVE_MONOID.addAll(Loom.harness(List.of(1, 2, 3)))).isEqualTo(6);
	}

	@Test
	@DisplayName("additive group aliases should delegate to the neutral core")
	void additiveGroupAliasesShouldDelegateToTheNeutralCore()
	{
		assertThat(INTEGER_ADDITION.add(2, 5)).isEqualTo(7);
		assertThat(INTEGER_ADDITION.negate(4)).isEqualTo(-4);
		assertThat(INTEGER_ADDITION.subtract(9, 6)).isEqualTo(3);
		assertThat(INTEGER_ADDITION.zero()).isEqualTo(0);
	}

	@Test
	@DisplayName("additive abelian group should remain commutative")
	void additiveAbelianGroupShouldRemainCommutative()
	{
		assertThat(INTEGER_ADDITION.add(2, 5)).isEqualTo(INTEGER_ADDITION.add(5, 2));
	}
}