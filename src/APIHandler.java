import okhttp3.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.json.*;

public class APIHandler {
    
    final static String apiKeyDatabase = "7ccb94ab-bf8f-40f5-b841-49457a939a25";
    final static String apiKeyVerif = "594d6475-8cfa-4ed5-8b5c-26891507bb64";
    final static String webbase = "http://192.168.68.93:8000";
    final static OkHttpClient client = new OkHttpClient().newBuilder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

    /*
     * Adds an image to the database.
     * @param imagefile - File to add
     * @param subject - Identity of person images are being added to (will create if does not exist)
     */
    public static String addImage(File imagefile, String subject) throws IOException {

        // Payload: image
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file",imagefile.getAbsolutePath(),
            RequestBody.create(imagefile, MediaType.parse("application/octet-stream"))
            )
        .build();

        // actual request
        Request request = new Request.Builder()
        .url(webbase + "/api/v1/recognition/faces?subject=" + subject)
        .method("POST", body)
        .addHeader("Content-Type", "multipart/form-data")
        .addHeader("x-api-key", apiKeyDatabase)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return "Request failed with code " + Integer.toString(response.code()) + ".";
        }
        JSONObject json = new JSONObject(response.body().string());
        return json.get("image_id").toString();
    }

    public static boolean renameSubject(String subject, String updatedName) throws IOException {
        
            RequestBody body = RequestBody.create("{\"subject\": \"" + updatedName + "\"}", MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
            .url(webbase + "/api/v1/recognition/subjects/" + subject)
            .method("PUT", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-api-key", apiKeyDatabase)
            .build();
            
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return false;
            }
            JSONObject json = new JSONObject(response.body().string());
            return json.get("updated").toString() == "true";
    }

    /*
        * Verifies that the image is in fact an image of the subject.
        * @param _threshold Value needed to return success.
        * @param compare File object of the image.
        */
    public static boolean verify(File compare, String subject, Double _threshold) throws IOException {

        // request: fetches all image ids of target subject
        Request request = new Request.Builder()
        .url(webbase + "/api/v1/recognition/faces?subject=" + subject)
        .addHeader("x-api-key", apiKeyDatabase)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return false;
        }

        // extract all image ids from json
        JSONObject json = new JSONObject(response.body().string());
        JSONArray faces = (JSONArray) json.get("faces");
        String[] imageids = new String[json.getInt("total_elements")];
        for (int i = 0; i < json.getInt("total_elements"); i++) {
            imageids[i] = ((JSONObject) faces.get(i)).getString("image_id");
        }

        // iterate over image ids 
        for (String id : imageids) {
            try {
                // payload: image 
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",compare.getAbsolutePath(),
                    RequestBody.create(compare, MediaType.parse("application/octet-stream"))
                    )
                .build();

                request = new Request.Builder()
                .url(webbase + "/api/v1/recognition/faces/" + id + "/verify?face_plugins=pose")
                .method("POST", body)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("x-api-key", apiKeyDatabase)
                .build();

                response = client.newCall(request).execute();

                json = new JSONObject(response.body().string());

                // get similarity as double
                double similarity = ((JSONObject)((JSONArray)json.get("result")).get(0)).getDouble("similarity");

                // add code here to get pose for pose tracking

                if (!(similarity >= _threshold)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(verify(new File("src/assets/calvin.jpg"), "Calvin", 0.97));
            //System.out.println(addImage(new File("src/calvin.jpg"), "a"));
            //System.out.println(renameSubject("Calvin", "Alex"));
            /*File file = new File("C:\\Users\\overk\\OneDrive\\Documents\\GitHub\\CloudsTheLimit\\src\\img.jpg");

            OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

            MediaType mediaType = MediaType.parse("multipart/form-data");
            
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file","img.jpg",
                RequestBody.create(file, MediaType.parse("application/octet-stream"))
                )
            .build();

            Request request = new Request.Builder()
            .url("http://localhost:8000/api/v1/recognition/recognize?limit=0&det_prob_threshold=0.0&prediction_count=1&face_plugins=landmarks,gender,age,calculator,mask&status=true")
            .method("POST", body)
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("x-api-key", "3bc74a05-9733-4ef2-a1e2-2a5a26d6c7e3")
            .build();
            
            Response response = client.newCall(request).execute();

            File h = new File("src/img.jpg");
            System.out.println(h.getName());
            System.out.println(response.code());
            System.out.println(response.body().byteString());*/

    }
}
