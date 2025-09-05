package de.gupta.commons.utility.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HashUtility Tests")
class HashUtilityTest
{
	@Nested
	@DisplayName("md5Hash Tests")
	class Md5HashTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validHashTestCases")
		@DisplayName("Test MD5 hash generation for valid inputs")
		void testMd5HashValidInputs(String input, String expectedHash, String description)
		{
			String result = HashUtility.md5Hash(input);

			assertThat(result).as(description)
							  .isNotNull()
							  .hasSize(32)
							  .matches("[a-f0-9]{32}")
							  .isEqualTo(expectedHash);
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("edgeCaseTestCases")
		@DisplayName("Test MD5 hash generation for edge cases")
		void testMd5HashEdgeCases(String input, String description, String expectedHash)
		{
			String result = HashUtility.md5Hash(input);

			assertThat(result).as(description)
							  .isNotNull()
							  .hasSize(32)
							  .matches("[a-f0-9]{32}")
							  .isEqualTo(expectedHash);
		}

		@Test
		@DisplayName("Test MD5 hash generation throws RuntimeException for null input")
		void testMd5HashNullInput()
		{
			assertThatThrownBy(() -> HashUtility.md5Hash(null))
					.isInstanceOf(NullPointerException.class)
					.hasMessageContaining("Cannot invoke \"String.getBytes()\" because \"input\" is null");
		}

		@Test
		@DisplayName("Test MD5 hash consistency - same input produces same hash")
		void testMd5HashConsistency()
		{
			String input = "test string for consistency";
			String firstHash = HashUtility.md5Hash(input);
			String secondHash = HashUtility.md5Hash(input);

			assertThat(firstHash).as("Same input should produce identical hash")
								 .isEqualTo(secondHash);
		}

		@Test
		@DisplayName("Test MD5 hash uniqueness - different inputs produce different hashes")
		void testMd5HashUniqueness()
		{
			String input1 = "first string";
			String input2 = "second string";
			String hash1 = HashUtility.md5Hash(input1);
			String hash2 = HashUtility.md5Hash(input2);

			assertThat(hash1).as("Different inputs should produce different hashes")
							 .isNotEqualTo(hash2);
		}

		@Test
		@DisplayName("Test MD5 hash case sensitivity")
		void testMd5HashCaseSensitivity()
		{
			String lowerCase = "hello world";
			String upperCase = "HELLO WORLD";
			String mixedCase = "Hello World";

			String lowerHash = HashUtility.md5Hash(lowerCase);
			String upperHash = HashUtility.md5Hash(upperCase);
			String mixedHash = HashUtility.md5Hash(mixedCase);

			assertThat(lowerHash).as("Different case inputs should produce different hashes")
								 .isNotEqualTo(upperHash)
								 .isNotEqualTo(mixedHash);

			assertThat(upperHash).as("Different case inputs should produce different hashes")
								 .isNotEqualTo(mixedHash);
		}

		private static Stream<Arguments> validHashTestCases()
		{
			return Stream.of(
					// Known MD5 hash values for common strings
					HashTestCase.of("hello", "5d41402abc4b2a76b9719d911017c592",
							"Simple string 'hello' should produce known MD5 hash"),
					HashTestCase.of("world", "7d793037a0760186574b0282f2f435e7",
							"Simple string 'world' should produce known MD5 hash"),
					HashTestCase.of("Hello World", "b10a8db164e0754105b7a99be72e3fe5",
							"String 'Hello World' should produce known MD5 hash"),
					HashTestCase.of("test", "098f6bcd4621d373cade4e832627b4f6",
							"String 'test' should produce known MD5 hash"),
					HashTestCase.of("password", "5f4dcc3b5aa765d61d8327deb882cf99",
							"String 'password' should produce known MD5 hash"),
					HashTestCase.of("123456", "e10adc3949ba59abbe56e057f20f883e",
							"Numeric string '123456' should produce known MD5 hash"),
					HashTestCase.of("The quick brown fox jumps over the lazy dog",
							"9e107d9d372bb6826bd81d3542a419d6",
							"Long sentence should produce known MD5 hash"),
					HashTestCase.of("abcdefghijklmnopqrstuvwxyz", "c3fcd3d76192e4007dfb496cca67e13b",
							"Alphabet string should produce known MD5 hash")
			).map(tc -> Arguments.of(tc.input(), tc.expectedHash(), tc.description()));
		}

		private static Stream<Arguments> edgeCaseTestCases()
		{
			return Stream.of(
					EdgeCaseTestCase.of("", "Empty string should produce known MD5 hash",
							"d41d8cd98f00b204e9800998ecf8427e"),
					EdgeCaseTestCase.of(" ", "Single space should produce known MD5 hash",
							"7215ee9c7d9dc229d2921a40e899ec5f"),
					EdgeCaseTestCase.of("   ", "Multiple spaces should produce known MD5 hash",
							"628631f07321b22d8c176c200c855e1b"),
					EdgeCaseTestCase.of("\n", "Newline character should produce known MD5 hash",
							"68b329da9893e34099c7d8ad5cb9c940"),
					EdgeCaseTestCase.of("\t", "Tab character should produce known MD5 hash",
							"5e732a1878be2342dbfeff5fe3ca5aa3"),
					EdgeCaseTestCase.of("!@#$%^&*()", "Special characters should produce known MD5 hash",
							"05b28d17a7b6e7024b6e5d8cc43a8bf7"),
					EdgeCaseTestCase.of("áéíóúñü", "Unicode characters should produce known MD5 hash",
							"ca1f2764c02d5795aeff41a017418fab"),
					EdgeCaseTestCase.of("🚀🌟💻", "Emoji characters should produce known MD5 hash",
							"a47366ac06ff898786d30edb74e4e591"),
					EdgeCaseTestCase.of("a".repeat(1000), "Very long string should produce valid MD5 hash",
							"cabe45dcc9ae5b66ba86600cca6b8ba8")
			).map(tc -> Arguments.of(tc.input(), tc.description(), tc.expectedHash()));
		}

		private record HashTestCase(String input, String expectedHash, String description)
		{
			static HashTestCase of(String input, String expectedHash, String description)
			{
				return new HashTestCase(input, expectedHash, description);
			}
		}

		private record EdgeCaseTestCase(String input, String description, String expectedHash)
		{
			static EdgeCaseTestCase of(String input, String description, String expectedHash)
			{
				return new EdgeCaseTestCase(input, description, expectedHash);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests
	{
		@Test
		@DisplayName("Test HashUtility constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> HashUtility.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test HashUtility class is final")
		void testClassIsFinal()
		{
			assertThat(HashUtility.class.getModifiers())
					.as("HashUtility class should be final")
					.satisfies(modifiers -> assertThat(java.lang.reflect.Modifier.isFinal(modifiers)).isTrue());
		}
	}
}