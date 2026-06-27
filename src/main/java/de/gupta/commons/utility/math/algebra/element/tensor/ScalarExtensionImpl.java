package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

final class ScalarExtensionImpl<E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
		implements ScalarExtension<E, R, S>
{
	private final LinearCombination<S, E> combination;

	static <E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
	ScalarExtensionImpl<E, R, S> of(final S coefficient, final E element)
	{
		return new ScalarExtensionImpl<>(LinearCombinationFactory.of(coefficient, element));
	}

	@Override
	public boolean isEmpty()
	{
		return combination.isEmpty();
	}

	@Override
	public <T> T fold(final T identity, final BiFunction<S, E, T> mapper, final BinaryOperator<T> combiner)
	{
		return combination.terms().stream()
		                  .map(t -> mapper.apply(t.coefficient(), t.element()))
		                  .reduce(identity, combiner);
	}

	@Override
	public ScalarExtension<E, R, S> add(final ScalarExtension<E, R, S> other)
	{
		return switch (other)
		{
			case ScalarExtensionImpl<E, R, S> impl -> new ScalarExtensionImpl<>(combination.add(impl.combination));
		};
	}

	@Override
	public ScalarExtension<E, R, S> scale(final S scalar)
	{
		return new ScalarExtensionImpl<>(combination.scale(scalar));
	}

	@Override
	public ScalarExtension<E, R, S> negate()
	{
		return new ScalarExtensionImpl<>(combination.negate());
	}

	@Override
	public ScalarExtension<E, R, S> zero()
	{
		return empty();
	}

	static <E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
	ScalarExtensionImpl<E, R, S> empty()
	{
		return new ScalarExtensionImpl<>(LinearCombinationFactory.<S, E>empty());
	}

	@Override
	public int hashCode()
	{
		return combination.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof ScalarExtensionImpl<?, ?, ?> other)) return false;
		return Objects.equals(combination, other.combination);
	}

	@Override
	public String toString()
	{
		return "ScalarExtension" + combination.terms();
	}

	private ScalarExtensionImpl(final LinearCombination<S, E> combination)
	{
		this.combination = combination;
	}
}