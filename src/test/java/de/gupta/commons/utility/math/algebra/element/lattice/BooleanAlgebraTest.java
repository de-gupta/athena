package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("BooleanAlgebra")
final class BooleanAlgebraTest
{
	@TestFactory
	@DisplayName("Bit satisfies all Boolean algebra laws")
	Stream<DynamicTest> bitSatisfiesAllBooleanAlgebraLaws()
	{
		return new BooleanAlgebraLaws<>(Bit.TRUE, Bit.FALSE, Bit.TRUE).tests();
	}

	private enum Bit implements BooleanAlgebra<Bit>
	{
		FALSE, TRUE;

		@Override
		public Bit meet(final Bit other)
		{
			return this == TRUE && other == TRUE ? TRUE : FALSE;
		}

		@Override
		public Bit join(final Bit other)
		{
			return this == TRUE || other == TRUE ? TRUE : FALSE;
		}

		@Override
		public Bit complement()
		{
			return this == TRUE ? FALSE : TRUE;
		}

		@Override
		public Bit top()
		{
			return TRUE;
		}

		@Override
		public Bit bottom()
		{
			return FALSE;
		}
	}
}
