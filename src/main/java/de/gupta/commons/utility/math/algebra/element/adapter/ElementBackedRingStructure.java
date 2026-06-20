package de.gupta.commons.utility.math.algebra.element.adapter;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public record ElementBackedRingStructure<E extends Ring<E>>(Supplier<E> zeroSupplier, Supplier<E> oneSupplier,
                                                            BinaryOperator<E> negateFunction)
		implements RingStructure<E>
{
	public static <E extends Ring<E>> ElementBackedRingStructure<E> of(final Supplier<E> zeroSupplier,
	                                                                   final Supplier<E> oneSupplier,
	                                                                   final BinaryOperator<E> negateFunction)
	{
		return new ElementBackedRingStructure<>(zeroSupplier, oneSupplier, negateFunction);
	}

	public ElementBackedRingStructure
	{
		Objects.requireNonNull(zeroSupplier, "zeroSupplier");
		Objects.requireNonNull(oneSupplier, "oneSupplier");
	}

	@Override
	public E zero()
	{
		return zeroSupplier.get();
	}

	@Override
	public E one()
	{
		return oneSupplier.get();
	}

	@Override
	public E multiply(final E left, final E right)
	{
		return left.multiply(right);
	}

	@Override
	public E add(final E left, final E right)
	{
		return left.add(right);
	}

	@Override
	public E negate(final E element)
	{
		return negateFunction.apply(element, element);
	}
}