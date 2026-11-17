# SE-314 Lab 08 — Recursion

Two recursive command-line programs in Java, plus the JUnit 4 suite that tests them:

- `lab08.FileSearch` — depth-first search for one or more file names under a directory tree,
  with per-name counts and optional case-insensitive matching.
- `lab08.Permutations` — recursive backtracking generator of all string permutations
  (with or without duplicate strings), plus a non-recursive lexicographic generator.
- `lab08.FileSearchTest`, `lab08.PermutationsTest` — 31 JUnit tests covering base cases,
  recursive steps, invalid input and edge conditions.

## Layout

```
src/lab08/      main sources
test/lab08/     JUnit 4 tests
lib/            junit-4.13.2.jar, hamcrest-core-1.3.jar
demo/           small directory tree used by the file-search demo
build/          compiler output (not committed)
```

## Build and test

```sh
javac -Xlint:all -d build/main src/lab08/*.java
javac -Xlint:all -d build/test -cp "build/main:lib/*" test/lab08/*.java
java -cp "build/main:build/test:lib/*" org.junit.runner.JUnitCore \
    lab08.FileSearchTest lab08.PermutationsTest
```

## Run

```sh
java -cp build/main lab08.FileSearchApp demo report.pdf notes.txt
java -cp build/main lab08.FileSearchApp demo REPORT.PDF notes.txt --ignore-case
java -cp build/main lab08.PermutationsApp abc
java -cp build/main lab08.PermutationsApp aab --distinct
java -cp build/main lab08.PermutationsApp aab --iterative
java -cp build/main lab08.PermutationBenchmark 10
```

Exit status of `FileSearchApp`: 0 when every requested name was found, 1 when at
least one was not found, 2 for a usage or input error.

## Eclipse

`File → Import → Existing Projects into Workspace` and select this folder. The
supplied `.project` and `.classpath` put `src` and `test` on the build path and add
the two jars from `lib` as JUnit 4 libraries.
