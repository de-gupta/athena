package de.gupta.commons.utility.math.algebra.structure.binary;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

@FunctionalInterface
public interface SemigroupStructure<E>
{
	E combine(E left, E right);

	default E combineAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.forge(this::combine)
					   .decree(() -> new IllegalArgumentException(
							   "A semigroup requires at least one element to combine."));
	}

	default E combineAll(final Iterable<? extends E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return combineAll(Loom.harness(elements));
	}
}