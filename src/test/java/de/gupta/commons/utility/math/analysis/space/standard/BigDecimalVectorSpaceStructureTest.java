package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructureLaws;
import de.gupta.commons.utility.math.algebra.structure.module.standard.BigDecimalVectorSpaceStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.stream.Stream;

@DisplayName("BigDecimalVectorSpaceStructure — vector space laws")
final class BigDecimalVectorSpaceStructureTest
{
	@TestFactory
	@DisplayName("satisfies all vector space laws")
	Stream<DynamicTest> vectorSpaceLaws()
	{
		return new VectorSpaceStructureLaws<>(
				BigDecimalVectorSpaceStructure.INSTANCE,
				new BigDecimal("3"),
				new BigDecimal("7"),
				new BigDecimal("2"),
				new BigDecimal("5")
		).tests();
	}
}