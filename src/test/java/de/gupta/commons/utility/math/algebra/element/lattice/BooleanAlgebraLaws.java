package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BooleanAlgebraLaws<E extends BooleanAlgebra<E>> extends DistributiveLatticeLaws<E>
{
	@Property
	default void complementLawForMeet(@ForAll("elements") E a)
	{
		assertThat(a.meet(a.complement())).as("a.meet(a.complement())").isEqualTo(a.bottom());
	}

	@Property
	default void complementLawForJoin(@ForAll("elements") E a)
	{
		assertThat(a.join(a.complement())).as("a.join(a.complement())").isEqualTo(a.top());
	}

	@Property
	default void selfXorIsBottom(@ForAll("elements") E a)
	{
		assertThat(a.xor(a)).as("a.xor(a)").isEqualTo(a.bottom());
	}

	@Property
	default void xorWithComplementIsTop(@ForAll("elements") E a)
	{
		assertThat(a.xor(a.complement())).as("a.xor(a.complement())").isEqualTo(a.top());
	}
}
