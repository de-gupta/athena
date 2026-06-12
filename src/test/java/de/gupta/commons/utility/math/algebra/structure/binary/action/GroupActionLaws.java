package de.gupta.commons.utility.math.algebra.structure.binary.action;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record GroupActionLaws<S, X>(GroupAction<S, X> action, S actor1, S actor2, X point)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new MonoidActionLaws<>(action, actor1, actor2, point).tests(),
				Stream.of(
						dynamicTest("act(g⁻¹, act(g, x)) == x", this::inverseUndoesAction)
				)
		);
	}

	private void inverseUndoesAction()
	{
		X acted = action.act(actor1, point);
		X undone = action.act(action.inverse(actor1), acted);

		assertThat(undone)
				.as("acting then acting by the inverse must return the original point")
				.isEqualTo(point);
	}
}
