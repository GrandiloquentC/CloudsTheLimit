import org.json.simple.parser.*;

import java.io.*;

public class PersistentStorage {
    String relativePath;

    public org.json.simple.JSONObject readFile() throws IOException, ParseException {
        
        JSONParser parser = new JSONParser();

        org.json.simple.JSONObject main = (org.json.simple.JSONObject) parser.parse(new FileReader(relativePath));
        
        return main;
    }

    public void writeToFile(org.json.JSONObject towrite) throws IOException {
        FileWriter writer = new FileWriter(new File(relativePath));
        writer.write(towrite.toString());
        writer.flush();
        writer.close();
    }

    public PersistentStorage(String _relativePath) {
        this.relativePath = _relativePath;
    }

    public static void main(String[] args) throws Exception {
        /*org.json.simple.JSONObject a = readFile();
        a.put("h", "t");
        writeToFile(new org.json.JSONObject(a.toJSONString()));*/

    }
}
