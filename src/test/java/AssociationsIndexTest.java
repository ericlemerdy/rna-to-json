import org.junit.ClassRule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;

import java.io.IOException;

import static com.google.common.primitives.Bytes.toArray;
import static java.net.InetAddress.getByAddress;
import static java.nio.file.Files.createTempDirectory;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class AssociationsIndexTest {

    @ClassRule
    public static DockerRule elasticsearchRule = ElasticsearchRule.ELASTICSEARCH;

    @Test
    public void should_create_index() throws IOException {
        String containerIp = elasticsearchRule.getContainerIp();
        byte[] bytes = toArray(stream(containerIp
                .split("\\."))
                .map(s -> (byte) Integer.parseInt(s))
                .collect(toList()));
        AssociationsIndex associationsIndex = new AssociationsIndex(getByAddress(bytes));

        associationsIndex.createIndex();

        assertThat(associationsIndex.getClient().admin().indices().prepareGetIndex().get().getIndices()).containsExactly("rna");
    }
}