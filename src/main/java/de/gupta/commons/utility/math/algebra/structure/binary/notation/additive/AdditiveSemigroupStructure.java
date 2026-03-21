package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

@FunctionalInterface
public interface AdditiveSemigroupStructure<E>
{
	E add(E left, E right);

	default E addAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.forge(this::add)
					   .decree(() -> new IllegalArgumentException(
							   "An additive semigroup requires at least one element to add."));
	}

	default E addAll(final Iterable<? extends E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return addAll(Loom.harness(elements));
	}
}