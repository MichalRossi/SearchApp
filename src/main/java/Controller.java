import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    public static Path[] getRoots() {
        if(SystemUtils.IS_OS_WINDOWS){
            return Stream.of(File.listRoots())
                    .map(File::toPath)
                    .toArray(Path[]::new);
        }
        else return new Path[]{Paths.get(System.getProperty("user.home"))};
    }

    public static List<Path> search(String searchName, Path directory) throws IOException {
        return Files.walk(directory)
                .filter(file -> file.getFileName().toFile().getName().equals(searchName))
                .collect(Collectors.toList());
    }

    public static List<Path> searchByExtension(String extension, Path directory) throws IOException {
        return Files.walk(directory)
                .filter(file -> file.getFileName().toFile().getName().endsWith(extension))
                .collect(Collectors.toList());
    }

}
