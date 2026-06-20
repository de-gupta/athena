package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.structure.module.standard.DoubleVectorSpaceStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("DoubleVectorSpaceStructure — vector space laws")
final class DoubleVectorSpaceStructureTest
{
	@TestFactory
	@DisplayName("satisfies all vector space laws")
	Stream<DynamicTest> vectorSpaceLaws()
	{
		return new VectorSpaceStructureLaws<>(DoubleVectorSpaceStructure.INSTANCE, 3.0, 7.0, 2.0, 5.0).tests();
	}
}