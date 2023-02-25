// user class; stores data. split up data for more security, make accessing all data points easier.
public class User {
	String name;
	String pin;
	String face;
	String param;

	public User(String _name, String _pin, String _face, String _param) {
		this.name = _name;
		this.pin = _pin;
		this.face = _face;
		this.param = _param;
	}
}
