package no.cantara.cs.persistence;

import no.cantara.cs.dto.Application;
import no.cantara.cs.dto.ApplicationConfig;
import no.cantara.cs.dto.Client;

import java.util.*;

/**
 * This class is a mess. Should be totally redesigned after the public API is stable.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-07-09.
 */
//Add @Service and remove @Service from PersistedConfigRepo to activate.
public class InMemConfigRepo implements ApplicationConfigDao {
    private final Map<String, Application> idToApplication;
    private final Map<String, ApplicationConfig> configs;
    private final Map<String, Client> clients;
    private final Map<String, String> applicationIdToConfigIdMapping;


    public InMemConfigRepo() {
        this.idToApplication = new HashMap<>();
        this.configs = new HashMap<>();
        this.applicationIdToConfigIdMapping = new HashMap<>();
        this.clients = new HashMap<>();
        //addTestData();
    }

    @Override
    public Application createApplication(Application newApplication) {
        newApplication.id = UUID.randomUUID().toString();
        idToApplication.put(newApplication.id, newApplication);
        return newApplication;
    }

    @Override
    public ApplicationConfig createApplicationConfig(String applicationId, ApplicationConfig newConfig) {
        newConfig.setId(UUID.randomUUID().toString());
        configs.put(newConfig.getId(), newConfig);
        applicationIdToConfigIdMapping.put(applicationId, newConfig.getId());
        return newConfig;
    }

    @Override
    public ApplicationConfig getApplicationConfig(String configId) {
        return configs.get(configId);
    }

    @Override
    public ApplicationConfig deleteApplicationConfig(String configId) {
        return configs.remove(configId);
    }

    @Override
    public ApplicationConfig findApplicationConfigByArtifactId(String artifactId) {
        Application application = findApplication(artifactId);
        if (application == null) {
            return null;
        }

        String configId = applicationIdToConfigIdMapping.get(application.id);
        if (configId == null) {
            return null;
        }
        return configs.get(configId);
    }

    private Application findApplication(String artifactId) {
        for (Map.Entry<String, Application> entry : idToApplication.entrySet()) {
            if (entry.getValue().artifactId.equals(artifactId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public ApplicationConfig updateApplicationConfig(ApplicationConfig updatedConfig) {
        return configs.put(updatedConfig.getId(), updatedConfig);
    }

    @Override
    public String getArtifactId(ApplicationConfig config) {
        String configId = config.getId();
        String applicationId = applicationIdToConfigIdMapping.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(configId))
                .findFirst()
                .get()
                .getKey();

        Application application = idToApplication.get(applicationId);
        return application.artifactId;
    }

    @Override
    public Map<String, ApplicationConfig> getAllConfigs() {
        //TODO: Implementation
        return null;
    }

    @Override
    public List<Application> getApplications() {
        return new ArrayList<>(idToApplication.values());
    }

}
