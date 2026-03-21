package de.gupta.commons.utility.math.algebra.structure.binary;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface MonoidStructure<E> extends SemigroupStructure<E>
{
	E identity();

	@Override
	default E multiplyAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.weave(identity(), this::multiply);
	}
}