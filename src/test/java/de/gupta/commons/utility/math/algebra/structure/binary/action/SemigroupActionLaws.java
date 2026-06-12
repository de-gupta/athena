package de.gupta.commons.utility.math.algebra.structure.binary.action;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record SemigroupActionLaws<S, X>(SemigroupAction<S, X> action, S actor1, S actor2, X point)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("act(s*t, x) == act(s, act(t, x))", this::actionIsCompatibleWithMultiplication)
		);
	}

	private void actionIsCompatibleWithMultiplication()
	{
		X left = action.act(action.multiply(actor1, actor2), point);
		X right = action.act(actor1, action.act(actor2, point));

		assertThat(left)
				.as("act(s*t, x) must equal act(s, act(t, x))")
				.isEqualTo(right);
	}
}
