package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static deployment.mgmt.utils.ZipUtils.readInnerFile;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class ArtifactClasspathReader implements JarClasspathReader {
    private static final int MINIMAL_IDENTIFIERS_COUNT = 2;

    @Override
    public List<Artifact> extractClasspath(File artifactFile, Artifact artifact) {
        String classpath = new String(readInnerFile(artifactFile, CLASSPATH_GRADLE_FILE));

        return stream(classpath.split(", "))
                .map(ArtifactClasspathReader::toArtifact)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static Artifact toArtifact(String mavenString) {
        if (hasEnoughData(mavenString)) {
            return Artifact.fromMavenString(mavenString);
        } else {
            return null;
        }
    }

    private static boolean hasEnoughData(String mavenString) {
        return mavenString.chars().filter(ch -> ch == ':').count() >= MINIMAL_IDENTIFIERS_COUNT;
    }
}
