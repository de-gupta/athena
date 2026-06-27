package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.laws.algebra.ScalarExtensionLaw;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface ScalarExtensionLaws<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		extends ScalarExtensionLaw<E, R, S>
{
	@Provide
	Arbitrary<ScalarExtension<E, R, S>> elements();

	@Provide
	Arbitrary<S> scalars();

	@Property
	default void scaleByOneIsIdentity(@ForAll("elements") final ScalarExtension<E, R, S> x,
	                                  @ForAll("scalars") final S s)
	{
		assertThat(x.scale(s.one()))
				.as("x.scale(1) == x")
				.isEqualTo(x);
	}

	@Property
	default void scaleDistributesOverModuleAddition(@ForAll("elements") final ScalarExtension<E, R, S> x,
	                                                @ForAll("elements") final ScalarExtension<E, R, S> y,
	                                                @ForAll("scalars") final S s)
	{
		assertThat(x.add(y).scale(s))
				.as("(x + y).scale(s) == x.scale(s) + y.scale(s)")
				.isEqualTo(x.scale(s).add(y.scale(s)));
	}

	@Property
	default void scaleDistributesOverScalarAddition(@ForAll("elements") final ScalarExtension<E, R, S> x,
	                                                @ForAll("scalars") final S s1,
	                                                @ForAll("scalars") final S s2)
	{
		assertThat(x.scale(s1.add(s2)))
				.as("x.scale(s1 + s2) == x.scale(s1) + x.scale(s2)")
				.isEqualTo(x.scale(s1).add(x.scale(s2)));
	}

	@Property
	default void scaleIsCompatibleWithScalarMultiplication(@ForAll("elements") final ScalarExtension<E, R, S> x,
	                                                       @ForAll("scalars") final S s1,
	                                                       @ForAll("scalars") final S s2)
	{
		assertThat(x.scale(s1.multiply(s2)))
				.as("x.scale(s1 * s2) == x.scale(s2).scale(s1)  [left module: (s1*s2)*x = s1*(s2*x)]")
				.isEqualTo(x.scale(s2).scale(s1));
	}

	@Property
	default void addingZeroIsIdentity(@ForAll("elements") final ScalarExtension<E, R, S> x)
	{
		assertThat(x.add(x.zero()))
				.as("x + 0 == x")
				.isEqualTo(x);
	}

	@Property
	default void addingNegateIsZero(@ForAll("elements") final ScalarExtension<E, R, S> x)
	{
		assertThat(x.add(x.negate()).isEmpty())
				.as("x + (-x) == 0")
				.isTrue();
	}
}