import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONParse {
    private JSONObject jsonFile;
    private final String fileName = "resources/data.json";

    public JSONParse(){
        try {
            Object obj = new JSONParser().parse(new FileReader(this.fileName));
            this.jsonFile = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            System.out.println("Une erreur est parvenue lors de la lecture du fichier JSON.");
        }
    }

    public Iterator<String> parseArray(String nameArray) {
        JSONArray jsonArray = (JSONArray) this.jsonFile.get(nameArray);
        return (Iterator<String>) jsonArray.iterator();
    }
}
