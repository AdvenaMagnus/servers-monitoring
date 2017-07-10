package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Created by Grey on 23.03.2017.
 */

@Controller
public class SSEcontroller {

	@Autowired
	List<SseEmitter> emitters;

//	@Autowired
//	@Qualifier("sseListToUpdate")
//	List<SseEmitter> emittersUpdate;
//
//	@Autowired
//	@Qualifier("sseListToDelete")
//	List<SseEmitter> emittersDelete;
//
//	@Autowired
//	@Qualifier("sseListStatus")
//	List<SseEmitter> emittersStatus;
//
//	@Autowired
//	@Qualifier("sseListDetailInfo")
//	List<SseEmitter> emittersDetailInfo;

	@RequestMapping("/sse/servers")
	public ResponseBodyEmitter sseServers (@CookieValue("JSESSIONID") String submissionId) {
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
		emitters.add(emitter);
		return emitter;
	}

//	@RequestMapping("/sse/servers/update")
//	public ResponseBodyEmitter sseUpdate (@CookieValue("JSESSIONID") String submissionId) {
//		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
//		emittersUpdate.add(emitter);
//		return emitter;
//	}
//
//	@RequestMapping("/sse/servers/delete")
//	public ResponseBodyEmitter sseDelete (@CookieValue("JSESSIONID") String submissionId) {
//		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
//		emittersDelete.add(emitter);
//		return emitter;
//	}
//
//	@RequestMapping("/sse/servers/status")
//	public ResponseBodyEmitter sseServersStatus (@CookieValue("JSESSIONID") String submissionId) {
//		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
//		emittersStatus.add(emitter);
//		return emitter;
//	}
//
//	@RequestMapping("/sse/servers/detailinfo")
//	public ResponseBodyEmitter sseServersDetailInfo (@CookieValue("JSESSIONID") String submissionId) {
//		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
//		emittersDetailInfo.add(emitter);
//		return emitter;
//	}


}
