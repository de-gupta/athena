package de.gupta.commons.utility.comparison;

public interface DescriptivelyComparable<T>
{
	ComparisonResult compare(T other);
}