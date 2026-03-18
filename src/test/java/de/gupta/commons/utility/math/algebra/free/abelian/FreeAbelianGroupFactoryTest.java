package de.gupta.commons.utility.math.algebra.free.abelian;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FreeAbelianGroupFactory")
class FreeAbelianGroupFactoryTest
{
	private enum TestEnum
	{
		FIRST, SECOND, THIRD
	}

	private enum SingleValueEnum
	{
		ONLY
	}

	private enum LargeEnum
	{
		VALUE_1, VALUE_2, VALUE_3, VALUE_4, VALUE_5
	}

	@Nested
	@DisplayName("Create Method Tests")
	final class CreateMethodTests
	{
		@ParameterizedTest
		@DisplayName("should create free abelian group for valid enum classes")
		@MethodSource("validEnumClassTestCases")
		<V extends Enum<V>> void testCreateWithValidEnumClasses(final Class<V> enumClass,
																final String description,
																final int expectedEnumConstantsCount)
		{
			final FreeAbelianGroup<V> result = FreeAbelianGroupFactory.create(enumClass);

			assertThat(result)
					.as("Factory should return non-null FreeAbelianGroup for %s", description)
					.isNotNull()
					.isInstanceOf(FreeAbelianGroupCanonicalImplementation.class);

			assertThat(result.generatorType())
					.as("Factory should keep the enum type for %s", description)
					.isEqualTo(enumClass);

			assertThat(result.zero())
					.as("Zero element should be the identity element")
					.isEqualTo(result.identity());

			final FreeAbelianElement<V> allGenerators = result.combineAll(
					Arrays.stream(enumClass.getEnumConstants()).map(result::generator).toList());

			assertThat(allGenerators.exponents())
					.as("Group should support all enum constants for %s", description)
					.hasSize(expectedEnumConstantsCount);

			assertThat(result.isZero(result.zero()))
					.as("Zero element should be recognized as zero")
					.isTrue();
		}

		@Test
		@DisplayName("should create functional free abelian group with basic operations")
		void testCreateProducesFunctionalGroup()
		{
			final FreeAbelianGroup<TestEnum> group = FreeAbelianGroupFactory.create(TestEnum.class);
			final FreeAbelianElement<TestEnum> element = group.add(
					group.generator(TestEnum.FIRST, 3),
					group.generator(TestEnum.SECOND, -2));

			final FreeAbelianElement<TestEnum> negated = group.negate(element);

			assertThat(group.exponentOf(negated, TestEnum.FIRST))
					.as("Negation should flip sign of first component")
					.isEqualTo(-3);

			assertThat(group.exponentOf(negated, TestEnum.SECOND))
					.as("Negation should flip sign of second component")
					.isEqualTo(2);

			assertThat(group.add(element, negated))
					.as("Adding element and its negation should yield zero")
					.isEqualTo(group.zero());
		}

		@Test
		@DisplayName("should create group with proper scaling functionality")
		void testCreateProducesGroupWithScaling()
		{
			final FreeAbelianGroup<TestEnum> group = FreeAbelianGroupFactory.create(TestEnum.class);
			final FreeAbelianElement<TestEnum> element = group.generator(TestEnum.FIRST, 2);

			assertThat(group.scale(element, 3))
					.as("Scaling by 3 should multiply exponent by 3")
					.isEqualTo(group.generator(TestEnum.FIRST, 6));

			assertThat(group.scale(element, 0))
					.as("Scaling by zero should yield zero element")
					.isEqualTo(group.zero());
		}

		@Test
		@DisplayName("should create group with proper subtraction functionality")
		void testCreateProducesGroupWithSubtraction()
		{
			final FreeAbelianGroup<TestEnum> group = FreeAbelianGroupFactory.create(TestEnum.class);

			assertThat(group.subtract(
					group.generator(TestEnum.FIRST, 5),
					group.generator(TestEnum.FIRST, 3)))
					.as("Subtraction should compute correct difference")
					.isEqualTo(group.generator(TestEnum.FIRST, 2));
		}

		private static Stream<Arguments> validEnumClassTestCases()
		{
			return Stream.of(
					ValidEnumClassTestCase.of(TestEnum.class, "simple test enum", 3),
					ValidEnumClassTestCase.of(SingleValueEnum.class, "single value enum", 1),
								 ValidEnumClassTestCase.of(LargeEnum.class, "larger enum with many values", 5))
						 .map(ValidEnumClassTestCase::toArguments);
		}

		private record ValidEnumClassTestCase(
				Class<? extends Enum<?>> enumClass,
				String description,
				int expectedEnumConstantsCount)
		{
			static ValidEnumClassTestCase of(final Class<? extends Enum<?>> enumClass,
											 final String description,
											 final int expectedEnumConstantsCount)
			{
				return new ValidEnumClassTestCase(enumClass, description, expectedEnumConstantsCount);
			}

			Arguments toArguments()
			{
				return Arguments.of(enumClass, description, expectedEnumConstantsCount);
			}
		}
	}

