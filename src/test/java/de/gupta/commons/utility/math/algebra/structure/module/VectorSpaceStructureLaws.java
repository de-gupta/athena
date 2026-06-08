package de.gupta.commons.utility.math.algebra.structure.module;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

record VectorSpaceStructureLaws<V, F>(VectorSpaceStructure<V, F> subject, V vector, V secondVector, F scalar,
                                      F secondScalar)
{
	Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new ModuleStructureLaws<>(subject, vector, secondVector, scalar, secondScalar).tests(),
				Stream.of(
						dynamicTest("r·(r⁻¹·v) == v", this::scaleByInverseUndoesScale),
						dynamicTest("(-1)·v == -v", this::scaleByAdditiveInverseOfOneNegatesVector)
				)
		);
	}

	private void scaleByInverseUndoesScale()
	{
		V scaled = subject.scale(scalar, vector);
		V unscaled = subject.scale(subject.scalars().multiplicativeInverse(scalar), scaled);

		assertThat(unscaled)
				.as("scaling then unscaling must return the original vector")
				.isEqualTo(vector);
	}

	private void scaleByAdditiveInverseOfOneNegatesVector()
	{
		F negativeOne = subject.scalars().additiveInverse(subject.scalars().one());
		V scaledByNegativeOne = subject.scale(negativeOne, vector);
		V negated = subject.negate(vector);

		assertThat(scaledByNegativeOne)
				.as("(-1)·v must equal -v")
				.isEqualTo(negated);
	}
}
