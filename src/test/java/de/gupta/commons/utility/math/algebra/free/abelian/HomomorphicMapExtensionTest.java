package de.gupta.commons.utility.math.algebra.free.abelian;

import de.gupta.commons.utility.math.algebra.structure.binary.AbelianGroupStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HomomorphicMapExtension")
class HomomorphicMapExtensionTest
{
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
		public Integer combine(final Integer left, final Integer right)
		{
			return left + right;
		}
	};

	@Test
	@DisplayName("should derive preimages from direct matches and generator slices")
	void shouldDerivePreimagesFromDirectMatchesAndGeneratorSlices()
	{
		final FreeAbelianGroup<Generator> codomain = FreeAbelianGroupFactory.create(Generator.class);
		final FreeAbelianElement<Generator> target = codomain.add(
				codomain.generator(Generator.ALPHA, 6),
				codomain.generator(Generator.BETA));

		final Map<Integer, FreeAbelianElement<Generator>> partialMapping = Map.of(
				2, codomain.generator(Generator.ALPHA, 2),
				3, codomain.generator(Generator.ALPHA, 3),
				5, codomain.generator(Generator.BETA),
				11, target);

		assertThat(HomomorphicMapExtension.preimagesFromPartialMapping(
				INTEGER_ADDITION,
				codomain,
				partialMapping,
				target)).containsExactly(11);
	}

	@Test
	@DisplayName("should reject the zero element when no constituents are present")
	void shouldRejectTheZeroElementWhenNoConstituentsArePresent()
	{
		final FreeAbelianGroup<Generator> codomain = FreeAbelianGroupFactory.create(Generator.class);

		assertThatThrownBy(() -> HomomorphicMapExtension.preimagesFromPartialMapping(
				INTEGER_ADDITION,
				codomain,
				Map.of(1, codomain.generator(Generator.ALPHA)),
				codomain.zero()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No constituents found in the given element.");
	}

	private enum Generator
	{
		ALPHA, BETA
	}
}