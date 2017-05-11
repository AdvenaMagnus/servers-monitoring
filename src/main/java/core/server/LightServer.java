package core.server;

/**
 * Created by Alexander on 30.03.2017.
 */
public class LightServer {

	long id;
	String name;
	String ip;

	public LightServer(){
	}

	public LightServer(Server server){
		this.setId(server.getId());
		this.setName(server.getName());
		this.setIp(server.getIp());
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
