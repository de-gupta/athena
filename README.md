# Athena — LLM Agent Reference

**Athena** (`io.github.de-gupta:athena`) is a Java 25 utility library published to Maven Central under GPL-3.0. It
provides production-ready utility functions across 11 domains, emphasizing functional style, immutability, and
mathematical rigor.

Current version: `0.5.4-SNAPSHOT`. Latest release: `v0.5.3`.

---

## Scope and Features

### Collection Utilities (`de.gupta.commons.utility.collection`)

- **`StreamUtility`**: Splits strings into streams using regex patterns or whitespace; handles regex safety.
- **`ListUtility`**: Splits strings to `List<String>`.
- **`SetUtility`**: Splits strings to `Set<String>`, removes blanks, computes unions.
- **`SequencedCollectionUtility`**: Retrieves first/last element matching or not matching a predicate from any
  `SequencedCollection`.

### String Utilities (`de.gupta.commons.utility.string`)

- **`StringFormatUtility`**: Case detection (starts/ends uppercase/lowercase), superscript character conversion.
- **`StringSanitizationUtility`**: Null/blank/empty guards (`requireNotBlank`, `requireNotEmpty`), line breaking.
- **`StringSearchUtility`**: Finds first non-blank string from varargs or lists, pattern-based searching.
- **`StringPartUtility`**: Substring extraction.
- **`StringCaseUtility`**: Case conversions.
- **`StringTokenizeUtility`**: Tokenization.

### I/O Utilities (`de.gupta.commons.utility.io`)

- **`FileWritingUtility`**: Writes strings/lines to files with options: merge, overwrite, create-missing-directories.
  Returns a sealed `WriteResult` (subtypes: `Success`, `Error`, `FileAlreadyExists`, `DirectoryMissing`).
- **`FileReaderUtility`**: Path existence check, raw file reading.
- **`ConfigurationFileReaderUtility`**: Loads `.properties` files into `Map<String, String>`.
- **`PathUtility`**: Path construction and concatenation.

### Comparison Utilities (`de.gupta.commons.utility.comparison`)

- **`ComparisonUtility`**: Generic comparison of two values using a caller-supplied predicate and a `ComparisonType`
  enum.
- **`DescriptivelyComparable<T>`**: Functional interface with default methods for `isEqualTo`, `isGreaterThan`,
  `isLessThan`, and combinations thereof.
- **`ComparisonType`**: Enum: `EQUAL`, `GREATER_THAN`, `LESS_THAN`, `GREATER_THAN_OR_EQUAL`, `LESS_THAN_OR_EQUAL`,
  `NOT_EQUAL`.

### Map Utilities (`de.gupta.commons.utility.map`)

- **`MapUtility`**: Type-safe `get` that throws a caller-supplied exception on missing keys.
- **`SequencedMapBuilder`**: Fluent builder producing immutable `SequencedMap` instances.
- **`MapSorter`**: Sorting by key or value.
- **`MapFactory`**: Factory methods for common map constructions.
- **`EnumMapConstruction`** / **`EnumMapArithmetic`** (`enumMap` subpackage): Enum-keyed map builders and arithmetic
  operations.

### Java Language Analysis (`de.gupta.commons.utility.javaLanguage`)

- **`CodeTypeAnalysisUtility`**: Parses class, interface, and record declarations from Java source strings.
- **`PackageExtractor`** / **`PackageNameValidator`**: Extracts and validates package name syntax.
- **`CommentManager`**: Removes single-line and block comments from Java source.
- **`ClassNameUtility`**: Class name manipulation.
- **`ClassWritingUtility`**: Generates Java class file content.

### Mathematical Structures (`de.gupta.commons.utility.math`)

#### Abstract Algebra (`math.algebra`)

Sealed interface hierarchy implementing algebraic structures:

- **`Semiring<E>`**: `add`, `multiply`
- **`Ring<E>`**: extends `Semiring` with `negate`, `subtract`
- **`Field<E>`**: extends `Ring` with `divide`, `inverse`
- **`EuclideanDomain<E>`**: extends `Ring` with `remainder`, `quotient`, `gcd`
- **`GroupStructure<E>`**: `combine`, `identity`, `inverse`

