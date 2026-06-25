package de.gupta.commons.utility.math.algebra.structure.ring;

import de.gupta.commons.utility.math.algebra.structure.algebra.AlgebraStructure;

import java.util.Objects;

public interface FieldStructure<E> extends CommutativeRingStructure<E>, AlgebraStructure<E, E>
{
	default E divide(final E left, final E right)
	{
		Objects.requireNonNull(left, "left");
		Objects.requireNonNull(right, "right");
		return multiply(left, multiplicativeInverse(right));
	}

	E multiplicativeInverse(E element);

	@Override
	default E embed(final E scalar)
	{
		return scalar;
	}

	@Override
	default E scale(final E scalar, final E vector)
	{
		return multiply(scalar, vector);
	}
}