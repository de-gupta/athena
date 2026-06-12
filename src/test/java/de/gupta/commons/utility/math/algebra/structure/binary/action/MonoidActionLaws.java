package de.gupta.commons.utility.math.algebra.structure.binary.action;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record MonoidActionLaws<S, X>(MonoidAction<S, X> action, S actor1, S actor2, X point)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new SemigroupActionLaws<>(action, actor1, actor2, point).tests(),
				Stream.of(
						dynamicTest("act(e, x) == x", this::identityActsTrivially)
				)
		);
	}

	private void identityActsTrivially()
	{
		assertThat(action.act(action.identity(), point))
				.as("identity must act trivially on any point")
				.isEqualTo(point);
	}
}
