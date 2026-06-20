package de.gupta.commons.utility.math.algebra.element.adapter;

import de.gupta.commons.utility.math.algebra.element.binary.Group;
import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

import java.util.Objects;
import java.util.function.Supplier;

public record ElementBackedGroupStructure<E extends Group<E>>(Supplier<E> identitySupplier) implements GroupStructure<E>
{
	public static <E extends Group<E>> ElementBackedGroupStructure<E> of(final Supplier<E> identitySupplier)
	{
		return new ElementBackedGroupStructure<>(identitySupplier);
	}

	public ElementBackedGroupStructure
	{
		Objects.requireNonNull(identitySupplier, "identitySupplier");
	}

	@Override
	public E multiply(final E left, final E right)
	{
		return left.multiply(right);
	}

	@Override
	public E identity()
	{
		return identitySupplier.get();
	}

	@Override
	public E inverse(final E element)
	{
		return element.inverse();
	}
}