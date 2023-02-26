public class ReallySecureDatabase {
	//michaels ip: 192.168.68.93
	// port = 8000
	static int users = 30;
	static int datas  = 4;
	static String[][] my_database = new String[users][datas];
	
	public static void main(String[] args) {
		for (int i = 0; i < users; i++) {
            for (int j = 0; j < datas; j++) {
                my_database[i][j] = "";
            }
        }
        
        add_user("Michael", "1111", "M Face", "Random");
        add_user("Dichael", "2222", "D Face", "Random2");
        add_user("Sichael", "3333", "S Face", "Random3");
        add_user("Yichael", "4444", "Y Face", "Random4");
        add_user("Snichael", "5555", "Sn Face", "Random5");
        add_user("Crichael", "6666", "Cr Face", "Random6");
        add_user("Michael", "1111", "M Face", "Random7");
        
        System.out.println("The database is: ");
        for (int i = 0; i < users; i++) {
        	System.out.print("|  ");
            for (int j = 0; j < datas; j++) {
                System.out.print(my_database[i][j] + "  |  ");
            }
            System.out.println();
        }
        
        System.out.println(retrieve_face("1111"));
        System.out.println(retrieve_face("1112"));
        System.out.println(check("1111", "M Face"));
        System.out.println(check("1111", "Y Face"));
        
    }
	
	public static String retrieve_face(String unhashed_pin) {
		for (int j = 0; j < users; j++) {
			if (my_database[j][1].equals(hash(unhashed_pin))) {
				return my_database[j][2];
			}
		}
		return "";
	}
	
	public static boolean check(String pin, String face) {
		return retrieve_face(pin) == face;
	}
	
	public static void add_user(String name, String pin, String face, String param) {
		System.out.print("Attempting to add account \"");
		System.out.print(name);
		System.out.println("\"");
		boolean valid = true;
		for (int i = 0; i < users; i++) {
			if (my_database[i][0].equals(name)) {
				System.out.println("Error: Name Already Exists");
				valid = false;
			}
			if (my_database[i][1].equals(hash(pin))) {
				System.out.println("Error: PIN Already Exists");
				valid = false;
			}
			if (my_database[i][2].equals(face)) {
				System.out.println("Error: Face Already Exists");
				valid = false;
			}
			if (my_database[i][3].equals(param)) {
				System.out.println("Error: Random 4th param Already Exists");
				valid = false;
			} 
			if (!valid) {
				System.out.print("The account \"");
				System.out.print(name);
				System.out.println("\" could not be added.");
				return;
			} else if (my_database[i][0] == "") {
				my_database[i] = new String[] {name, hash(pin), face, param};
				System.out.print("Account \"");
				System.out.print(name);
				System.out.println("\" added successfully!");
				return;
			}
		}
	}
	
	public static String hash (String pin){
		return pin + "__hash__";
		
	}
}
