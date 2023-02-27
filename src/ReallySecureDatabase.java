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
			ArrayList<PoseTracking.Pose> poses = new ArrayList<>();
			JSONArray posesobj = user.getJSONArray("poses");
			for (int j = 0; j < 5; j++) {
				poses.add(new PoseTracking.Pose(posesobj.getJSONObject(j).getDouble("roll"), posesobj.getJSONObject(j).getDouble("pitch"), 
				posesobj.getJSONObject(j).getDouble("yaw")));
			}
			my_database.add(new User(user.getString("email"), poses));
		}
	}

	public static void save() {
		try {
			JSONArray users = new JSONArray();
			JSONObject topass = new JSONObject();
			for (User user : my_database) {
				JSONObject userobj = new JSONObject();
				userobj.put("email", user.email);
				JSONArray poses = new JSONArray();
				for (int i = 0; i < 5; i++) {
					JSONObject pose = new JSONObject();
					pose.put("roll", user.pose_sequence.get(i).roll);
					pose.put("pitch", user.pose_sequence.get(i).pitch);
					pose.put("yaw", user.pose_sequence.get(i).yaw);
					poses.put(pose);
				}
				userobj.put("poses", poses);
				users.put(userobj);
			}
			topass.put("Users", users);
			store.writeToFile(topass);
		} catch (IOException e) {
			System.out.println("Filepath " + store.relativePath + " is invalid.");
		}
	}

	
	public static void main(String[] args) throws Exception {
		init();
		verify("Alex");
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
		if (camera.equals("hacker@gmail.com")) {
			return new File("src/assets/robert.jpg");
		}
		return new File("src/assets/calvin.jpg");
	}
	
	public static User retrieveUserByEmail(String email) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).email.equals(email)) {
				return my_database.get(j);
			}
		}
		return new User("null", new ArrayList<>());
	}
	public static void add_user(String email) throws AssertionError, IOException {
		System.out.print("App | Attempting to add account \"");
		System.out.print(email);
		System.out.println("\"");
		//boolean valid = true;
		if (!assertOwnsEmail(email)) {
			System.out.println("Email ownership could not be validated.");
			throw new AssertionError("Email ownership could not be validated.");
		}

		if (!appAPI.assertSubjectExists(email)) {
			my_database.add(new User(email, fetch_poses(email)));
			save();
		}
	}

	public static boolean verify(String subject) {
		final int points = 5;
		final double threshold = 30;
		final long delay = 200;
		ArrayList<PoseTracking.Pose> poses = new ArrayList<>();
		for (int i = 0; i < points; i++) {
			try {
				if (!appAPI.verifyWithPose(camera_view(subject), subject, poses)) {
					return false;
				}
				Thread.sleep(delay);
			} catch (Exception e) {}
		}
		return PoseTracking.compare(retrieveUserByEmail(subject).pose_sequence, poses, threshold);
	}

	public static ArrayList<PoseTracking.Pose> fetch_poses(String subject) {
		final int points = 5;
		final long delay = 200;
		ArrayList<PoseTracking.Pose> poses = new ArrayList<>();
		for (int i = 0; i < points; i++) {
			try {
				poses.add(appAPI.getPose(camera_view(subject)));
				appAPI.addImage(camera_view(subject), subject);
				Thread.sleep(delay);
			} catch (Exception e) {}
		}
		return poses;
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
	
	public static boolean check(String email) {
		return verify(email);
	}
/*	public static String hash (String pin, String email){
		return pin + "__hash__" + email;
		
	}
*/	
	public static class User {
		String email;
		ArrayList<PoseTracking.Pose> pose_sequence;

		public User(String _email, ArrayList<PoseTracking.Pose> _pose_sequence) {
			this.email = _email;
			this.pose_sequence = _pose_sequence;
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

	

	
}
