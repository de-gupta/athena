package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.DifferentiableSpaceLaws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("DoubleInterpolatableSpace")
final class DoubleInterpolatableSpaceTest
{
	@TestFactory
	@DisplayName("LINEAR — satisfies all differentiable space laws")
	Stream<DynamicTest> linearLaws()
	{
		return new DifferentiableSpaceLaws<>(DoubleInterpolatableSpace.LINEAR, 1.0, 5.0, 9.0).tests();
	}

	@TestFactory
	@DisplayName("LOG — satisfies all differentiable space laws")
	Stream<DynamicTest> logLaws()
	{
		return new DifferentiableSpaceLaws<>(DoubleInterpolatableSpace.LOG, 1.0, 3.0, 9.0).tests();
	}
}
