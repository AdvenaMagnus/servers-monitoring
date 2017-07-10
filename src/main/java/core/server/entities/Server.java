package core.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.LightObject;
import core.server.LightServer;
import org.hibernate.annotations.Cascade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Grey on 17.03.2017.
 */
@Entity
@Repository
@Transactional
public class Server {

	public Server(){
	}
	public Server(String name, String ip){
		this.setName(name);
		this.setIp(ip);
	}

	public Server(long id, String name, String ip){
		this.setId(id);
		this.setName(name);
		this.setIp(ip);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	String name;
	String ip;
	String notices;

	@OneToOne
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	ServerDetailInfo detailInfo;

	boolean inService=false;

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
//	@Cascade({org.hibernate.annotations.CascadeType.ALL})
//	Set<ServerStatusCached> statuses;

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "server")
//	@Cascade({org.hibernate.annotations.CascadeType.ALL})
//	Set<OnMaintenanceStatus> maintanceStatuses;

	//ServerStatusCached serverStatusCached;
	//ServerStatusCached status = ServerStatusCached.ok;


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

	public String getNotices() {
		return notices;
	}
	public void setNotices(String notices) {
		this.notices = notices;
	}

	//@JsonIgnore
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public ServerDetailInfo getDetailInfo() {
		return detailInfo;
	}
	public void setDetailInfo(ServerDetailInfo detailInfo) {
		this.detailInfo = detailInfo;
	}
//
//	@JsonIgnore
//	public Set<ServerStatusCached> getStatuses() {
//		return statuses;
//	}
//	public void setStatuses(Set<ServerStatusCached> statuses) {
//		this.statuses = statuses;
//	}

//	@JsonIgnore
//	public Set<OnMaintenanceStatus> getMaintanceStatuses() {
//		return maintanceStatuses;
//	}
//	public void setMaintanceStatuses(Set<OnMaintenanceStatus> maintanceStatuses) {
//		this.maintanceStatuses = maintanceStatuses;
//	}

	//	public ServerStatusCached getServerStatusCached() {
//		return serverStatusCached;
//	}
//	public void setServerStatusCached(ServerStatusCached serverStatusCached) {
//		this.serverStatusCached = serverStatusCached;
//	}


	public boolean getInService() {
		return inService;
	}
	public void setInService(boolean inService) {
		this.inService = inService;
	}

	@JsonIgnore
	@Transient
	public LightObject getLight(){
		return new LightObject(this.id, this.name+" "+this.ip);
	}

	@JsonIgnore
	@Transient
	public LightServer getLightServer(){
		return new LightServer(this);
	}

	@JsonIgnore
	@Transient
	public String getUrl(){
		if(!this.getIp().isEmpty()) return "http://"+ this.getIp();
		else return "";
	}

}
