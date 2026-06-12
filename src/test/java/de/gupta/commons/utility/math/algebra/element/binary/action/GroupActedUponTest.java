package de.gupta.commons.utility.math.algebra.element.binary.action;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("GroupActedUpon — group action laws (element side)")
final class GroupActedUponTest
{
	@TestFactory
	@DisplayName("integer position acted upon by integer shifts satisfies all group action laws")
	Stream<DynamicTest> groupActedUponLaws()
	{
		return new GroupActedUponLaws<>(new Position(10), new Shift(3), new Shift(5)).tests();
	}

	private record Shift(int value) implements de.gupta.commons.utility.math.algebra.element.binary.Group<Shift>
	{
		@Override
		public Shift multiply(final Shift other)
		{
			return new Shift(value + other.value);
		}

		@Override
		public Shift identity()
		{
			return new Shift(0);
		}

		@Override
		public Shift inverse()
		{
			return new Shift(-value);
		}
	}

	private record Position(int value) implements GroupActedUpon<Shift, Position>
	{
		@Override
		public Position act(final Shift actor)
		{
			return new Position(value + actor.value());
		}
	}
}
