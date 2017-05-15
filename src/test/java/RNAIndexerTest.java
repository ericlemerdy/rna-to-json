import data.Association;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;
import tool.ElasticsearchRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Calendar;

import static java.net.InetAddress.getLocalHost;
import static org.assertj.core.api.Assertions.assertThat;

public class RNAIndexerTest {

    @ClassRule
    public static DockerRule elasticsearch = ElasticsearchRule.ELASTICSEARCH;

    private AssociationsIndex associationsIndex;

    @Before
    public void setUp() throws IOException {
        associationsIndex = new AssociationsIndex(getLocalHost());
        associationsIndex.createIndex();
    }

    @Test
    public void should_read_one_import_association() throws IOException {
        Calendar calendar = Calendar.getInstance();

        Association a = new RNAIndexer((Client) null).serializeOneLine("" +
                "751P00085568;00085568;\"\";751P;1988-04-15;0001-01-01;D;S;INDIGO;dvpt et démocratisation de la musique;006030;000000;\"\";10        BD  Brune;\"\";75014;PARIS;75114;\"\";W751085568;R;\"\";2013-07-10 10:33:24.263074\n");
        assertThat(a.getCodeGestionnaire()).isEqualTo("751P00085568");
        assertThat(a.getAncienNumero()).isEqualTo("00085568");
        assertThat(a.getSiret()).isEmpty();
        assertThat(a.getCodePrefectureOuSousPrefectureDuBureauGestionnaire()).isEqualTo("751P");
        calendar.set(1988, 3, 15);
        assertThat(a.getDateDeCreation()).isInSameDayAs(calendar.getTime());
        calendar.set(1, 0, 1);
        assertThat(a.getDateDePublication()).isInSameDayAs(calendar.getTime());
        assertThat(a.getNature()).isEqualTo('D');
        assertThat(a.getGroupement()).isEqualTo('S');
        assertThat(a.getTitre()).isEqualTo("INDIGO");
        assertThat(a.getObjet()).isEqualTo("dvpt et démocratisation de la musique");
        assertThat(a.getObjetSocialWaldec1()).isEqualTo("006030");
        assertThat(a.getObjetSocialWaldec2()).isEqualTo("000000");
        assertThat(a.getAdresseLigne1()).isEmpty();
        assertThat(a.getAdresseLigne2()).isEqualTo("10        BD  Brune");
        assertThat(a.getAdresseLigne3()).isEmpty();
        assertThat(a.getCodePostal()).isEqualTo("75014");
        assertThat(a.getCommune()).isEqualTo("PARIS");
        assertThat(a.getCodeInsee()).isEqualTo("75114");
        assertThat(a.getSiteWeb()).isEmpty();
        assertThat(a.getObservation()).isEqualTo("W751085568");
        assertThat(a.getPosition()).isEqualTo("R");
        assertThat(a.getNumeroRUPMinistere()).isEmpty();
        calendar.set(2013, 6, 10);
        assertThat(a.getDateDeMiseAJour()).isInSameDayAs(calendar.getTime());
    }

    @Test
    public void an_association_should_have_23_columns() {
        int columns = Association.class.getDeclaredFields().length;

        assertThat(columns).isEqualTo(23);
    }

    @Test
    public void csv_should_have_23_columns() {
        int columns = new RNAIndexer((Client) null).countColumns();

        assertThat(columns).isEqualTo(23);
    }

    @Test
    public void should_migrate_a_file() throws IOException, URISyntaxException {
        try (InputStream source = this.getClass().getResourceAsStream("rna_import_3_lines.csv");
             StringWriter writer = new StringWriter()) {
            new RNAIndexer((Client) null).convertToJson(writer, source);

            assertThat(writer.toString()).isEqualTo("" +
                    "{\"index\":{\"_id\":\"751P00085568\"}}\n" +
                    " {" +
                    "\"codeGestionnaire\":\"751P00085568\"," +
                    "\"ancienNumero\":\"00085568\"," +
                    "\"siret\":\"\"," +
                    "\"codePrefectureOuSousPrefectureDuBureauGestionnaire\":\"751P\"," +
                    "\"dateDeCreation\":577065600000," +
                    "\"dateDePublication\":-62135769600000," +
                    "\"nature\":\"D\"," +
                    "\"groupement\":\"S\"," +
                    "\"titre\":\"INDIGO\"," +
                    "\"objet\":\"dvpt et démocratisation de la musique\"," +
                    "\"objetSocialWaldec1\":\"006030\"," +
                    "\"objetSocialWaldec2\":\"000000\"," +
                    "\"adresseLigne1\":\"\"," +
                    "\"adresseLigne2\":\"10        BD  Brune\"," +
                    "\"adresseLigne3\":\"\"," +
                    "\"codePostal\":\"75014\"," +
                    "\"commune\":\"PARIS\"," +
                    "\"codeInsee\":\"75114\"," +
                    "\"siteWeb\":\"\"," +
                    "\"observation\":\"W751085568\"," +
                    "\"position\":\"R\"," +
                    "\"numeroRUPMinistere\":\"\"," +
                    "\"dateDeMiseAJour\":1373452404000" +
                    "}\n" +
                    " {\"index\":{\"_id\":\"751P00184848\"}}\n" +
                    " {" +
                    "\"codeGestionnaire\":\"751P00184848\"," +
                    "\"ancienNumero\":\"00184848\"," +
                    "\"siret\":\"\"," +
                    "\"codePrefectureOuSousPrefectureDuBureauGestionnaire\":\"751P\"," +
                    "\"dateDeCreation\":1200614400000," +
                    "\"dateDePublication\":1203724800000," +
                    "\"nature\":\"D\"," +
                    "\"groupement\":\"S\"," +
                    "\"titre\":\"COMITE DE GESTION RUE PINEL\"," +
                    "\"objet\":\"la gestion des locaux et des services nécessaires aux buts des antennes et a l'exercice des activités de ses membres, conformément a la convention qui sera agree par l'association et l'association philosophique\"," +
                    "\"objetSocialWaldec1\":\"006000\"," +
                    "\"objetSocialWaldec2\":\"000000\"," +
                    "\"adresseLigne1\":\"\"," +
                    "\"adresseLigne2\":\"9         RUE Pinel\"," +
                    "\"adresseLigne3\":\"\"," +
                    "\"codePostal\":\"75013\"," +
                    "\"commune\":\"PARIS\"," +
                    "\"codeInsee\":\"75113\"," +
                    "\"siteWeb\":\"\"," +
                    "\"observation\":\"W751184848\"," +
                    "\"position\":\"R\"," +
                    "\"numeroRUPMinistere\":\"\"," +
                    "\"dateDeMiseAJour\":1373988617000" +
                    "}\n");
        }
    }

    @Test
    public void should_index_2_docs() throws IOException {
        try (InputStream source = this.getClass().getResourceAsStream("rna_import_3_lines.csv")) {
            new RNAIndexer(getLocalHost()).index(source);
        }

        associationsIndex.getClient().admin().indices().prepareRefresh().get();
        SearchResponse response = associationsIndex.getClient().prepareSearch().get();

        assertThat(response.getHits().getHits()).isNotEmpty();
    }

    @After
    public void tearDown() throws Exception {
        associationsIndex.deleteIndexIfExists();
    }
}
