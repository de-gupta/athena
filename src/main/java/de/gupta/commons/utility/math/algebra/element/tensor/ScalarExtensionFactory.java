package de.gupta.commons.utility.math.algebra.element.tensor;

import de.gupta.commons.utility.math.algebra.element.algebra.Algebra;
import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public final class ScalarExtensionFactory
{
	public static <E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
	ScalarExtension<E, R, S> of(final S coefficient, final E element)
	{
		return ScalarExtensionImpl.of(coefficient, element);
	}

	public static <E extends Module<E, R>, R extends Ring<R>, S extends Algebra<R, S>>
	ScalarExtension<E, R, S> empty()
	{
		return ScalarExtensionImpl.empty();
	}

	private ScalarExtensionFactory()
	{
	}
}