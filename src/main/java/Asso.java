import java.io.IOException;
import java.net.URISyntaxException;

public class Asso {
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args[0].equals("AssociationsIndex")) {
            AssociationsIndex.main(new String[]{});
        } else if (args[0].equals("RNAIndexer")) {
            RNAIndexer.main(new String[]{});
        }
    }
}
