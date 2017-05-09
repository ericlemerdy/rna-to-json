import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Association;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;

import java.io.IOException;

import static com.fasterxml.jackson.databind.MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS;
import static java.net.InetAddress.getLocalHost;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class IndexRNATest {

    private AssociationsIndex associationsIndex;

    @ClassRule
    public static DockerRule elasticsearch = ElasticsearchRule.ELASTICSEARCH;

    @Before
    public void setUp() throws IOException {
        associationsIndex = new AssociationsIndex(getLocalHost());
        associationsIndex.createIndex();
    }

    @Test
    public void should_index_one_association() throws JsonProcessingException {
        Association association = Association.builder()
                .codeGestionnaire("ABC")
                .objet("Hello World !")
                .build();

        associationsIndex.getClient().prepareIndex("rna", "associations", "ABC")
                .setSource(new ObjectMapper()
                        .disable(CAN_OVERRIDE_ACCESS_MODIFIERS)
                        .writeValueAsBytes(association), JSON);
    }

    @After
    public void tearDown() throws Exception {
        associationsIndex.deleteIndexIfExists();
    }
}
