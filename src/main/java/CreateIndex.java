import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.net.InetAddress.getLocalHost;
import static org.elasticsearch.client.Requests.indicesExistsRequest;
import static org.elasticsearch.common.settings.Settings.EMPTY;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class CreateIndex {

    public static void main(String[] args) throws URISyntaxException, IOException {
        try (TransportClient client = new PreBuiltTransportClient(EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(getLocalHost(), 9300))) {
            ActionFuture<IndicesExistsResponse> exists = client.admin().indices().exists(indicesExistsRequest("rna"));
            if (exists.actionGet().isExists()) {
                client.admin().indices().prepareDelete("rna").get();
            }
            CreateIndexRequestBuilder rna = client.admin().indices().prepareCreate("rna");
            rna.setSource(toByteArray(CreateIndex.class.getResourceAsStream("mapping.json")), JSON).get();
        }
    }

}
