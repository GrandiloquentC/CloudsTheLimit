import org.json.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;

public class PersistentStorage {
    final static String relativePath = "src/database.json";

    public static org.json.simple.JSONObject readFile() throws IOException, ParseException {
        
        JSONParser parser = new JSONParser();

        org.json.simple.JSONObject main = (org.json.simple.JSONObject) parser.parse(new FileReader(relativePath));
        
        return main;
    }

    public static void writeToFile(org.json.JSONObject towrite) throws IOException {
        FileWriter writer = new FileWriter(new File(relativePath));
        writer.write(towrite.toString());
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        org.json.simple.JSONObject a = readFile();
        a.put("h", "t");
        writeToFile(new org.json.JSONObject(a.toJSONString()));

    }
}
