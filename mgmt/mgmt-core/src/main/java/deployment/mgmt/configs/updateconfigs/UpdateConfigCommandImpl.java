package deployment.mgmt.configs.updateconfigs;

import deployment.mgmt.configs.componentgroup.ComponentGroupService;
import deployment.mgmt.configs.componentgroup.GroupDescription;
import deployment.mgmt.configs.filestructure.DeployFileStructure;
import deployment.mgmt.microconfig.factory.MgmtMicroConfigAdapter;
import deployment.mgmt.process.stop.StopCommand;
import deployment.mgmt.update.scriptgenerator.MgmtScriptGenerator;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;

import static deployment.mgmt.configs.updateconfigs.UpdateConfigOption.*;
import static io.microconfig.utils.FileUtils.delete;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RequiredArgsConstructor
public class UpdateConfigCommandImpl implements UpdateConfigCommand {
    private final ComponentGroupService componentGroupService;
    private final DeployFileStructure deployFileStructure;
    private final StopCommand stopCommand;
    private final NewServicePreparer newServicePreparer;
    private final MgmtScriptGenerator mgmtScriptGenerator;

    @Override
    public void buildConfig(UpdateConfigOption... options) {
        announce("Going to build configs");
        List<String> servicesBeforeUpdate = componentGroupService.getServices();

        buildConfigs();
        applyAlteredVersions(options);
        stopRemovedServices(servicesBeforeUpdate);
        generateMgmtScript();
        prepareNewServices(options);

        announce("Updated configs");
    }

    private void stopRemovedServices(List<String> servicesBeforeUpdate) {
        List<String> currentServices = componentGroupService.getServices();

        servicesBeforeUpdate.parallelStream()
                .filter(s -> !currentServices.contains(s))
                .forEach(s -> {
                    stopCommand.stop(s);
                    delete(deployFileStructure.service().getServiceDir(s));
                });
    }

    private void buildConfigs() {
        GroupDescription groupDescription = componentGroupService.getDescription();

        File repoDir = deployFileStructure.configs().getMicroconfigSourcesRootDir();
        File componentsDir = deployFileStructure.service().getComponentsDir();
        info("Source: " + repoDir + ". Destination: " + componentsDir);

        MgmtMicroConfigAdapter.execute(
                groupDescription.getEnv(),
                singletonList(groupDescription.getGroup()),
                emptyList(), repoDir,
                componentsDir
        );
    }

    private void applyAlteredVersions(UpdateConfigOption... options) {
        if (isPresent(CLEAN_ALTERED_VERSIONS, options)) {
            componentGroupService.cleanAlteredVersions();
        } else {
            componentGroupService.replaceServiceVersionWithAltered();
        }
    }

    private void generateMgmtScript() {
        mgmtScriptGenerator.generateMgmtScript();
    }

    private void prepareNewServices(UpdateConfigOption... options) {
        newServicePreparer.prepare(componentGroupService.getServices(), isPresent(SKIP_CLASSPATH_BUILD_FOR_SNAPSHOT, options));
    }
}