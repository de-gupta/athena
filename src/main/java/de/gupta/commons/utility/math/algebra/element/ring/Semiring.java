package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveSemigroup;

import java.util.Objects;
import java.util.stream.StreamSupport;

public interface Semiring<E extends Semiring<E>> extends AdditiveSemigroup<E>
{
	E zero();

	E one();

	default E sumAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return StreamSupport.stream(others.spliterator(), false)
		                    .map(other -> (E) other)
		                    .reduce(self(), Semiring::add, Semiring::add);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	default E multiplyAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return multiplyAll(Loom.thread(others));
	}

	default E multiplyAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), Semiring::multiply);
	}

	E multiply(E other);
}