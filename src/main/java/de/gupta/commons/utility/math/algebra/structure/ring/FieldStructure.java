package de.gupta.commons.utility.math.algebra.structure.ring;

import java.util.Objects;

public interface FieldStructure<E> extends CommutativeRingStructure<E>
{
	E multiplicativeInverse(E element);

	default E divide(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return multiply(left, multiplicativeInverse(right));
	}
}
