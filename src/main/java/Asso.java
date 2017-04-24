import java.io.IOException;
import java.net.URISyntaxException;

public class Asso {
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args[0].equals("CreateIndex")) {
            CreateIndex.main(new String[]{});
        } else if (args[0].equals("RNAToJson")) {
            RNAToJson.main(new String[]{});
        }
    }
}
