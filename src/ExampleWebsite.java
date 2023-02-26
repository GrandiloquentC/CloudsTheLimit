

import java.util.*;
import org.json.*;
import java.io.*;

public class ExampleWebsite  {
	//michaels ip: 192.168.68.93
	//port = 8000
	//static int site_users = 30;
	static int site_datas  = 3;
	static ArrayList<SiteUser> site_database = new ArrayList<SiteUser>();
	static ReallySecureDatabase AuthApp = new ReallySecureDatabase();/////////////////////////////////////////////////////
	static PersistentStorage store = new PersistentStorage("src/websitedatabase.json");
	
	
	public static void init() throws IOException, org.json.simple.parser.ParseException {
		JSONObject main = new JSONObject(store.readFile().toJSONString());
		JSONArray users = main.getJSONArray("Users");
		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			add_user(user.getString("name"), user.getString("email"), user.getString("pin"));
		}
	}

	public static void save() throws IOException {
		JSONArray users = new JSONArray();
		JSONObject topass = new JSONObject();
		for (SiteUser user : site_database) {
			JSONObject userobj = new JSONObject();
			userobj.put("name", user.name);
			userobj.put("email", user.email);
			userobj.put("pin", user.pin);
			users.put(userobj);
		}
		topass.put("Users", users);
		store.writeToFile(topass);
	}

	public static void main(String[] args) throws Exception {
		init();
		//for (int i = 0; i < site_users; i++) {
        //    site_database.get(i).name = "";
        //    site_database.get(i).pin = "";
        //    site_database.get(i).hashed_pin = "";
        //}
        
        /*add_user("Michael", "M@gmail.com","1111");
        add_user("Sichael","S@gmail.com" ,"3333");
        add_user("Yichael", "T@gmail.com","4444");
        add_user("Snichael","Sn@gmail.com" ,"5555");
        add_user("Crichael", "Cr@gmail.com","6666");
        add_user("Michael", "hacker@gmail.com","7777");
        add_user("Michaels brother", "M@gmail.com", "2904");*/
        
        System.out.println("The web database is: ");
        for (int i = 0; i < site_database.size(); i++) {
        	System.out.print("|  ");
            System.out.print(site_database.get(i).name);
            System.out.print("  |  ");
            System.out.print(site_database.get(i).pin);
            System.out.println("  |");
        }
        
        //System.out.println(retrieve_pass_from_name("Michael"));
        //System.out.println(retrieve_pass_from_email("D@gmail.com"));
        attempt_login("Michael", "wrong password", "M Camera");
        attempt_login("Michael", "1111", "M Camera 2");
        attempt_login("Yichael", "4444", "M Camera");
        
        for (int i = 0; i < AuthApp.my_database.size(); i++) {
        	AuthApp.my_database.get(i).printReadable();
        }
    }
	
	public static String retrieve_pass_from_email(String email) {
		for (int j = 0; j < site_database.size(); j++) {
			if (site_database.get(j).email.equals(email)) {
				return site_database.get(j).pin;
			}
		}
		return "Site Error: No such user";
	}
	
	public static String retrieve_pass_from_name(String name) {
		for (int j = 0; j < site_database.size(); j++) {
			if (site_database.get(j).name.equals(name)) {
				return site_database.get(j).pin;
			}
		}
		return "Site Error: No such user";
	}
	
	public static String retrieve_email(String user) {
		for (int j = 0; j < site_database.size(); j++) {
			if (site_database.get(j).name.equals(user)) {
				return site_database.get(j).email;
			}
		}
		return "Site Error: No such user";
	}
	
	public static void attempt_login (String name, String pin, String camera) {
		String email = retrieve_email(name);
		if (retrieve_pass_from_email(email).equals(pin)) {
			System.out.print("Site | Correct password! Moving on to 2FA. ");
			if (AuthApp.check(email, AuthApp.camera_view(camera))) {
				System.out.print("Correct face! Welcome, "); //////////////////////TO DO: ACTUALLY LOG IN
			} else {
				System.out.print("Wrong face, ");
			}
		} else {
			System.out.print("Site | Wrong password, ");
		}
		System.out.println(name);
	}

	public static void add_user(String new_name, String email, String pin) throws IOException{///////////////////////////////////////////////////////
		if (!new_name.equals("") ){
			System.out.print("Site | Attempting to add account \"");
			System.out.print(new_name);
			System.out.println("\"");
			boolean valid = true;
			for (int i = 0; i < site_database.size(); i++) {
				//if (site_database.get(i).name.equals(new_name)) {
				//	System.out.println("Site | Error: Name Already Exists");
				//	valid = false;
				//}
				if (site_database.get(i).email.equals(email)) {
					System.out.println("Site | Error: Email Already Exists");
					valid = false;
				}
				if (!valid) {
					System.out.print("Site | The account \"");
					System.out.print(new_name);
					System.out.println("\" could not be added.");
					return;
				}
			}
			try {
			AuthApp.add_user(email);
			site_database.add(new SiteUser(new_name, email, pin));
			} catch (AssertionError e) {
				System.out.println("Site | The account \"" + new_name + "\" could not be added due to difficulties verifying your email.");
			}
			save();
			////////////////////////////////////////////////////////////////////////////////
		} else {
			System.out.print("Site | Account name invalid");
		}
	}
	
	public static void update_user(String[] creds, String replace_name, String replace_pin) throws IOException { //creds = name, email, pin
		if (!replace_name.equals("") ){
			System.out.print("Site | Attempting to update account \"");
			System.out.println("\"");
			for (int i = 0; i < site_database.size(); i++) {
				if (site_database.get(i).info().equals(creds)) {
					site_database.get(i).name = replace_name;
					site_database.get(i).pin = replace_pin;
					//AuthApp.update_user(creds, replace_name);
				}
			}
			save();

		} else {
			System.out.print("Site | Account name invalid");
		}
	}
	
	public static String hash (String pin){
		return pin + "__thewebsite_hash__";
		
	}
	public static class SiteUser {
		String name;
		String pin;
		String email;
		String hashed_pin;

		public SiteUser(String _name, String _email, String _pin) {
			this.name = _name;
			this.email = _email;
			this.pin = _pin;
			this.hashed_pin = hash(_pin);
		}
		
		public String[] info() {
			return new String[] {this.name, this.email, this.hashed_pin};
		}
		
		public boolean check(String pwd) {
			return this.pin.equals(pwd);
		}
	}
	
}
