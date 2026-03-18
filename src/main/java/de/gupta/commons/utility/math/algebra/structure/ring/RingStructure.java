package de.gupta.commons.utility.math.algebra.structure.ring;

import java.util.Objects;

public interface RingStructure<E> extends SemiringStructure<E>
{
	E additiveInverse(E element);

	default E subtract(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return add(left, additiveInverse(right));
	}
}