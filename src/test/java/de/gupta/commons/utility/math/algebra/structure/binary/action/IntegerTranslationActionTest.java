package de.gupta.commons.utility.math.algebra.structure.binary.action;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("IntegerTranslationAction — group action laws")
final class IntegerTranslationActionTest
{
	@TestFactory
	@DisplayName("satisfies all group action laws")
	Stream<DynamicTest> groupActionLaws()
	{
		return new GroupActionLaws<>(IntegerTranslationAction.INSTANCE, 3, 5, 10).tests();
	}
}
