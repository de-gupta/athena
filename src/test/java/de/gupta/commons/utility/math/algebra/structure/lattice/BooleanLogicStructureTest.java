package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("BooleanLogicStructure")
final class BooleanLogicStructureTest
{
	@TestFactory
	@DisplayName("satisfies all Boolean algebra laws")
	Stream<DynamicTest> satisfiesAllBooleanAlgebraLaws()
	{
		return new BooleanAlgebraStructureLaws<>(BooleanLogicStructure.INSTANCE, true, false, true).tests();
	}
}
