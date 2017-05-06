import org.assertj.core.api.Assertions;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

public class AssociationsIndexTest extends ESIntegTestCase {

    @Test
    public void should_create_index() throws Exception {
        AssociationsIndex associationsIndex = new AssociationsIndex(client());

        associationsIndex.createIndex();

        Assertions.assertThat(client().admin().indices().prepareGetIndex().get().getIndices()).containsExactly("rna");
    }
}