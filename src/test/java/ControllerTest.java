import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


class ControllerTest {

    @Test
    void getRoots() {
        //given
        Path[] root = new Path[]{Path.of("/home/michal")};

        //when
        Path[] rootTest = Controller.getRoots();

        //then
        Assertions.assertEquals(root[0], rootTest[0]);

    }

    @Test
    void search() throws IOException {
        //given
        Path directory = Paths.get("/home/michal/test_special");
        Path directory1 = Paths.get("/home/michal/Kurs/test_special");
        Path directory2 = Paths.get("/home/michal/test/test_special");

        Files.createDirectories(directory);
        Files.createDirectories(directory1);
        Files.createDirectories(directory2);

        Set<Path> directories = new HashSet<>();
        directories.add(directory);
        directories.add(directory1);
        directories.add(directory2);

        //when
        Set<Path> testDirectories = new HashSet<>(Controller.search("test_special", Controller.getRoots()[0]));

        //then
        Assertions.assertEquals(directories, testDirectories);
        Assertions.assertThrows(IOException.class, () -> Controller.search("test_special", Paths.get("/home/michal/AAAAxxx")));

        Files.deleteIfExists(directory);
        Files.deleteIfExists(directory1);
        Files.deleteIfExists(directory2);

    }

    @Test
    void searchByExtension() throws IOException {
        //given
        Path file = Path.of("/home/michal/test/test.txt");
        Files.createDirectories(file);

        //when
        List<Path> fileTest = Controller.searchByExtension("txt", Path.of("/home/michal/test/"));

        //then
        Assertions.assertEquals(file, fileTest.get(0));
        Assertions.assertEquals(new ArrayList<Path>() , Controller.searchByExtension("aaa",Path.of("/home/michal/test/")));

        Files.deleteIfExists(file);
    }
}