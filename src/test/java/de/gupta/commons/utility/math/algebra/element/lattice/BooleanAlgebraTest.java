package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.DisplayName;

@DisplayName("BooleanAlgebra")
final class BooleanAlgebraTest implements BooleanAlgebraLaws<BooleanAlgebraTest.Bit>
{
	@Override
	@Provide
	public Arbitrary<Bit> elements()
	{
		return Arbitraries.of(Bit.class);
	}

	enum Bit implements BooleanAlgebra<Bit>
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