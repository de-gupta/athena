package de.gupta.commons.utility.math.prefix;

import de.gupta.commons.utility.collection.SetUtility;

import java.util.List;
import java.util.Set;

public enum Prefix
{
	ATTO("a", 1e-18, Set.of("atto"), Set.of()),
	FEMTO("f", 1e-15, Set.of("femto"), Set.of()),
	PICO("p", 1e-12, Set.of("pico"), Set.of()),
	NANO("n", 1e-9, Set.of("nano"), Set.of()),
	MICRO("µ", 1e-6, Set.of("micro"), Set.of("u")),
	MILLI("m", 1e-3, Set.of("milli"), Set.of()),
	CENTI("c", 1e-2, Set.of("centi"), Set.of()),
	DECI("d", 1e-1, Set.of("deci"), Set.of()),
	UNITY("UNITY", 1, Set.of("unity"), Set.of()),
	DEKA("da", 1e1, Set.of("deka", "deca"), Set.of()),
	HECTO("h", 1e2, Set.of("hecto"), Set.of()),
	KILO("k", 1e3, Set.of("kilo", "kilos"), Set.of()),
	MEGA("M", 1e6, Set.of("mega"), Set.of()),
	GIGA("G", 1e9, Set.of("giga"), Set.of()),
	TERA("T", 1e12, Set.of("tera"), Set.of()),
	PETA("P", 1e15, Set.of("peta"), Set.of()),
	EXA("E", 1e18, Set.of("exa"), Set.of());

	private final String symbol;
	private final double factor;
	private final Set<String> caseInsensitiveAliases;
	private final Set<String> caseSensitiveAliases;
	private final Set<String> specialAliases;

	public String symbol()
	{
		return symbol;
	}

	public double factor()
	{
		return factor;
	}

	public Set<String> caseInsensitiveAliases()
	{
		return caseInsensitiveAliases;
	}

	public Set<String> caseSensitiveAliases()
	{
		return caseSensitiveAliases;
	}

	public Set<String> representations()
	{
		return SetUtility.unionOf(
				List.of(Set.of(symbol), caseInsensitiveAliases, caseSensitiveAliases, specialAliases));
	}

	public int compareByFactor(Prefix other)
	{
		return Double.compare(factor, other.factor);
	}

	public boolean isUnity()
	{
		return this == UNITY;
	}

	public boolean isNotUnity()
	{
		return this != UNITY;
	}

	Prefix(String symbol, double factor, Set<String> caseInsensitiveAliases, Set<String> caseSensitiveAliases)
	{
		this.symbol = symbol;
		this.factor = factor;
		this.caseInsensitiveAliases = caseInsensitiveAliases;
		this.caseSensitiveAliases = caseSensitiveAliases;
		specialAliases = switch (this)
		{
			case UNITY -> Set.of("");
			default -> Set.of();
		};
	}
}