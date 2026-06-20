package de.gupta.commons.utility.math.algebra.free.abelian;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FreeAbelianElement")
class FreeAbelianElementTest
{
	@Test
	@DisplayName("factory should canonicalize, copy and protect exponent maps")
	void factoryShouldCanonicalizeCopyAndProtectExponentMaps()
	{
		final EnumMap<TestEnum, Integer> source = new EnumMap<>(TestEnum.class);
		source.put(TestEnum.FIRST, 3);
		source.put(TestEnum.SECOND, 0);

		final FreeAbelianElement<TestEnum> element = FreeAbelianElement.from(TestEnum.class, source);
		source.put(TestEnum.FIRST, 99);

		assertThat(element.exponents())
				.containsExactly(Map.entry(TestEnum.FIRST, 3));

		assertThatThrownBy(() -> element.exponents().put(TestEnum.THIRD, 1))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	@DisplayName("generator helpers should produce canonical elements")
	void generatorHelpersShouldProduceCanonicalElements()
	{
		assertThat(FreeAbelianElement.generator(TestEnum.FIRST))
				.isEqualTo(FreeAbelianElement.from(TestEnum.class, Map.of(TestEnum.FIRST, 1)));

		assertThat(FreeAbelianElement.generator(TestEnum.SECOND, 0))
				.isEqualTo(FreeAbelianElement.zero(TestEnum.class));
	}

	@Test
	@DisplayName("zero and exponent access should behave naturally")
	void zeroAndExponentAccessShouldBehaveNaturally()
	{
		final FreeAbelianElement<TestEnum> zero = FreeAbelianElement.zero(TestEnum.class);
		final FreeAbelianElement<TestEnum> element = FreeAbelianElement.from(
				TestEnum.class,
				Map.of(TestEnum.FIRST, 2, TestEnum.SECOND, -1));

		assertThat(zero.isZero()).isTrue();
		assertThat(zero.exponentStream()).isEmpty();
		assertThat(element.isZero()).isFalse();
		assertThat(element.exponentOf(TestEnum.FIRST)).isEqualTo(2);
		assertThat(element.exponentOf(TestEnum.THIRD)).isEqualTo(0);
	}

	@Test
	@DisplayName("factory should reject null inputs")
	void factoryShouldRejectNullInputs()
	{
		assertThatThrownBy(() -> FreeAbelianElement.from(null, Map.of(TestEnum.FIRST, 1)))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("generatorType");

		assertThatThrownBy(() -> FreeAbelianElement.from(TestEnum.class, null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("exponents");
	}

	private enum TestEnum
	{
		FIRST, SECOND, THIRD
	}
}