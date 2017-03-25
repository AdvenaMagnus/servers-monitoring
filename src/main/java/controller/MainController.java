package controller;

import core.server.Server;
import core.server.ServerDAO;
import core.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("server_dao_persist")
	ServerDAO serverDao;

	@RequestMapping(path = "/")
	public String index(){
		return "index";
	}

	@RequestMapping(path = "/servers/status/{serverId}")
	public ResponseEntity<SystemInfo> serverStatus(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(server !=null)
			return new ResponseEntity<SystemInfo>(server.getSystemInfo(), HttpStatus.OK);
		else return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(path = "/servers/ping/{serverId}")
	public ResponseEntity<Map<String,String>> serverPing(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(server !=null) {
			Map<String,String> result = new HashMap<String,String>();
			result.put("ping", serverDao.getPing(server));
			return new ResponseEntity<Map<String,String>>(result, HttpStatus.OK);
		}
		else
			return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(path = "/servers")
	public ResponseEntity<List<Server>> servers(){
		return new ResponseEntity(serverDao.allServers(), HttpStatus.OK);
	}

	@RequestMapping(path = "/servers/create", method = RequestMethod.PUT)
	public ResponseEntity<Server> newServer(@RequestBody Server server){
		Server serv = serverDao.createNew(server);
		if(serv!=null) return new ResponseEntity(serv, HttpStatus.OK);
		else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/servers/update", method = RequestMethod.POST)
	public ResponseEntity<Server> updateServer(@RequestBody Server server){
		Server serv = serverDao.update(server);
		if(serv!=null) return new ResponseEntity(serv, HttpStatus.OK);
		else return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/servers/delete/{serverId}", method = RequestMethod.DELETE)
	public ResponseEntity deleteServer(@PathVariable("serverId") long id) throws Exception {
		Server server = serverDao.serverById(id);
		if(serverDao.delete(server)) {
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

}
