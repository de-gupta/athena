package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.aletheia.collection.cascade.Cascade;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

final class LinearCombinationImpl<S extends Ring<S>, E> implements LinearCombination<S, E>
{
	private final Cascade<Entry<S, E>> entries;

	static <S extends Ring<S>, E> LinearCombinationImpl<S, E> of(final S coefficient, final E element)
	{
		return of(List.of(Entry.of(coefficient, element)));
	}

	static <S extends Ring<S>, E> LinearCombinationImpl<S, E> of(final Collection<Entry<S, E>> entries)
	{
		return of(Cascade.beckon(entries));
	}

	static <S extends Ring<S>, E> LinearCombinationImpl<S, E> of(final Cascade<Entry<S, E>> entries)
	{
		return new LinearCombinationImpl<>(normalize(entries));
	}

	private static <S extends Ring<S>, E> Cascade<Entry<S, E>> normalize(final Cascade<Entry<S, E>> entries)
	{
		return entries.amalgamate(Entry::element,
							  (e, f) -> Entry.of(e.coefficient().add(f.coefficient()), e.element())
					  )
		              .discern(e -> !e.coefficient().equals(e.coefficient().zero()));
	}

	@Override
	public LinearCombination<S, E> addEntry(final S coefficient, final E element)
	{
		return of(entries.admit(Entry.of(coefficient, element)));
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
		// TODO: use Cascade.admit when available and normalize
		final LinearCombinationImpl<S, E> otherImpl = (LinearCombinationImpl<S, E>) other;
		return new LinearCombinationImpl<>(Cascade.beckon(Stream.concat(entries.stream(), otherImpl.entries.stream())));
	}

	@Override
	public LinearCombination<S, E> transformCoefficients(final UnaryOperator<S> transform)
	{
		return of(
				entries.transfigure(
						collection -> collection.stream().map(entry -> entry.transformCoefficients(transform)).toList())
		);
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

	@Override
	public LinearCombination<S, E> scale(final S scalar)
	{
		return transformCoefficients(coefficient -> coefficient.multiply(scalar));
	}

	@Override
	public LinearCombination<S, E> negate()
	{
		return transformCoefficients(Ring::negate);
	}

	@Override
	public LinearCombination<S, E> add(final LinearCombination<S, E> other)
	{
		return of(entries.admit(other.terms()));
	}

	@Override
	public LinearCombination<S, E> zero()
	{
		return empty();
	}

	static <S extends Ring<S>, E> LinearCombinationImpl<S, E> empty()
	{
		return of(List.of());
	}

	private LinearCombinationImpl(final Cascade<Entry<S, E>> entries)
	{
		this.entries = entries;
	}
}