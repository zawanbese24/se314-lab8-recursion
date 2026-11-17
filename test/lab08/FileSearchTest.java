package lab08;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSearchTest {

    @Rule
    public final TemporaryFolder temporary = new TemporaryFolder();

    private Path touch(Path directory, String relativePath) throws IOException {
        Path file = directory.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, relativePath);
        return file;
    }

    private Path root() {
        return temporary.getRoot().toPath();
    }

    @Test
    public void testFindsFileInRootDirectory() throws IOException {
        Path file = touch(root(), "report.pdf");

        SearchResult result = FileSearch.search(root(), List.of("report.pdf"), true);

        assertEquals(List.of(file.toAbsolutePath().normalize()), result.matches("report.pdf"));
        assertEquals(1, result.count("report.pdf"));
        assertEquals(1, result.totalCount());
    }

    @Test
    public void testFindsFileInNestedSubdirectory() throws IOException {
        Path file = touch(root(), "a/b/c/deep.txt");

        SearchResult result = FileSearch.search(root(), List.of("deep.txt"), true);

        assertEquals(1, result.count("deep.txt"));
        assertEquals(file.toAbsolutePath().normalize(), result.matches("deep.txt").get(0));
    }

    @Test
    public void testEmptyDirectoryProducesNoMatches() throws IOException {
        SearchResult result = FileSearch.search(root(), List.of("anything.txt"), true);

        assertEquals(0, result.count("anything.txt"));
        assertEquals(0, result.totalCount());
    }

    @Test
    public void testMissingFileProducesNoMatches() throws IOException {
        touch(root(), "present.txt");

        SearchResult result = FileSearch.search(root(), List.of("absent.txt"), true);

        assertEquals(0, result.count("absent.txt"));
        assertTrue(result.matches("absent.txt").isEmpty());
    }

    @Test
    public void testUnknownNameHasNoMatches() throws IOException {
        touch(root(), "present.txt");

        SearchResult result = FileSearch.search(root(), List.of("present.txt"), true);

        assertEquals(0, result.count("never-requested.txt"));
    }

    @Test
    public void testSearchesSeveralNamesInOneRun() throws IOException {
        touch(root(), "one.txt");
        touch(root(), "sub/two.txt");

        SearchResult result = FileSearch.search(root(),
                List.of("one.txt", "two.txt", "three.txt"), true);

        assertEquals(1, result.count("one.txt"));
        assertEquals(1, result.count("two.txt"));
        assertEquals(0, result.count("three.txt"));
        assertEquals(2, result.totalCount());
        assertEquals(Set.of("one.txt", "two.txt", "three.txt"), result.searchedNames());
    }

    @Test
    public void testCountsEveryOccurrenceOfRepeatedName() throws IOException {
        touch(root(), "report.pdf");
        touch(root(), "docs/report.pdf");
        touch(root(), "archive/2024/report.pdf");

        SearchResult result = FileSearch.search(root(), List.of("report.pdf"), true);

        assertEquals(3, result.count("report.pdf"));
        assertEquals(3, result.matches("report.pdf").size());
    }

    @Test
    public void testCaseInsensitiveSearchMatchesAnyCase() throws IOException {
        touch(root(), "docs/REPORT.PDF");
        touch(root(), "archive/Report.PDF");

        SearchResult result = FileSearch.search(root(), List.of("report.pdf"), false);

        assertEquals(2, result.count("report.pdf"));
    }

    @Test
    public void testCaseSensitiveSearchIgnoresOtherCases() throws IOException {
        touch(root(), "docs/REPORT.PDF");
        touch(root(), "archive/report.pdf");

        SearchResult result = FileSearch.search(root(), List.of("report.pdf"), true);

        assertEquals(1, result.count("report.pdf"));
    }

    @Test
    public void testDirectoryWithMatchingNameIsNotReported() throws IOException {
        touch(root(), "report.pdf/inside.txt");

        SearchResult result = FileSearch.search(root(), List.of("report.pdf"), true);

        assertEquals(0, result.count("report.pdf"));
    }

    @Test
    public void testMatchesFollowSortedDepthFirstOrder() throws IOException {
        Path inSubdirectory = touch(root(), "a/z.txt");
        Path inRoot = touch(root(), "z.txt");

        SearchResult result = FileSearch.search(root(), List.of("z.txt"), true);

        assertEquals(
                List.of(inSubdirectory.toAbsolutePath().normalize(),
                        inRoot.toAbsolutePath().normalize()),
                result.matches("z.txt"));
    }

    @Test
    public void testMissingDirectoryIsRejected() {
        Path missing = root().resolve("no-such-directory");

        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> FileSearch.search(missing, List.of("file.txt"), true));

        assertTrue(failure.getMessage().contains("not an existing directory"));
    }

    @Test
    public void testFileInsteadOfDirectoryIsRejected() throws IOException {
        Path file = touch(root(), "plain.txt");

        assertThrows(IllegalArgumentException.class,
                () -> FileSearch.search(file, List.of("plain.txt"), true));
    }

    @Test
    public void testEmptyFileNameListIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> FileSearch.search(root(), List.of(), true));
    }

    @Test
    public void testBlankFileNameIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> FileSearch.search(root(), List.of("  "), true));
    }

    @Test
    public void testNullArgumentsAreRejected() {
        assertThrows(NullPointerException.class,
                () -> FileSearch.search(null, List.of("file.txt"), true));
        assertThrows(NullPointerException.class,
                () -> FileSearch.search(root(), null, true));
    }

    @Test
    public void testSymbolicLinkCycleTerminates() throws IOException {
        Path cycle = root().resolve("loop");
        touch(root(), "target.txt");
        Files.createDirectories(cycle);
        try {
            Files.createSymbolicLink(cycle.resolve("back"), root());
        } catch (IOException | UnsupportedOperationException unsupported) {
            return;
        }

        SearchResult result = FileSearch.search(root(), List.of("target.txt"), true);

        assertEquals(1, result.count("target.txt"));
    }
}
