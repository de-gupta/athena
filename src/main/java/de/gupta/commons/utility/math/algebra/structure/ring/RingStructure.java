package de.gupta.commons.utility.math.algebra.structure.ring;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveGroupStructure;

public interface RingStructure<E> extends SemiringStructure<E>, AdditiveGroupStructure<E>
{
	E additiveInverse(E element);
}