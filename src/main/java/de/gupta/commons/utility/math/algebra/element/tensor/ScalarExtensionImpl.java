package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.List;
import java.util.Objects;

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
	public List<LinearCombination.Entry<S, E>> terms()
	{
		return combination.terms();
	}

	@Override
	public ScalarExtension<E, R, S> add(final ScalarExtension<E, R, S> other)
	{
		return new ScalarExtensionImpl<>(combination.add(unwrap(other)));
	}

	private static <E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
	LinearCombination<S, E> unwrap(final ScalarExtension<E, R, S> ext)
	{
		return ((ScalarExtensionImpl<E, R, S>) ext).combination;
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