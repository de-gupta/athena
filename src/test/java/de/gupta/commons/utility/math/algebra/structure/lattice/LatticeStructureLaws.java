package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface LatticeStructureLaws<E> extends MeetSemilatticeStructureLaws<E>, JoinSemilatticeStructureLaws<E>
{
	@Property
	default void meetAbsorbs(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(subject().meet(a, subject().join(a, b))).as("meet(a, join(a, b))").isEqualTo(a);
	}

	@Override
	LatticeStructure<E> subject();

	@Property
	default void joinAbsorbs(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(subject().join(a, subject().meet(a, b))).as("join(a, meet(a, b))").isEqualTo(a);
	}
}