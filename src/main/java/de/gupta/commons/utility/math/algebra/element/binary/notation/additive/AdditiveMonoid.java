package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.aletheia.collection.folding.Loom;

import java.util.Objects;

public interface AdditiveMonoid<E extends AdditiveMonoid<E>> extends AdditiveSemigroup<E>
{
	E zero();

	@Override
	default E addAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(zero(), AdditiveSemigroup::add);
	}
}