package core.server.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Alexander on 30.03.2017.
 */
@Entity
public class ServerDetailInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String systemLogin;
	String systemPassword;
	String serverLogin;
	String serverPassword;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getSystemLogin() {
		return systemLogin;
	}
	public void setSystemLogin(String systemLogin) {
		this.systemLogin = systemLogin;
	}

	public String getSystemPassword() {
		return systemPassword;
	}
	public void setSystemPassword(String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public String getServerLogin() {
		return serverLogin;
	}
	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerPassword() {
		return serverPassword;
	}
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
}
