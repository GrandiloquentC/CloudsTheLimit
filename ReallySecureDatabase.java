package hackathoncode;

import java.util.*;

public class ReallySecureDatabase {
	//michaels ip: 192.168.68.93
	// port = 8000
	//static int my_database.size() = 30;
	//static int datas  = 4;
	static ArrayList<User> my_database = new ArrayList<User>();
	
	public static void main(String[] args) {

    }
	
	public static String retrieve_face(String email) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).email.equals(email)) {
				return my_database.get(j).face_name;
			}
		}
		return "App | Error: No such Email";
	}
	
	public static String camera_view(String camera) {
		//User user = retrieveUserByEmail(email);
		return camera.charAt(0) + " Face"; ////////////////////////////////////////////TO DO: Actually get the user's face
	}
	
	public static User retrieveUserByEmail(String email) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).email.equals(email)) {
				return my_database.get(j);
			}
		}
		return new User("null", "null");
	}
	public static void add_user(String email, String camera) {
		System.out.print("App | Attempting to add account \"");
		System.out.print(email);
		System.out.println("\"");
		//boolean valid = true;
		for (int i = 0; i < my_database.size(); i++) {
			if (my_database.get(i).email.equals(email)) {
				System.out.println("App | Error: Email Already Exists");////////////////////////////////// confirm same person
				//valid = false;
				System.out.print("App | The account \"");
				System.out.print(email);
				System.out.println("\" could not be added.");
				return;
			}
		}
		
		
		my_database.add(new User(email, camera_view(camera)));
		
	}
	
	public static void add_face() {
		
	}
	public static void reset_faces() {
		
	}
	
	public static boolean check(String email, String face) {
		return retrieve_face(email).equals(face);
	}
/*	public static String hash (String pin, String email){
		return pin + "__hash__" + email;
		
	}
*/	
	public static class User {
		String email;
		String face_name;

		public User(String _email, String _face) {
			this.email = _email;
			this.face_name = _face;
		}
		
		//public boolean check(String pwd) {
		//	return this.pin.equals(pwd);
		//}
		
		public void reset() {
			this.email = "";
			this.face_name = "";
		}

		public void printReadable() {
			System.out.print("|  ");
			System.out.print(email);
			System.out.print("  |  ");
			System.out.print(face_name);
			System.out.println("  |");
		}
	}

	
}
