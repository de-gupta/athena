package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.structure.binary.MonoidStructure;

public interface AdditiveMonoidStructure<E> extends AdditiveSemigroupStructure<E>, MonoidStructure<E>
{
	default E zero()
	{
		return identity();
	}

	@Override
	default E addAll(final Loom<E> elements)
	{
		return combineAll(elements);
	}
}
