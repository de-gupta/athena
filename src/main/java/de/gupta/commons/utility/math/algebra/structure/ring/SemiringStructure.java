package de.gupta.commons.utility.math.algebra.structure.ring;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;
import java.util.stream.StreamSupport;

public interface SemiringStructure<E>
{
	E zero();

	E one();

	E add(E left, E right);

	E multiply(E left, E right);

	default E addAll(final Iterable<? extends E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return StreamSupport.stream(elements.spliterator(), false)
							.map(element -> (E) element)
							.reduce(zero(), this::add, this::add);
	}

	default E multiplyAll(final Iterable<? extends E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return multiplyAll(Loom.thread(elements));
	}

	default E multiplyAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.weave(one(), this::multiply);
	}
}