package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Group;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record GroupActedUponLaws<S extends Group<S>, X extends GroupActedUpon<S, X>>(
		X point, S actor1, S actor2)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new MonoidActedUponLaws<>(point, actor1, actor2).tests(),
				Stream.of(
						dynamicTest("x.act(g).act(g⁻¹) == x", this::inverseUndoesAction)
				)
		);
	}

	private void inverseUndoesAction()
	{
		X acted = point.act(actor1);
		X undone = acted.act(actor1.inverse());

		assertThat(undone)
				.as("acting then acting by the inverse must return the original point")
				.isEqualTo(point);
	}
}
