package de.gupta.commons.utility.math.algebra.algebraicGroup;

public interface Group<E>
{
	E identity();

	E inverse();

	E multiply(E other);

	E divide(E other);

	default E negation()
	{
		return this.inverse();
	}

	default E add(E other)
	{
		return this.multiply(other);
	}

	default E subtract(E other)
	{
		return this.divide(other);
	}
}