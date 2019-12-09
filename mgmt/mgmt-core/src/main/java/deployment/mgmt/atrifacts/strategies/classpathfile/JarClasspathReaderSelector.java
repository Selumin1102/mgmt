package deployment.mgmt.atrifacts.strategies.classpathfile;

import java.io.File;

public interface JarClasspathReaderSelector {
    JarClasspathReader selectReader(File artifactFile);
}
