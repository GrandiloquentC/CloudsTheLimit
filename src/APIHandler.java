import okhttp3.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.json.*;

public class APIHandler {
    
    final static String apiKeyDatabase = "7ccb94ab-bf8f-40f5-b841-49457a939a25";
    final static String apiKeyVerif = "594d6475-8cfa-4ed5-8b5c-26891507bb64";
    final static String webbase = "http://192.168.68.93:8000";



    public static String addImage(File imagefile, String subject) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file","img.jpg",
                RequestBody.create(imagefile, MediaType.parse("application/octet-stream"))
                )
            .build();

            Request request = new Request.Builder()
            .url(webbase + "/api/v1/recognition/faces?subject=" + subject)
            .method("POST", body)
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("x-api-key", apiKeyDatabase)
            .build();
            
            Response response = client.newCall(request).execute();
            //return response.message();
            JSONObject json = new JSONObject(response.body().string());
            return json.get("image_id").toString();
    }
    public static void main(String[] args) throws Exception {
            System.out.println(addImage(new File("src/calvin.jpg"), "Calvin"));
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
