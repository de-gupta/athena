package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public interface ModuleStructure<V, R> extends AdditiveAbelianGroupStructure<V>
{
	RingStructure<R> scalars();

	V scale(R scalar, V vector);
}