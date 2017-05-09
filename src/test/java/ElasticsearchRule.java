import pl.domzal.junit.docker.rule.DockerRule;
import pl.domzal.junit.docker.rule.WaitFor;

import java.io.IOException;

import static java.nio.file.Files.createTempDirectory;

public class ElasticsearchRule {
    public static final DockerRule ELASTICSEARCH = DockerRule.builder()
            .imageName("elasticsearch:5.4.0")
            .mountFrom("/usr/share/elasticsearch/data").to(dataDir())
            .env("ES_JAVA_OPTS", "-Xmx1g -Xms1g")
            .cmd("-Etransport.host=0.0.0.0", "-Ediscovery.zen.minimum_master_nodes=1")
            .expose("9200", "9200")
            .expose("9300", "9300")
            .waitFor(WaitFor.logMessage("started"))
            .waitFor(WaitFor.httpPing(9200))
            .build();

    private static String dataDir() {
        try {
            return createTempDirectory("elastic-search-test").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
