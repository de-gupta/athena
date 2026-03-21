package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface AdditiveMonoidStructure<E> extends AdditiveSemigroupStructure<E>
{
	E zero();

	@Override
	default E addAll(final Loom<E> elements)
	{
		Objects.requireNonNull(elements, "elements");
		return elements.weave(zero(), this::add);
	}
}