Free Abelian groups over enum generators:

- **`FreeAbelianGroup<V extends Enum<V>>`**: Sealed interface with `add`, `subtract`, `scale`, `coefficient`, `support`,
  `isZero`, `toMap`.
- **`FreeAbelianGroupCanonicalImplementation`**: Concrete implementation maintaining canonical (zero-suppressed) form
  backed by `EnumMap`.
- **`FreeAbelianGroupFactory`**: `zero(Class<V>)` and `of(Class<V>, Map<V,Integer>)` factory methods.

#### Metric Prefixes (`math.prefix`)

- **`PrefixWithFactor`**: Interface: SI prefix name, symbol, and integer exponent (power of 10).
- **`PrefixArithmetic`**: Operations on prefix values.
- **`PrefixSearch`**: Selects the appropriate prefix for a numeric magnitude.
- **`PrefixWithFactorFactory`**: Creates prefix instances.

### Security Utilities (`de.gupta.commons.utility.security`)

- **`HashUtility`**: MD5 hashing, coupon code generation using custom character sets.

### Properties Utilities (`de.gupta.commons.utility.properties`)

- **`PropertiesUtility`**: Read/write `.properties` files with fallback default values.

---

## Architecture and Design Philosophy

### Utility Class Pattern

Every top-level utility class is `final` with a private no-arg constructor. No instantiation is possible; all methods
are static. This is the universal pattern across all domains.

```java
public final class ExampleUtility
{
	private ExampleUtility()
	{
	}

	public static String doSomething(...)
	{ ...}
}
```

### Null Safety at Boundaries

All public methods that accept `String` or object parameters validate at entry using explicit null/blank checks.
`StringSanitizationUtility.requireNotBlank` and similar guards are used internally. Null inputs produce
`NullPointerException` or `IllegalArgumentException` with descriptive messages.

### Functional and Stream-Oriented Style

- `Stream`, `Optional`, and functional interfaces are preferred over loops.
- Method parameters often accept `Predicate<T>`, `Function<T,R>`, or `Supplier<Exception>` for caller-supplied logic.
- The companion library `io.github.de-gupta:aletheia` (v0.9.0) provides the `Unfolding.beckon()` pattern used for lazy
  unfolding.

### Immutability

- Collections returned by utility methods are unmodifiable (`Collections.unmodifiableSequencedMap`, etc.).
- `SequencedMapBuilder` produces immutable maps.
- `FreeAbelianGroup` operations return new instances; no mutation.

### Sealed Types for Exhaustiveness

- `FreeAbelianGroup<V>` is a sealed interface permitting only `FreeAbelianGroupCanonicalImplementation`.
- `WriteResult` is a sealed interface with known subtypes (`Success`, `Error`, `FileAlreadyExists`, `DirectoryMissing`),
  enabling exhaustive switch expressions in calling code.

### Mathematical Correctness

Abstract algebra interfaces model real algebraic laws. Implementations are expected to satisfy group axioms (
associativity, identity, inverse), ring axioms (distributivity), and field axioms (multiplicative inverse). The `laws`
subpackage provides predicate-based law checkers for validation in tests.

### Adapter and Wrapper Patterns

- `ElementBackedGroupStructure` and `StructuredGroupElement` wrap algebra element types for use in structure-generic
  algorithms.
- `EnumMapArithmetic` adapts `EnumMap` as a free abelian group operation target.

---

## Code Style

### Naming Conventions

- Method names are verbose and self-documenting: `doesThisValueSatisfyTheComparison`, `requireNotBlank`,
  `getFirstNotMatchingFrom`.
- No abbreviations; full English words.
- Boolean-returning methods use question-style names: `isBlank`, `startsWith`.

### Modern Java Features in Use

- **Records**: Used for test case data holders.
- **Sealed interfaces/classes**: `FreeAbelianGroup`, `WriteResult`.
- **Switch expressions** with `->` arrow syntax.
- **Pattern matching** in `instanceof` checks.
- **Text blocks**: For multiline strings.
- **`var`**: Used locally where types are obvious.
- **Java Modules**: `module-info.java` declares all `exports` (~35 packages) and `requires` (`de.gupta.aletheia`).

