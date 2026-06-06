package de.gupta.commons.utility.math.algebra.structure.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModuleStructure laws")
abstract class ModuleStructureLawsTest<V, R>
{
	protected abstract ModuleStructure<V, R> module();

	protected abstract V vector();

	protected abstract V secondVector();

	protected abstract R scalar();

	protected abstract R secondScalar();

	@Test
	@DisplayName("scale distributes over vector addition: r·(v+w) == r·v + r·w")
	void scaleDistributesOverVectorAddition()
	{
		ModuleStructure<V, R> module = module();
		R r = scalar();
		V v = vector();
		V w = secondVector();

		V left = module.scale(r, module.add(v, w));
		V right = module.add(module.scale(r, v), module.scale(r, w));

		assertThat(left).isEqualTo(right);
	}

	@Test
	@DisplayName("scale distributes over scalar addition: (r+s)·v == r·v + s·v")
	void scaleDistributesOverScalarAddition()
	{
		ModuleStructure<V, R> module = module();
		R r = scalar();
		R s = secondScalar();
		V v = vector();

		V left = module.scale(module.scalars().add(r, s), v);
		V right = module.add(module.scale(r, v), module.scale(s, v));

		assertThat(left).isEqualTo(right);
	}

	@Test
	@DisplayName("scale is compatible with ring multiplication: (r·s)·v == r·(s·v)")
	void scaleIsCompatibleWithRingMultiplication()
	{
		ModuleStructure<V, R> module = module();
		R r = scalar();
		R s = secondScalar();
		V v = vector();

		V left = module.scale(module.scalars().multiply(r, s), v);
		V right = module.scale(r, module.scale(s, v));

		assertThat(left).isEqualTo(right);
	}

	@Test
	@DisplayName("scaling by the ring identity leaves the vector unchanged: 1·v == v")
	void unitScalarActsAsIdentity()
	{
		ModuleStructure<V, R> module = module();
		V v = vector();

		V result = module.scale(module.scalars().one(), v);

		assertThat(result).isEqualTo(v);
	}
}
