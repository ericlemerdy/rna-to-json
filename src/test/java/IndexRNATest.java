import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Association;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class IndexRNATest extends ESIntegTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        new AssociationsIndex(client()).createIndex();
    }

    @Test
    public void should_index_one_association() throws JsonProcessingException {
        Association association = Association.builder()
                .codeGestionnaire("ABC")
                .objet("Hello World !")
                .build();

        client().prepareIndex("rna", "associations", "ABC")
                .setSource(new ObjectMapper().writeValueAsBytes(association), JSON);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        new AssociationsIndex(client()).deleteIndexIfExists();
        super.tearDown();
    }
}
