package de.gupta.commons.utility.math.ordering.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("IntegerNaturalOrder")
final class IntegerNaturalOrderTest
{
	@TestFactory
	@DisplayName("satisfies all total order laws")
	Stream<DynamicTest> satisfiesAllTotalOrderLaws()
	{
		return new TotalOrderStructureLaws<>(IntegerNaturalOrder.INSTANCE, 1, 3, 7).tests();
	}
}
