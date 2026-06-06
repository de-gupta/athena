package de.gupta.commons.utility.math.algebra.structure.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VectorSpaceStructure laws")
abstract class VectorSpaceStructureLawsTest<V, F> extends ModuleStructureLawsTest<V, F>
{
	@Override
	protected abstract VectorSpaceStructure<V, F> module();

	@Test
	@DisplayName("scalars() provides a FieldStructure")
	void scalarsIsAField()
	{
		assertThat(module().scalars()).isNotNull();
	}

	@Test
	@DisplayName("scaling by multiplicative inverse then original scalar returns vector: r·(r⁻¹·v) == v")
	void scaleByInverseUndoesScale()
	{
		VectorSpaceStructure<V, F> space = module();
		F r = scalar();
		V v = vector();

		V scaled = space.scale(r, v);
		V unscaled = space.scale(space.scalars().multiplicativeInverse(r), scaled);

		assertThat(unscaled).isEqualTo(v);
	}

	@Test
	@DisplayName("scaling by additive inverse negates the vector: (-1)·v == -v")
	void scaleByAdditiveInverseNegatesVector()
	{
		VectorSpaceStructure<V, F> space = module();
		V v = vector();

		V scaledByMinusOne = space.scale(space.scalars().additiveInverse(space.scalars().one()), v);
		V negated = space.negate(v);

		assertThat(scaledByMinusOne).isEqualTo(negated);
	}
}
