package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.structure.ring.FieldStructure;

public interface VectorSpaceStructure<V, F> extends ModuleStructure<V, F>
{
	@Override
	FieldStructure<F> scalars();
}