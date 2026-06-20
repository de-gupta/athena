package de.gupta.commons.utility.math.algebra.structure.binary;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

@FunctionalInterface
public interface SemigroupStructure<E>
{
	E multiply(E left, E right);

	default E multiplyAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.forge(this::multiply)
					   .decree(() -> new IllegalArgumentException(
							   "A semigroup requires at least one element to multiply."));
	}

	default E multiplyAll(final Iterable<? extends E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return multiplyAll(Loom.thread(elements));
	}
}