package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.aletheia.collection.cascade.Cascade;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

final class LinearCombinationImpl<S, E> implements LinearCombination<S, E>
{
	private final Cascade<Entry<S, E>> entries;

	static <S, E> LinearCombinationImpl<S, E> empty()
	{
		return new LinearCombinationImpl<>(Cascade.abyss());
	}

	static <S, E> LinearCombinationImpl<S, E> of(final S coefficient, final E element)
	{
		return new LinearCombinationImpl<>(Cascade.beckon(List.of(new Entry<>(coefficient, element))));
	}

	@Override
	public LinearCombination<S, E> addEntry(final S coefficient, final E element)
	{
		return new LinearCombinationImpl<>(Cascade.beckon(
				Stream.concat(entries.stream(), Stream.of(new Entry<>(coefficient, element))).toList()));
	}

	@Override
	public LinearCombination<S, E> removeEntries(final E element)
	{
		return new LinearCombinationImpl<>(Cascade.beckon(
				entries.stream().filter(t -> !Objects.equals(t.element(), element)).toList()));
	}

	@Override
	public LinearCombination<S, E> combine(final LinearCombination<S, E> other)
	{
		final LinearCombinationImpl<S, E> otherImpl = (LinearCombinationImpl<S, E>) other;
		return new LinearCombinationImpl<>(Cascade.beckon(
				Stream.concat(entries.stream(), otherImpl.entries.stream()).toList()));
	}

	@Override
	public LinearCombination<S, E> scaleCoefficients(final UnaryOperator<S> transform)
	{
		return new LinearCombinationImpl<>(Cascade.beckon(
				entries.stream().map(t -> new Entry<>(transform.apply(t.coefficient()), t.element())).toList()));
	}

	@Override
	public List<Entry<S, E>> terms()
	{
		return entries.stream().toList();
	}

	@Override
	public int size()
	{
		return terms().size();
	}

	@Override
	public boolean isEmpty()
	{
		return terms().isEmpty();
	}

	@Override
	public int hashCode()
	{
		return terms().hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof LinearCombinationImpl<?, ?> other)) return false;
		return Objects.equals(terms(), other.terms());
	}

	@Override
	public String toString()
	{
		return "LinearCombination" + terms();
	}

	private LinearCombinationImpl(final Cascade<Entry<S, E>> entries)
	{
		this.entries = entries;
	}
}