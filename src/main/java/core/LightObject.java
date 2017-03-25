package core;

/**
 * Created by Grey on 17.03.2017.
 */
public class LightObject {
	long id;
	String info;

	public LightObject(){
	}

	public LightObject(long id, String info){
		this.setId(id);
		this.setInfo(info);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
