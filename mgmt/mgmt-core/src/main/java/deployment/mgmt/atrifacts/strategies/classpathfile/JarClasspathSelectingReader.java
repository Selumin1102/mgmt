package deployment.mgmt.atrifacts.strategies.classpathfile;

import deployment.mgmt.atrifacts.Artifact;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;

import static deployment.mgmt.utils.ZipUtils.containsInnerFile;

@RequiredArgsConstructor
public class JarClasspathSelectingReader implements JarClasspathReader {
    private final ArtifactClasspathReader artifactClasspathReader;
    private final MgmtClasspathFileReader mgmtClasspathFileReader;

    @Override
    public List<Artifact> extractClasspath(File artifactFile, Artifact artifact) {
        JarClasspathReader reader;
        if (containsInnerFile(artifactFile, CLASSPATH_GRADLE_FILE)) {
            reader = artifactClasspathReader;
        } else {
            reader = mgmtClasspathFileReader;
        }
        return reader.extractClasspath(artifactFile, artifact);
    }
}
