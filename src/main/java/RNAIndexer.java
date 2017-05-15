import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.annotations.VisibleForTesting;
import data.Association;
import data.Id;
import data.Index;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static com.fasterxml.jackson.databind.MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.net.InetAddress.getLocalHost;
import static java.nio.charset.Charset.forName;
import static org.elasticsearch.action.bulk.BackoffPolicy.exponentialBackoff;
import static org.elasticsearch.common.settings.Settings.EMPTY;
import static org.elasticsearch.common.unit.ByteSizeUnit.MB;
import static org.elasticsearch.common.unit.TimeValue.timeValueMillis;
import static org.elasticsearch.common.unit.TimeValue.timeValueSeconds;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class RNAIndexer {
    private static final char SEPARATOR_CHAR = ';';
    private static final int BUFFER_SIZE = 10000;
    public static final BulkProcessor.Listener BULK_PROCESSOR_ADAPTER = new BulkProcessor.Listener() {
        @Override
        public void beforeBulk(long executionId,
                               BulkRequest request) {
        }

        @Override
        public void afterBulk(long executionId,
                              BulkRequest request,
                              BulkResponse response) {
        }

        @Override
        public void afterBulk(long executionId,
                              BulkRequest request,
                              Throwable failure) {
        }
    };
    private CsvMapper csvMapper;
    private CsvSchema csvSchema;
    private Client client;


    public RNAIndexer(InetAddress inetAddress) throws UnknownHostException {
        this(new PreBuiltTransportClient(EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(inetAddress, 9300)));
    }

    public RNAIndexer(Client client) {
        this.client = client;
        csvMapper = (CsvMapper) new CsvMapper().disable(CAN_OVERRIDE_ACCESS_MODIFIERS);
        csvSchema = csvMapper
                .schemaFor(Association.class)
                .withColumnSeparator(SEPARATOR_CHAR);
    }

    public void convertToJson(Writer writer, InputStream source) throws IOException, URISyntaxException {
        MappingIterator<Association> datas = createObjectReader(csvSchema.withHeader())
                .readValues(new InputStreamReader(source, forName("latin1")));
        int i = 0, j = 0;
        try (SequenceWriter sequenceWriter = new ObjectMapper()
                .disable(CAN_OVERRIDE_ACCESS_MODIFIERS)
                .writer()
                .writeValues(writer)
                .init(false)) {
            while (datas.hasNext()) {
                Association association = datas.next();
                sequenceWriter.write(new Index(new Id(association.getCodeGestionnaire())));
                writer.write(format("%n"));
                sequenceWriter.write(association);
                writer.write(format("%n"));
                i++;
                if (i > BUFFER_SIZE) {
                    sequenceWriter.flush();
                    i = 0;
                }
                j++;
                if (j > 80 * BUFFER_SIZE) {
                    j = 0;
                }
            }
            sequenceWriter.flush();
        }
    }

    public void index(InputStream source) throws IOException {
        try (BulkProcessor bulkProcessor = BulkProcessor
                .builder(client, BULK_PROCESSOR_ADAPTER)
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(5, MB))
                .setFlushInterval(timeValueSeconds(5))
                .setConcurrentRequests(0)
                .setBackoffPolicy(exponentialBackoff(timeValueMillis(100), 3))
                .build()) {
            ObjectMapper associationMapper = new ObjectMapper()
                    .disable(CAN_OVERRIDE_ACCESS_MODIFIERS);
            MappingIterator<Association> datas = createObjectReader(csvSchema.withHeader())
                    .readValues(new InputStreamReader(source, forName("latin1")));
            while (datas.hasNext()) {
                Association association = datas.next();
                byte[] bytes = associationMapper.writeValueAsBytes(association);
                bulkProcessor.add(new IndexRequest("rna")
                        .type("association")
                        .id(association.getCodeGestionnaire())
                        .source(bytes, JSON));
            }
        }
    }

    @VisibleForTesting
    protected Association serializeOneLine(String s) throws IOException {
        return createObjectReader(csvSchema.withoutHeader()).readValue(s);
    }

    @VisibleForTesting
    protected int countColumns() {
        return csvSchema.size();
    }

    private ObjectReader createObjectReader(CsvSchema schema) {
        return csvMapper
                .readerFor(Association.class)
                .with(schema);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        File destination = File.createTempFile("rna", ".json");
        try (InputStream source = RNAIndexer.class.getResourceAsStream("rna_import_20170301_.csv")) {
            new RNAIndexer(getLocalHost()).index(source);
            out.printf("%s", destination.getAbsolutePath());
        }
    }
}
