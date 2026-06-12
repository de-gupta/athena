package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Monoid;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record MonoidActedUponLaws<S extends Monoid<S>, X extends MonoidActedUpon<S, X>>(
		X point, S actor1, S actor2)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new SemigroupActedUponLaws<>(point, actor1, actor2).tests(),
				Stream.of(
						dynamicTest("x.act(e) == x", this::identityActsTrivially)
				)
		);
	}

	private void identityActsTrivially()
	{
		assertThat(point.act(actor1.identity()))
				.as("identity must act trivially on any point")
				.isEqualTo(point);
	}
}