	@Nested
	@DisplayName("Group Properties Tests")
	final class GroupPropertiesTests
	{
		@ParameterizedTest(name = "{1}")
		@DisplayName("should create groups with algebraic properties")
		@MethodSource("algebraicPropertyTestCases")
		<V extends Enum<V>> void testAlgebraicProperties(final Class<V> enumClass, final String propertyDescription)
		{
			final FreeAbelianGroup<V> group = FreeAbelianGroupFactory.create(enumClass);
			final V[] constants = enumClass.getEnumConstants();
			final FreeAbelianElement<V> zero = group.zero();
			final FreeAbelianElement<V> a = group.generator(constants[0], 2);
			final FreeAbelianElement<V> b = constants.length > 1 ? group.generator(constants[1], 3) : zero;

			assertThat(group.add(a, zero))
					.as("Adding zero should be identity operation")
					.isEqualTo(a);

			assertThat(group.add(zero, a))
					.as("Zero should be left identity")
					.isEqualTo(a);

			assertThat(group.add(a, group.negate(a)))
					.as("Adding element and its negation should yield zero")
					.isEqualTo(zero);

			assertThat(group.add(a, b))
					.as("Addition should be commutative")
					.isEqualTo(group.add(b, a));
		}

		@Test
		@DisplayName("should create groups where canonical string representation works")
		void testCanonicalStringRepresentation()
		{
			final FreeAbelianGroup<TestEnum> group = FreeAbelianGroupFactory.create(TestEnum.class);
			final FreeAbelianElement<TestEnum> element = group.add(
					group.generator(TestEnum.FIRST, 2),
					group.generator(TestEnum.SECOND, -1));

			assertThat(group.toCanonicalString(element))
					.as("Canonical string should contain generators and exponents")
					.contains("FIRST^2", "SECOND^-1");

			assertThat(group.toCanonicalString(group.zero()))
					.as("Zero should render canonically")
					.isEqualTo("0");
		}

		@Test
		@DisplayName("should create groups with proper exponent extraction")
		void testExponentExtraction()
		{
			final FreeAbelianGroup<TestEnum> group = FreeAbelianGroupFactory.create(TestEnum.class);
			final FreeAbelianElement<TestEnum> element = group.generator(TestEnum.FIRST, 7);

			assertThat(group.exponentOf(element, TestEnum.FIRST))
					.as("Should extract correct exponent for existing entry")
					.isEqualTo(7);

			assertThat(group.exponentOf(element, TestEnum.SECOND))
					.as("Should return 0 for non-existing entry")
					.isEqualTo(0);
		}

		private static Stream<Arguments> algebraicPropertyTestCases()
		{
			return Stream.of(
					AlgebraicPropertyTestCase.of(TestEnum.class, "test enum algebraic properties"),
					AlgebraicPropertyTestCase.of(SingleValueEnum.class, "single value enum algebraic properties"),
								 AlgebraicPropertyTestCase.of(LargeEnum.class, "large enum algebraic properties"))
						 .map(AlgebraicPropertyTestCase::toArguments);
		}

		private record AlgebraicPropertyTestCase(
				Class<? extends Enum<?>> enumClass,
				String propertyDescription)
		{
			static AlgebraicPropertyTestCase of(final Class<? extends Enum<?>> enumClass,
												final String propertyDescription)
			{
				return new AlgebraicPropertyTestCase(enumClass, propertyDescription);
			}

			Arguments toArguments()
			{
				return Arguments.of(enumClass, propertyDescription);
			}
		}
	}

	@Nested
	@DisplayName("Edge Cases Tests")
	final class EdgeCasesTests
	{
		@Test
		@DisplayName("should handle single value enums correctly")
		void testSingleValueEnum()
		{
			final FreeAbelianGroup<SingleValueEnum> group = FreeAbelianGroupFactory.create(SingleValueEnum.class);
			final FreeAbelianElement<SingleValueEnum> element = group.generator(SingleValueEnum.ONLY, 5);

			assertThat(group.scale(element, 2))
					.as("Single value enum should scale correctly")
					.isEqualTo(group.generator(SingleValueEnum.ONLY, 10));
		}

		@Test
		@DisplayName("should create distinct instances for different calls")
		void testFactoryCreatesDistinctInstances()
		{
			assertThat(FreeAbelianGroupFactory.create(TestEnum.class))
					.as("Factory should create distinct instances")
					.isNotSameAs(FreeAbelianGroupFactory.create(TestEnum.class));
		}

		@Test
		@DisplayName("should handle enums with many constants")
		void testLargeEnum()
		{
			final FreeAbelianGroup<LargeEnum> group = FreeAbelianGroupFactory.create(LargeEnum.class);

			final FreeAbelianElement<LargeEnum> element = group.combineAll(
					Arrays.stream(LargeEnum.values()).map(group::generator).toList());

			assertThat(element.exponents())
					.as("Large enum should support all constants when populated")
					.hasSize(LargeEnum.values().length);

			for (final LargeEnum constant : LargeEnum.values())
			{
				assertThat(group.exponentOf(group.zero(), constant))
						.as("All exponents should start at zero")
						.isEqualTo(0);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("should have private constructor")
		void testPrivateConstructor() throws Exception
		{
			assertThat(Modifier.isPrivate(FreeAbelianGroupFactory.class.getDeclaredConstructor().getModifiers()))
					.as("Constructor should be private")
					.isTrue();
		}

		@Test
		@DisplayName("should be final class")
		void testClassIsFinal()
		{
			assertThat(Modifier.isFinal(FreeAbelianGroupFactory.class.getModifiers()))
					.as("Class should be final")
					.isTrue();
		}
	}
}