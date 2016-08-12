package org.kantega.documenter;

import fj.Try;
import fj.data.Either;
import fj.data.List;
import fj.data.Validation;
import fj.function.Try0;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.eclipse.aether.*;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.kantega.documenter.api.FailedPluginDoc;
import org.kantega.documenter.api.PluginDoc;
import org.xml.sax.InputSource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MavenDocumentationLocator implements org.kantega.documenter.api.DocumentationLocator {


    private final Map<String, List<Either<FailedPluginDoc, PluginDoc>>> cache =
      new HashMap<>();

    private final ObjectMapper mapper;

    public MavenDocumentationLocator() throws ParserConfigurationException {
        mapper = new ObjectMapper();
    }

    @Override
    public Validation<String, List<Either<FailedPluginDoc, PluginDoc>>> getDocumentationFor(String mavenCoordinates) {

        return MavenCoordinates.fromString(mavenCoordinates, "", "plugins").bind(coordinates -> {
              try {
                  resolve(coordinates);
              }
              catch (Exception e) {
                  e.printStackTrace();
              }

              if (cache.containsKey(mavenCoordinates)) {
                  return Validation.success(cache.get(mavenCoordinates));
              } else {

                  Validation<String, List<MavenCoordinates>> pluginCoords =
                    locatePlugins(coordinates);

                  Validation<String, List<Either<FailedPluginDoc, PluginDoc>>> maybeDistDoc =
                    pluginCoords
                      .map(list ->
                        list
                          .filter(c->c.groupId.startsWith("no.nte.services"))
                          .map(c -> locateDoc(c).toEither()));

                  maybeDistDoc.forEach(distributionDoc -> cache.put(mavenCoordinates, distributionDoc));
                  return maybeDistDoc;
              }
          }
        );
    }


    private Validation<String, List<MavenCoordinates>> locatePlugins(MavenCoordinates mavenCoordinates) {
        Validation<Exception, String> r =
          Try.f(resolve(mavenCoordinates))._1();
        return
          r
            .bind(xml ->
              Try.f((Try0<Document, Exception>) () -> new SAXReader().read(new InputSource(new StringReader(xml))))._1())
            .bind(doc -> {

                ArrayList<MavenCoordinates> coords =
                  new ArrayList<>();

                doc.accept(
                  new VisitorSupport() {
                      public void visit(Element element) {
                          if (element.getName().equals("plugin")) {
                              String groupId = element.attributeValue("groupId");
                              String artifactId = element.attributeValue("artifactId");
                              String version = element.attributeValue("version");
                              coords.add(MavenCoordinates.coords(groupId, artifactId, version, "docs", "json"));
                          }
                      }
                  });

                return Validation.success(List.iterableList(coords));
            })
            .f().map(ex -> ex.getMessage() + " at " + mavenCoordinates);
    }

    private Validation<FailedPluginDoc, PluginDoc> locateDoc(MavenCoordinates mavenCoordinates) {

        Validation<Exception, String> r =
          Try.f(resolve(mavenCoordinates))._1();

        Validation<Exception, JsonNode> jsonV =
          r.bind(resp -> Try.f((Try0<JsonNode, Exception>) () -> mapper.readTree(resp))._1());

        Validation<Exception, PluginDoc> pluginV =
          jsonV.map(node -> new PluginDoc(mavenCoordinates.version, mavenCoordinates.artifactId, node));

        return pluginV.f().map(ex -> new FailedPluginDoc(mavenCoordinates.artifactId,mavenCoordinates.version,ex.getMessage()));

    }


    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                exception.printStackTrace();
            }
        });
        return locator.getService(RepositorySystem.class);
    }

    static final String envM2Home = System.getenv("M2_HOME");
    static final String userHome = System.getProperty("user.home");
    static final Path mavenHome = envM2Home != null ? Paths.get(envM2Home) : Paths.get(userHome, ".m2");
    static final Path localRepoPath = mavenHome.resolve("repository");


    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(localRepoPath.toFile());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));


        return session;
    }


    public static java.util.List<RemoteRepository> newRepositories(
      RepositorySystem system,
      RepositorySystemSession session) {
        return new ArrayList<>(Arrays.asList(newNteRepository(), newNteSnapsRepository()));
    }

    private static RemoteRepository newNteRepository() {
        return new RemoteRepository.Builder("nte", "default", "http://nexus.nte.local/nexus/content/groups/public/").build();
    }

    private static RemoteRepository newNteSnapsRepository() {
        return new RemoteRepository.Builder("ntesnaps", "default", "http://nexus.nte.local/nexus/content/groups/NTESnapshots/").build();
    }


    public static Try0<String, Exception> resolve(MavenCoordinates coordinates) {
        return () -> {
            RepositorySystem system = newRepositorySystem();

            RepositorySystemSession session = newRepositorySystemSession(system);

            Artifact download =
              new DefaultArtifact(coordinates.groupId, coordinates.artifactId, coordinates.classifier,coordinates.extension, coordinates.version);

            ArtifactRequest artifactRequest = new ArtifactRequest();
            artifactRequest.setArtifact(download);
            artifactRequest.setRepositories(newRepositories(system, session));
            ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
            String content = new String(Files.readAllBytes(artifactResult.getArtifact().getFile().toPath()), Charset.forName("UTF-8"));
            return content;
        };
    }

    //For reading settings.xml
    static final File DEFAULT_USER_SETTINGS_FILE = mavenHome.resolve("settings.xml").toFile();
    static final File DEFAULT_GLOBAL_SETTINGS_FILE =
      new File(System.getProperty("maven.home", envM2Home != null ? envM2Home : ""), "conf/settings.xml");


    private static synchronized Settings getSettings() throws SettingsBuildingException {
        SettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest();
        settingsBuildingRequest.setSystemProperties(System.getProperties());
        settingsBuildingRequest.setUserSettingsFile(DEFAULT_USER_SETTINGS_FILE);
        settingsBuildingRequest.setGlobalSettingsFile(DEFAULT_GLOBAL_SETTINGS_FILE);

        SettingsBuildingResult settingsBuildingResult;
        DefaultSettingsBuilderFactory mvnSettingBuilderFactory = new DefaultSettingsBuilderFactory();
        DefaultSettingsBuilder settingsBuilder = mvnSettingBuilderFactory.newInstance();
        settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);

        Settings effectiveSettings = settingsBuildingResult.getEffectiveSettings();
        return effectiveSettings;
    }


}
