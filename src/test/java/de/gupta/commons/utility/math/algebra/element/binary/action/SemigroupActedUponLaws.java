package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Semigroup;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record SemigroupActedUponLaws<S extends Semigroup<S>, X extends SemigroupActedUpon<S, X>>(
		X point, S actor1, S actor2)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("x.act(s*t) == x.act(t).act(s)", this::actionIsCompatibleWithMultiplication)
		);
	}

	// left action law: act(s*t, x) = act(s, act(t, x)) — t is applied first, order reverses in element notation
	private void actionIsCompatibleWithMultiplication()
	{
		X left = point.act(actor1.multiply(actor2));
		X right = point.act(actor2).act(actor1);

		assertThat(left)
				.as("x.act(s*t) must equal x.act(t).act(s)")
				.isEqualTo(right);
	}
}
