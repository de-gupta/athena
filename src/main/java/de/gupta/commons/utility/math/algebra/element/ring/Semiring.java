package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;
import java.util.stream.StreamSupport;

public interface Semiring<E extends Semiring<E>>
{
	E zero();

	E one();

	E add(E other);

	E multiply(E other);

	default E sumAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return StreamSupport.stream(others.spliterator(), false)
							.map(other -> (E) other)
							.reduce(self(), Semiring::add, Semiring::add);
	}

	default E productAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), Semiring::multiply);
	}

	default E productAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return productAll(Loom.thread(others));
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}
}