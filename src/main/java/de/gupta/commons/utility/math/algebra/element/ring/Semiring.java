package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.aletheia.collection.folding.Loom;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveMonoid;

import java.util.Objects;

public interface Semiring<E extends Semiring<E>> extends AdditiveMonoid<E>
{
	E one();

	default E multiplyAll(final Iterable<? extends E> others)
	{
		Objects.requireNonNull(others, "others");
		return multiplyAll(Loom.thread(others));
	}

	default E multiplyAll(final Loom<E> others)
	{
		Objects.requireNonNull(others, "others");
		return others.weave(self(), Semiring::multiply);
	}

	@SuppressWarnings("unchecked")
	private E self()
	{
		return (E) this;
	}

	E multiply(E other);
}