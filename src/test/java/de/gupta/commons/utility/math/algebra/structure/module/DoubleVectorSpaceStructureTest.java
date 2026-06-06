package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;
import org.junit.jupiter.api.DisplayName;

@DisplayName("VectorSpaceStructure laws — Double over Double")
final class DoubleVectorSpaceStructureTest extends VectorSpaceStructureLawsTest<Double, Double>
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

	@Override
	protected VectorSpaceStructure<Double, Double> module()
	{
		return DOUBLE_SPACE;
	}

	@Override
	protected Double vector()
	{
		return 3.0;
	}

	@Override
	protected Double secondVector()
	{
		return 7.0;
	}

	@Override
	protected Double scalar()
	{
		return 2.0;
	}

	@Override
	protected Double secondScalar()
	{
		return 5.0;
	}
}