### Error Handling

- Utility methods throw `IllegalArgumentException` for invalid inputs and `NullPointerException` for nulls.
- I/O operations return `WriteResult` sealed types rather than throwing checked exceptions.
- No checked exceptions are declared in public APIs.

### Constants

Static final fields use `UPPER_SNAKE_CASE`. They appear in test classes and in prefix/algebra constant definitions.

---

## Testing Standards

### Framework

- **JUnit 5 (Jupiter)**: `5.13.1`
- **AssertJ**: `3.27.3`
- No Mockito or other mocking frameworks; tests use real implementations.

### Test Organization

Tests mirror the main source tree. Each utility class has a corresponding `*Test.java`. Tests use `@Nested @DisplayName`
classes to group by scenario category (e.g., `"when the input is null"`, `"when the list has multiple elements"`).

```java

@Nested
@DisplayName("when input is null")
class WhenInputIsNull
{
	@Test
	@DisplayName("should throw NullPointerException")
	void shouldThrow()
	{ ...}
}
```

### Parameterized Testing

Data-driven tests use `@ParameterizedTest @MethodSource`. Provider methods return `Stream<Arguments>`. Test case data is
often encapsulated in private record types:

```java
private record TestCase(String input, boolean expected)
{
}

static Stream<Arguments> provideTestCases()
{
	return Stream.of(
			Arguments.of(new TestCase("hello", false)),
			Arguments.of(new TestCase("Hello", true))
	);
}
```

### What Is Tested

Every public method is covered for:

1. **Happy path**: Expected inputs produce expected outputs.
2. **Edge cases**: Empty strings, empty collections, single-element inputs, zero values.
3. **Null inputs**: Verify `NullPointerException` is thrown.
4. **Invalid inputs**: Blank strings, out-of-range values — verify `IllegalArgumentException`.
5. **Order preservation**: Especially for sequenced collection and map operations.
6. **Custom predicate behavior**: Tests supply lambda predicates to verify caller-controlled logic.

### Assertion Style

- `assertThat(actual).as("descriptive message").isEqualTo(expected)`
- `assertThatThrownBy(() -> ...).isInstanceOf(ExceptionType.class)`
- No bare JUnit assertions; AssertJ is used exclusively.

---

## Build System

### Maven Configuration

- **Compiler**: `maven-compiler-plugin 3.13.0`, source/target Java 25.
- **Sources JAR**: `maven-source-plugin` attaches `*-sources.jar`.
- **Javadoc JAR**: `maven-javadoc-plugin 3.11.2` attaches `*-javadoc.jar`.
- **GPG Signing**: `maven-gpg-plugin` signs all artifacts for Maven Central.
- **Release**: `maven-release-plugin` manages version tags and SCM tagging.
- **Publishing**: `central-publishing-maven-plugin` auto-publishes to Maven Central via Sonatype.

### Module System

`src/main/java/module-info.java` is the authoritative list of all exported packages. When adding a new package, it must
be declared with `exports`. The module requires `de.gupta.aletheia`.

### Running Tests

```
mvn test
```

All 41 test classes run as part of the standard Maven lifecycle. No integration test phase is separated; all tests are
unit tests run in `test`.

---

## Key Dependencies

| Dependency                        | Version  | Scope   | Purpose                                        |
|-----------------------------------|----------|---------|------------------------------------------------|
| `io.github.de-gupta:aletheia`     | `0.9.0`  | compile | Functional programming utilities (`Unfolding`) |
| `org.junit.jupiter:junit-jupiter` | `5.13.1` | test    | Test framework                                 |
| `org.assertj:assertj-core`        | `3.27.3` | test    | Fluent assertions                              |

---

## Package Index

All packages are under the root `de.gupta.commons.utility`:

```
collection
comparison
io.path
io.read
io.write
javaLanguage.classes
javaLanguage.code
javaLanguage.comments
javaLanguage.packages
map
map.enumMap
math.algebra.element
math.algebra.free.abelian
math.algebra.laws
math.algebra.structure
math.prefix
properties
security
string
```