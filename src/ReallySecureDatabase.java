import java.util.*;
import java.io.*;
import org.json.*;
public class ReallySecureDatabase {
	//michaels ip: 192.168.68.93
	// port = 8000
	//static int my_database.size() = 30;
	//static int datas  = 4;
	static ArrayList<User> my_database = new ArrayList<User>();
	final static PersistentStorage store = new PersistentStorage("src/appdatabase.json");
	final static APIHandler appAPI = new APIHandler(0.97);

	public static void init() throws IOException, org.json.simple.parser.ParseException {
		JSONObject main = new JSONObject(store.readFile().toJSONString());
		JSONArray users = main.getJSONArray("Users");
		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			add_user(user.getString("email"));
		}
	}

	public static void save() {
		try {
			JSONArray users = new JSONArray();
			JSONObject topass = new JSONObject();
			for (User user : my_database) {
				JSONObject userobj = new JSONObject();
				userobj.put("email", user.email);
				users.put(userobj);
			}
			topass.put("Users", users);
			store.writeToFile(topass);
		} catch (IOException e) {
			System.out.println("Filepath " + store.relativePath + " is invalid.");
		}
	}

	
	public static void main(String[] args) {
		add_user("Alex");
    }
	
	/*public static String retrieve_face(String email) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).email.equals(email)) {
				// add function 
			}
		}
		return "App | Error: No such Email";
	}*/
	
	public static File camera_view(String camera) {
		// DUMMY FUNCTION have this return a file and use it to take an image and stuff
		return new File("src/assets/calvin.jpg");
	}
	
	public static User retrieveUserByEmail(String email) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).email.equals(email)) {
				return my_database.get(j);
			}
		}
		return new User("null");
	}
	public static void add_user(String email) throws AssertionError {
		System.out.print("App | Attempting to add account \"");
		System.out.print(email);
		System.out.println("\"");
		//boolean valid = true;
		if (!assertOwnsEmail(email)) {
			System.out.println("Email ownership could not be validated.");
			throw new AssertionError("Email ownership could not be validated.");
		}
		
		// add POSE functions here when available
		try {
			appAPI.addImage(camera_view("camera"), email);
		} catch (IOException e) {
			System.out.println("Image could not be found.");
		} 
		my_database.add(new User(email));
		save();
	}

	public static boolean assertOwnsEmail(String email) {
		// dummy function
		return true;
	}
	
	public static void add_face(File face, String subject) {
		try {
			appAPI.addImage(face, subject);
		} catch (IOException e) {
			System.out.println("Image could not be found.");
		} 
	}
	public static void reset_faces(String email) {
		try {
			appAPI.removeAllPhotosOfSubject(email);
		} catch (IOException e) {
			System.out.println("Image could not be found.");
		} 
	}
	
	public static boolean check(String email, File face) {
		try {
			return appAPI.verify(face, email);
		} catch (IOException e) {
			System.out.println("Image could not be found.");
			return false;
		} 
	}
/*	public static String hash (String pin, String email){
		return pin + "__hash__" + email;
		
	}
*/	
	public static class User {
		String email;

		public User(String _email) {
			this.email = _email;
		}
		
		//public boolean check(String pwd) {
		//	return this.pin.equals(pwd);
		//}
		
		public void reset() {
			this.email = "";
		}

		public void printReadable() {
			System.out.print("|  ");
			System.out.print(email);
			System.out.print("  |  ");
		}
	}

	public static class Pose {
		double roll;
		double pitch;
		double raw;

		public Pose(double _roll, double _pitch, double _raw) {
			this.roll = _roll;
			this.pitch = _pitch;
			this.raw = _raw;
		}
	}

	
}
