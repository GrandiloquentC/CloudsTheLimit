import okhttp3.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
public class App {
    /*
    public static void main2(String[] args) throws Exception {
            File file = new File("C:\\Users\\overk\\OneDrive\\Documents\\GitHub\\CloudsTheLimit\\Api Access\\API Access\\src\\img.jpg");
            HttpResponse<String> response = Unirest.post("http://localhost:8000/api/v1/recognition/recognize?face_plugins=landmarks,gender,age")
            .header("Content-Type", "multipart/form-data")
            .header("x-api-key", "3bc74a05-9733-4ef2-a1e2-2a5a26d6c7e3")
            .field("file", file)
            .asString();

            File h = new File("src/img.jpg");
            System.out.println(h.getName());

            System.out.println(response.getStatus());
            System.out.println(response.getBody());
    }*/

    public static void main(String[] args) throws Exception {
            File file = new File("C:\\Users\\overk\\OneDrive\\Documents\\GitHub\\CloudsTheLimit\\src\\img.jpg");

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

            /*HttpResponse<String> response = Unirest.get("http://localhost:8000/api/v1/recognition/subjects")
            .header("Content-Type", "application/json")
            .header("x-api-key", "3bc74a05-9733-4ef2-a1e2-2a5a26d6c7e3")
            .asString();*/

            //Unirest.setTimeouts(0, 0);
            /*HttpResponse<String> response = Unirest.post("http://localhost:8000/api/v1/recognition/subjects")
            .header("Content-Type", "application/json")
            .header("x-api-key", "3bc74a05-9733-4ef2-a1e2-2a5a26d6c7e3")
            .body("{\r\n    \"subject\": \"preston\"\r\n}")
            .asString();*/
            System.out.println(response.code());
            System.out.println(response.body().byteString());

    }
}
