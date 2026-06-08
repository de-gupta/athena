package de.gupta.commons.utility.math.algebra.structure.module;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

record ModuleStructureLaws<V, R>(ModuleStructure<V, R> subject, V vector, V secondVector, R scalar, R secondScalar)
{
	Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("r·(v+w) == r·v + r·w", this::scaleDistributesOverVectorAddition),
				dynamicTest("(r+s)·v == r·v + s·v", this::scaleDistributesOverScalarAddition),
				dynamicTest("(r·s)·v == r·(s·v)", this::scaleIsCompatibleWithRingMultiplication),
				dynamicTest("1·v == v", this::unitScalarActsAsIdentity)
		);
	}

	private void scaleDistributesOverVectorAddition()
	{
		V left = subject.scale(scalar, subject.add(vector, secondVector));
		V right = subject.add(subject.scale(scalar, vector), subject.scale(scalar, secondVector));

		assertThat(left)
				.as("r·(v+w) must equal r·v + r·w")
				.isEqualTo(right);
	}

	private void scaleDistributesOverScalarAddition()
	{
		V left = subject.scale(subject.scalars().add(scalar, secondScalar), vector);
		V right = subject.add(subject.scale(scalar, vector), subject.scale(secondScalar, vector));

		assertThat(left)
				.as("(r+s)·v must equal r·v + s·v")
				.isEqualTo(right);
	}

	private void scaleIsCompatibleWithRingMultiplication()
	{
		V left = subject.scale(subject.scalars().multiply(scalar, secondScalar), vector);
		V right = subject.scale(scalar, subject.scale(secondScalar, vector));

		assertThat(left)
				.as("(r·s)·v must equal r·(s·v)")
				.isEqualTo(right);
	}

	private void unitScalarActsAsIdentity()
	{
		assertThat(subject.scale(subject.scalars().one(), vector))
				.as("1·v must equal v")
				.isEqualTo(vector);
	}
}
