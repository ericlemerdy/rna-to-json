import com.google.common.annotations.VisibleForTesting;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.net.InetAddress.getLocalHost;
import static org.elasticsearch.client.Requests.indicesExistsRequest;
import static org.elasticsearch.common.settings.Settings.EMPTY;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class AssociationsIndex implements AutoCloseable {

    private Client client;

    public AssociationsIndex(Client client) {
        this.client = client;
    }

    public AssociationsIndex(InetAddress inetAddress) throws UnknownHostException {
        this(new PreBuiltTransportClient(EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(inetAddress, 9300)));
    }

    @VisibleForTesting
    void createIndex() throws IOException {
        deleteIndexIfExists();
        CreateIndexRequestBuilder rna = client.admin().indices().prepareCreate("rna");
        rna.setSource(toByteArray(AssociationsIndex.class.getResourceAsStream("mapping.json")), JSON).get();
    }

    @VisibleForTesting
    void deleteIndexIfExists() {
        ActionFuture<IndicesExistsResponse> exists = client.admin().indices().exists(indicesExistsRequest("rna"));
        if (exists.actionGet().isExists()) {
            client.admin().indices().prepareDelete("rna").get();
        }
    }

    @VisibleForTesting
    Client getClient() {
        return client;
    }

    public static void main(String[] args) throws IOException {
        try (AssociationsIndex associationsIndex = new AssociationsIndex(getLocalHost())) {
            associationsIndex.createIndex();
        }
    }

    @Override
    public void close() {
        this.client.close();
    }
}
