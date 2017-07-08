package core.enums;

/**
 * Created by Alexander on 04.07.2017.
 */
public enum DowntimeReason {
	noInternet("Нет сети"),
	inService("На обслуживании");

	String name;

	DowntimeReason(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
