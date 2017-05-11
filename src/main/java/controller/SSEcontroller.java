package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Grey on 23.03.2017.
 */

@Controller
public class SSEcontroller {

	@Autowired
	SseEmitter sseEmitter;

	@Autowired
	@Qualifier("sseListToUpdate")
	List<SseEmitter> emittersUpdate;

	@Autowired
	@Qualifier("sseListToDelete")
	List<SseEmitter> emittersDelete;

	@Autowired
	@Qualifier("sseListStatus")
	List<SseEmitter> emittersStatus;

	@RequestMapping("/sse/servers/update")
	public ResponseBodyEmitter sseUpdate (@CookieValue("JSESSIONID") String submissionId) {
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
		emittersUpdate.add(emitter);
		return emitter;
	}

	@RequestMapping("/sse/servers/delete")
	public ResponseBodyEmitter sseDelete (@CookieValue("JSESSIONID") String submissionId) {
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
		emittersDelete.add(emitter);
		return emitter;
	}

	@RequestMapping("/sse/servers/status")
	public ResponseBodyEmitter sseServersStatus (@CookieValue("JSESSIONID") String submissionId) {
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
		emittersStatus.add(emitter);
		return emitter;
	}


}
