package de.gupta.commons.utility.math.algebra.structure.ring;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveMonoidStructure;

import java.util.Objects;

public interface SemiringStructure<E> extends AdditiveMonoidStructure<E>
{
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

	E one();

	E multiply(E left, E right);
}