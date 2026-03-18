package de.gupta.commons.utility.math.algebra.free.abelian;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
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
		<V extends Enum<V>> void testCreateWithValidEnumClasses(final Class<V> enumClass, final String description,
																final int expectedEnumConstantsCount)
		{
			var result = FreeAbelianGroupFactory.create(enumClass);

			assertThat(result)
					.as("Factory should return non-null FreeAbelianGroup for %s", description)
					.isNotNull();

			assertThat(result)
					.as("Factory should return FreeAbelianGroupCanonicalImplementation instance")
					.isInstanceOf(FreeAbelianGroupCanonicalImplementation.class);

			var zeroElement = result.zero();
			assertThat(zeroElement)
					.as("Zero element should not be null")
					.isNotNull();

			var testElement = result.zero();
			for (var constant : enumClass.getEnumConstants())
			{
				testElement.put(constant, 1);
			}
			assertThat(testElement.size())
					.as("Group should support all enum constants for %s", description)
					.isEqualTo(expectedEnumConstantsCount);

			assertThat(result.isZero(zeroElement))
					.as("Zero element should be recognized as zero")
					.isTrue();
		}

		@Test
		@DisplayName("should create functional free abelian group with basic operations")
		void testCreateProducesFunctionalGroup()
		{
			var group = FreeAbelianGroupFactory.create(TestEnum.class);
			var zero = group.zero();

			zero.put(TestEnum.FIRST, 3);
			zero.put(TestEnum.SECOND, -2);

			var negated = group.negate(zero);
			assertThat(group.exponentOf(negated, TestEnum.FIRST))
					.as("Negation should flip sign of first component")
					.isEqualTo(-3);

			assertThat(group.exponentOf(negated, TestEnum.SECOND))
					.as("Negation should flip sign of second component")
					.isEqualTo(2);

			var sum = group.add(zero, negated);
			assertThat(group.isZero(sum))
					.as("Adding element and its negation should yield zero")
					.isTrue();
		}

		@Test
		@DisplayName("should create group with proper scaling functionality")
		void testCreateProducesGroupWithScaling()
		{
			var group = FreeAbelianGroupFactory.create(TestEnum.class);
			var element = group.zero();
			element.put(TestEnum.FIRST, 2);

			var scaled = group.scale(element, 3);
			assertThat(group.exponentOf(scaled, TestEnum.FIRST))
					.as("Scaling by 3 should multiply exponent by 3")
					.isEqualTo(6);

			var zeroScaled = group.scale(element, 0);
			assertThat(group.isZero(zeroScaled))
					.as("Scaling by zero should yield zero element")
					.isTrue();
		}

		@Test
		@DisplayName("should create group with proper subtraction functionality")
		void testCreateProducesGroupWithSubtraction()
		{
			var group = FreeAbelianGroupFactory.create(TestEnum.class);
			var a = group.zero();
			a.put(TestEnum.FIRST, 5);

			var b = group.zero();
			b.put(TestEnum.FIRST, 3);

			var difference = group.subtract(a, b);
			assertThat(group.exponentOf(difference, TestEnum.FIRST))
					.as("Subtraction should compute correct difference")
					.isEqualTo(2);
		}

		private static Stream<Arguments> validEnumClassTestCases()
		{
			return Stream.of(
					ValidEnumClassTestCase.of(TestEnum.class, "simple test enum", 3),
					ValidEnumClassTestCase.of(SingleValueEnum.class, "single value enum", 1),
					ValidEnumClassTestCase.of(LargeEnum.class, "larger enum with many values", 5)
			).map(ValidEnumClassTestCase::toArguments);
		}

		private record ValidEnumClassTestCase(
				Class<? extends Enum<?>> enumClass,
				String description,
				int expectedEnumConstantsCount
		)
		{
			static ValidEnumClassTestCase of(final Class<? extends Enum<?>> enumClass, final String description,
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
		@ParameterizedTest(name = "{2}")
		@DisplayName("should create groups with algebraic properties")
		@MethodSource("algebraicPropertyTestCases")
		<V extends Enum<V>> void testAlgebraicProperties(final Class<V> enumClass, final String propertyDescription)
		{
			var group = FreeAbelianGroupFactory.create(enumClass);
			var zero = group.zero();
			var a = group.zero();
			var b = group.zero();

			if (enumClass.getEnumConstants().length > 0)
			{
				a.put(enumClass.getEnumConstants()[0], 2);
				if (enumClass.getEnumConstants().length > 1)
				{
					b.put(enumClass.getEnumConstants()[1], 3);
				}
			}

			assertThat(group.add(a, zero))
					.as("Adding zero should be identity operation")
					.isEqualTo(a);

			assertThat(group.add(zero, a))
					.as("Zero should be left identity")
					.isEqualTo(a);

			var negA = group.negate(a);
			var shouldBeZero = group.add(a, negA);
			assertThat(group.isZero(shouldBeZero))
					.as("Adding element and its negation should yield zero")
					.isTrue();

			if (enumClass.getEnumConstants().length > 1)
			{
				assertThat(group.add(a, b))
						.as("Addition should be commutative")
						.isEqualTo(group.add(b, a));
			}
		}

		@Test
		@DisplayName("should create groups where canonical string representation works")
		void testCanonicalStringRepresentation()
		{
			var group = FreeAbelianGroupFactory.create(TestEnum.class);
			var element = group.zero();
			element.put(TestEnum.FIRST, 2);
			element.put(TestEnum.SECOND, -1);

			var canonicalString = group.toCanonicalString(element);
			assertThat(canonicalString)
					.as("Canonical string should contain exponents")
					.contains("^2", "^-1");
		}

		@Test
		@DisplayName("should create groups with proper exponent extraction")
		void testExponentExtraction()
		{
			var group = FreeAbelianGroupFactory.create(TestEnum.class);
			var element = group.zero();
			element.put(TestEnum.FIRST, 7);

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
					AlgebraicPropertyTestCase.of(LargeEnum.class, "large enum algebraic properties")
			).map(AlgebraicPropertyTestCase::toArguments);
		}

		private record AlgebraicPropertyTestCase(
				Class<? extends Enum<?>> enumClass,
				String propertyDescription
		)
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
			var group = FreeAbelianGroupFactory.create(SingleValueEnum.class);
			var element = group.zero();
			element.put(SingleValueEnum.ONLY, 5);

			var doubled = group.scale(element, 2);
			assertThat(group.exponentOf(doubled, SingleValueEnum.ONLY))
					.as("Single value enum should scale correctly")
					.isEqualTo(10);
		}

		@Test
		@DisplayName("should create distinct instances for different calls")
		void testFactoryCreatesDistinctInstances()
		{
			var group1 = FreeAbelianGroupFactory.create(TestEnum.class);
			var group2 = FreeAbelianGroupFactory.create(TestEnum.class);

			assertThat(group1)
					.as("Factory should create distinct instances")
					.isNotSameAs(group2);
		}

		@Test
		@DisplayName("should handle enums with many constants")
		void testLargeEnum()
		{
			var group = FreeAbelianGroupFactory.create(LargeEnum.class);
			var zero = group.zero();

			var testElement = group.zero();
			for (var constant : LargeEnum.values())
			{
				testElement.put(constant, 1);
			}
			assertThat(testElement.size())
					.as("Large enum should support all constants when populated")
					.isEqualTo(LargeEnum.values().length);

			for (var constant : LargeEnum.values())
			{
				assertThat(group.exponentOf(zero, constant))
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
			var constructor = FreeAbelianGroupFactory.class.getDeclaredConstructor();
			assertThat(Modifier.isPrivate(constructor.getModifiers()))
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
