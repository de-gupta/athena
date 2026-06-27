package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public sealed interface ScalarExtension<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		extends Module<ScalarExtension<E, R, S>, S> permits ScalarExtensionImpl
{
	default boolean isEmpty()
	{
		return fold(Boolean.TRUE, (_, _) -> Boolean.FALSE, (a, b) -> a && b);
	}

	<T> T fold(final T identity, final BiFunction<S, E, T> mapper, final BinaryOperator<T> combiner);

	default E project(final ProjectionPolicy<E, R, S> policy)
	{
		return policy.project(this);
	}
}