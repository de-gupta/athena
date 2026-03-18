package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.structure.binary.SemigroupStructure;

public interface AdditiveSemigroupStructure<E> extends SemigroupStructure<E>
{
	default E add(final E left, final E right)
	{
		return combine(left, right);
	}

	default E addAll(final Loom<E> elements)
	{
		return combineAll(elements);
	}

	default E addAll(final Iterable<? extends E> elements)
	{
		return combineAll(elements);
	}
}
