package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("VectorSpaceStructure — Double over Double")
final class DoubleVectorSpaceStructureTest
{
	private static final FieldStructure<Double> DOUBLE_FIELD = new FieldStructure<>()
	{
		@Override
		public Double zero()
		{
			return 0.0;
		}

		@Override
		public Double one()
		{
			return 1.0;
		}

		@Override
		public Double add(final Double left, final Double right)
		{
			return left + right;
		}

		@Override
		public Double multiply(final Double left, final Double right)
		{
			return left * right;
		}

		@Override
		public Double additiveInverse(final Double element)
		{
			return -element;
		}

		@Override
		public Double multiplicativeInverse(final Double element)
		{
			return 1.0 / element;
		}
	};

	private static final VectorSpaceStructure<Double, Double> DOUBLE_SPACE = new VectorSpaceStructure<>()
	{
		@Override
		public FieldStructure<Double> scalars()
		{
			return DOUBLE_FIELD;
		}

		@Override
		public Double scale(final Double scalar, final Double vector)
		{
			return scalar * vector;
		}

		@Override
		public Double add(final Double left, final Double right)
		{
			return left + right;
		}

		@Override
		public Double negate(final Double element)
		{
			return -element;
		}

		@Override
		public Double zero()
		{
			return 0.0;
		}
	};

	@TestFactory
	@DisplayName("satisfies all vector space laws")
	Stream<DynamicTest> vectorSpaceLaws()
	{
		return new VectorSpaceStructureLaws<>(DOUBLE_SPACE, 3.0, 7.0, 2.0, 5.0).tests();
	}
}
