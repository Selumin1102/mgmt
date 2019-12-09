package deployment.mgmt.atrifacts.strategies.classpathfile;

import java.io.File;
import lombok.RequiredArgsConstructor;

import static deployment.mgmt.atrifacts.strategies.classpathfile.JarClasspathReader.CLASSPATH_GRADLE_FILE;
import static deployment.mgmt.utils.ZipUtils.containsInnerFile;

@RequiredArgsConstructor
public class JarClasspathReaderSelectorImpl implements JarClasspathReaderSelector {
    private final ArtifactClasspathReader artifactClasspathReader;
    private final MgmtClasspathFileReader mgmtClasspathFileReader;

    @Override
    public JarClasspathReader selectReader(File artifactFile) {
        if (containsInnerFile(artifactFile, CLASSPATH_GRADLE_FILE)) {
            return artifactClasspathReader;
        } else {
            return mgmtClasspathFileReader;
        }
    }
}
