import okhttp3.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.json.*;

import java.util.*;

public class APIHandler {
    
    final static String apiKeyDatabase = "7ccb94ab-bf8f-40f5-b841-49457a939a25";
    final static String apiKeyVerif = "594d6475-8cfa-4ed5-8b5c-26891507bb64";
    final static String apiKeyDetection = "e5be4317-5c39-4e6e-9e09-b7b83d2c35f6";
    final static String webbase = "http://192.168.68.93:8000";
    final static OkHttpClient client = new OkHttpClient().newBuilder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

    double threshold;

    public APIHandler(double _threshold) {
        this.threshold = _threshold;
    }

    // subject modifications
    /*
     * Adds an image to the database.
     * @param imagefile - File to add
     * @param subject - Identity of person images are being added to (will create if does not exist)
     */
    public String addImage(File imagefile, String subject) throws IOException {

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

    public boolean renameSubject(String subject, String updatedName) throws IOException {
        
        if (!assertSubjectExists(subject)) {
            return false;
        }

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
        return json.get("updated").toString().equals("true");
    }

    public boolean removeSubject(String subject) throws IOException {

        if (!assertSubjectExists(subject)) {
            return true;
        }

        RequestBody body = RequestBody.create("{\"subject\": \"" + subject + "\"}", MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
        .url(webbase + "/api/v1/recognition/subjects/" + subject)
        .method("DELETE", body)
        .addHeader("Content-Type", "application/json")
        .addHeader("x-api-key", apiKeyDatabase)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return false;
        }
        JSONObject json = new JSONObject(response.body().string());
        return json.get("subject").toString().equals(subject);
    }

    public boolean removeAllPhotosOfSubject(String subject) throws IOException {
        if (!assertSubjectExists(subject)) {
            return true;
        }

        RequestBody body = RequestBody.create("{\"subject\": \"" + subject + "\"}", MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
        .url(webbase + "/api/v1/recognition/faces?subject=" + subject)
        .method("DELETE", body)
        .addHeader("x-api-key", apiKeyDatabase)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return false;
        }
        JSONObject json = new JSONObject(response.body().string());
        return json.getInt("deleted") == 1;
    }

    // this could be a binary search if it really needed to be
    public boolean assertSubjectExists(String subject) throws IOException {

        Request request = new Request.Builder()
        .url(webbase + "/api/v1/recognition/subjects")
        .addHeader("Content-Type", "application/json")
        .addHeader("x-api-key", apiKeyDatabase)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            return false;
        }

        JSONObject json = new JSONObject(response.body().string());
        JSONArray subjects = (JSONArray) json.get("subjects");
        for (int i = 0; i < subjects.length(); i++) {
            if (subjects.get(i).toString().equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }


    // verification
    /*
        * Verifies that the image is in fact an image of the subject.
        * @param _threshold Value needed to return success.
        * @param compare File object of the image.
        */
    public boolean verify(File compare, String subject) throws IOException {
        if (!assertSubjectExists(subject)) {
            System.out.println("Subject does not exist.");
            return false;
        }

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
        int facecount = json.getInt("total_elements");
        String[] imageids = new String[facecount];
        for (int i = 0; i < facecount; i++) {
            imageids[i] = ((JSONObject) faces.get(i)).getString("image_id");
        }

        // iterate over image ids 
        double average = 0;
        int testnum = 1;
        System.out.println();
        for (String id : imageids) {
            try {
                // payload: image 
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",compare.getAbsolutePath(),
                    RequestBody.create(compare, MediaType.parse("application/octet-stream"))
                    )
                .build();

                // request to verify compare matches with target image
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

                System.out.print("Test " + testnum + ": " + similarity);
                if (similarity >= this.threshold) {
                    System.out.println(" (passed)");
                } else {
                    System.out.println(" (failed)");
                }
                // confirm similarity passes threshold
                average += similarity;
                testnum++;
            } catch (Exception e) {
                return false;
            }
        }
        average /= facecount;
        System.out.print("Average Similarity: " + average);

        if (average < this.threshold) {
            System.out.println(" (failed)");
            return false;
        }
        System.out.println(" (passed)");

        // if similarity passed threshold for all image ids, return true as all checks have succeeded
        return true;
    }

    public boolean verifyWithPose(File compare, String subject, ArrayList<PoseTracking.Pose> poses) throws IOException {
        if (!assertSubjectExists(subject)) {
            System.out.println("Subject does not exist.");
            return false;
        }

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
        int facecount = json.getInt("total_elements");
        String[] imageids = new String[facecount];
        for (int i = 0; i < facecount; i++) {
            imageids[i] = ((JSONObject) faces.get(i)).getString("image_id");
        }

        // iterate over image ids 
        double average = 0;
        int testnum = 1;
        boolean first = true;
        System.out.println();
        for (String id : imageids) {
            try {
                // payload: image 
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",compare.getAbsolutePath(),
                    RequestBody.create(compare, MediaType.parse("application/octet-stream"))
                    )
                .build();

                // request to verify compare matches with target image
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
                if (first) {
                    poses.add(new PoseTracking.Pose(((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("roll"), 
                    ((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("pitch"), 
                    ((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("yaw")));
                first = false;
                }

                System.out.print("Test " + testnum + ": " + similarity);
                if (similarity >= this.threshold) {
                    System.out.println(" (passed)");
                } else {
                    System.out.println(" (failed)");
                }
                // confirm similarity passes threshold
                average += similarity;
                testnum++;
            } catch (Exception e) {
                return false;
            }
        }
        average /= facecount;
        System.out.print("Average Similarity: " + average);

        if (average < this.threshold) {
            System.out.println(" (failed)");
            return false;
        }
        System.out.println(" (passed)");

        // if similarity passed threshold for all image ids, return true as all checks have succeeded
        return true;
    }

    // detection
    public PoseTracking.Pose getPose(File imagefile) throws IOException {

        // Payload: image
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file",imagefile.getAbsolutePath(),
            RequestBody.create(imagefile, MediaType.parse("application/octet-stream"))
            )
        .build();

        // actual request
        Request request = new Request.Builder()
        .url(webbase + "/api/v1/detection/detect?face_plugins=pose")
        .method("POST", body)
        .addHeader("Content-Type", "multipart/form-data")
        .addHeader("x-api-key", apiKeyDetection)
        .build();
        
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            System.out.println(response.code());
            return new PoseTracking.Pose(1/0, 1/0, 1/0);
        }
        JSONObject json = new JSONObject(response.body().string());
        //System.out.println(json.toString());
        return new PoseTracking.Pose(((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("roll"), 
        ((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("pitch"), 
        ((JSONObject)((JSONArray)json.get("result")).get(0)).getJSONObject("pose").getDouble("yaw"));
    }

    public static void main(String[] args) throws Exception {

    }
}
