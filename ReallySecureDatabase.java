package hackathoncode;
import java.sql.DatabaseMetaData;
import java.util.*;

// change vars to non-static for actual implementation
public class ReallySecureDatabase {
	//michaels ip: 192.168.68.93
	// port = 8000
	static int users = 30;
	static int datas  = 4;
	static ArrayList<User> my_database = new ArrayList<>();
	
	public static void main(String[] args) {
        
        add_user("Michael", "1111", "M Face", "Random");
        add_user("Dichael", "2222", "D Face", "Random2");
        add_user("Sichael", "3333", "S Face", "Random3");
        add_user("Yichael", "4444", "Y Face", "Random4");
        add_user("Snichael", "5555", "Sn Face", "Random5");
        add_user("Crichael", "6666", "Cr Face", "Random6");
        add_user("Michael", "1111", "M Face", "Random7");
        
        System.out.println(retrieve_face("1111"));
        System.out.println(retrieve_face("1112"));
        System.out.println(check("1111", "M Face"));
        System.out.println(check("1111", "Y Face"));
        
    }
	
	/*
	 * Fetch function: unhashed pin -> face
	 */
	public static String retrieve_face(String unhashed_pin) {
		for (int j = 0; j < my_database.size(); j++) {
			if (my_database.get(j).pin.equals(hash(unhashed_pin))) {
				return my_database.get(j).face;
			}
		}
		return "";
	}
	
	/*
	 * Asserts unhashed pin matches face
	 */
	public static boolean check(String pin, String face) {
		return retrieve_face(pin) == face;
	}
	
	public static void add_user(String name, String pin, String face, String param) {
		System.out.print("Attempting to add account \"");
		System.out.print(name);
		System.out.println("\"");
		boolean valid = true;
		for (int i = 0; i < my_database.size(); i++) {
			if (my_database.get(i).name.equals(name)) {
				System.out.println("Error: Name Already Exists");
				valid = false;
			}
			if (my_database.get(i).pin.equals(hash(pin))) {
				System.out.println("Error: PIN Already Exists");
				valid = false;
			}
			if (my_database.get(i).face.equals(face)) {
				System.out.println("Error: Face Already Exists");
				valid = false;
			}
			if (my_database.get(i).param.equals(param)) {
				System.out.println("Error: Random 4th param Already Exists");
				valid = false;
			} 
			if (!valid) {
				System.out.print("The account \"");
				System.out.print(name);
				System.out.println("\" could not be added.");
				return;
			}
		}
		my_database.add(new User(name, pin, face, param));
		System.out.print("Account \"");
		System.out.print(name);
		System.out.println("\" added successfully!");
		return;
	}
	
	/*
	 * Filler hash function. Replace with SHA256 later.
	 */
	public static String hash(String pin){
		return pin + "__hash__";
		
	}

	/*
	 * Filler assertion function. Replace with API request to proper servers later.
	 * @param image - replace with actual image object or something later.
	 */
	public static boolean assertIsFace(String face, String image) {
		return face == image;
	}
}

