package de.gupta.commons.utility.math.analysis.space.standard;

import de.gupta.commons.utility.math.analysis.space.InterpolatableSpaceLaws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.util.stream.Stream;

@DisplayName("LocalDateInterpolatableSpace")
final class LocalDateInterpolatableSpaceTest
{
	private static final LocalDate START = LocalDate.of(2024, 1, 1);
	private static final LocalDate MIDDLE = LocalDate.of(2024, 7, 1);
	private static final LocalDate END = LocalDate.of(2024, 12, 31);

	@TestFactory
	@DisplayName("satisfies all interpolatable space laws")
	Stream<DynamicTest> laws()
	{
		return new InterpolatableSpaceLaws<>(LocalDateInterpolatableSpace.INSTANCE, START, MIDDLE, END).tests();
	}
}
