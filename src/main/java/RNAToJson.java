import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.annotations.VisibleForTesting;

import java.io.*;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.nio.charset.Charset.forName;

class RNAToJson {
    private static final char SEPARATOR_CHAR = ';';
    public static final int BUFFER_SIZE = 10000;
    private CsvMapper csvMapper;
    private CsvSchema csvSchema;

    RNAToJson() {
        csvMapper = new CsvMapper();
        csvSchema = csvMapper
                .schemaFor(Association.class)
                .withColumnSeparator(SEPARATOR_CHAR);
    }

    void convertToJson(Writer writer, InputStream source) throws IOException, URISyntaxException {
        MappingIterator<Association> datas = createObjectReader(csvSchema.withHeader())
                .readValues(new InputStreamReader(source, forName("latin1")));
        int i = 0, j = 0;
        try (SequenceWriter sequenceWriter = new ObjectMapper()
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
        InputStream source = RNAToJson.class.getResourceAsStream("rna_import_20170301_.csv");
        new RNAToJson().convertToJson(new FileWriter(destination), source);
        out.printf("%s", destination.getAbsolutePath());
    }
}
