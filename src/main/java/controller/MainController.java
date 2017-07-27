package controller;

import core.server.*;
import core.server.entities.Server;
import core.server.entities.ServerDetailInfo;
import core.server.entities.ServerStatusCached;
import core.server.status.StatusService;
import core.server.status.StatusServiceImplGEO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Grey on 17.03.2017.
 */

@Controller
public class MainController {

	@Autowired
	ServerDAO serverDao;

	@Autowired
	NotifyService notifyService;

	@Autowired
	StatusService statusDAO;

	@Autowired
	NetworkService pingService;

	public MainController(){}

	public MainController(StatusServiceImplGEO statusDAO){
		this.statusDAO = statusDAO;
	}

	public MainController(StatusServiceImplGEO statusDAO, ServerDAO serverDao){
		this.statusDAO = statusDAO;
		this.serverDao = serverDao;
	}

	public MainController(StatusServiceImplGEO statusDAO, ServerDAO serverDao, NotifyService notifyService){
		this.statusDAO = statusDAO;
		this.serverDao = serverDao;
		this.notifyService = notifyService;
	}

	@RequestMapping(path = "/")
	public String index(){
		return "index";
	}

	@RequestMapping(path = "/servers/status/{serverId}")
	public ResponseEntity<ServerStatusCached> serverStatus(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(server !=null){
			ServerStatusCached status = statusDAO.updateStatus(server);
			notifyService.notifyStatus(status);
			return new ResponseEntity(HttpStatus.OK);
		}
		else return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(path = "/servers/detailinfo/{serverId}")
	public ResponseEntity<ServerDetailInfo> serverDetailInfo(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(server !=null){
			//notifyService.notifyDetailInfo(server);
			return new ResponseEntity(notifyService.detailInfoMsg(server).get("msg"), HttpStatus.OK);
		}
		else return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(path = "/servers/ping/{serverId}")
	public ResponseEntity<Map<String,String>> serverPing(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(server !=null) {
			Map<String,String> result = new HashMap<String,String>();
			result.put("ping", pingService.getPingToServer(server));
			return new ResponseEntity<Map<String,String>>(result, HttpStatus.OK);
		}
		else
			return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(path = "/servers")
	public ResponseEntity<List<Server>> servers(){
		return new ResponseEntity(serverDao.allServers(), HttpStatus.OK);
	}

	@RequestMapping(path = "/servers/light")
	public ResponseEntity<List<LightServer>> serversLight(){
		List<LightServer> result = new ArrayList<>();
		serverDao.allServers().forEach(server -> result.add(server.getLightServer()));
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@RequestMapping(path = "/servers/create", method = RequestMethod.PUT)
	public ResponseEntity<Server> newServer(@RequestBody Server server){
		serverDao.saveOrUpdate(server);
		notifyService.notifyAboutUpdate(server);
		return new ResponseEntity(server, HttpStatus.OK);
	}

	@RequestMapping(path = "/servers/update", method = RequestMethod.POST)
	public ResponseEntity<Server> updateServer(@RequestBody Server server){
		serverDao.saveOrUpdate(server);
		notifyService.notifyAboutUpdate(server);
		return new ResponseEntity(server, HttpStatus.OK);
	}

	@RequestMapping(path = "/servers/delete/{serverId}", method = RequestMethod.DELETE)
	public ResponseEntity deleteServer(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(serverDao.delete(server)) {
			notifyService.notifyAboutDelete(server);
			return new ResponseEntity(HttpStatus.OK);
		}
		else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/currentdate")
	public ResponseEntity<HashMap<String, String>> currentDate(){
		HashMap<String, String> result = new HashMap<>();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		Date today = Calendar.getInstance().getTime();
		String reportDate = df.format(today);
		result.put("date", reportDate);
		return new ResponseEntity<HashMap<String, String>>(result, HttpStatus.OK);
	}

	@RequestMapping(path = "/statistics")
	public String getStatisticsPage(){
		return "statistics";
	}

}
