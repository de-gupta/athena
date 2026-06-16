package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.DisplayName;

@DisplayName("BooleanLogicStructure")
final class BooleanLogicStructureTest implements BooleanAlgebraStructureLaws<Boolean>
{
	@Override
	public BooleanAlgebraStructure<Boolean> subject()
	{
		return BooleanLogicStructure.INSTANCE;
	}

	@Override
	@Provide
	public Arbitrary<Boolean> elements()
	{
		return Arbitraries.of(true, false);
	}
}