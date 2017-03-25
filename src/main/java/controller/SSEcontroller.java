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

	@RequestMapping("/sseUpdate")
	public ResponseBodyEmitter sseUpdate (@CookieValue("JSESSIONID") String submissionId) {
		//SseEmitter emitter = sseEmitter;
		//SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
//		ExecutorService service = Executors.newSingleThreadExecutor();
//		service.execute(() -> {
//			for (int i = 0; i < 500; i++) {
//				try {
//					emitter.send(LocalTime.now().toString() + " i: " +i  , MediaType.TEXT_PLAIN);
//
//					Thread.sleep(500);
//				} catch (Exception e) {
//					e.printStackTrace();
//					emitter.completeWithError(e);
//					return;
//				}
//			}
//			emitter.complete();
//		});
//		try {
//			emitter.send(LocalTime.now().toString()  , MediaType.TEXT_PLAIN);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		emittersUpdate.add(emitter);
		return emitter;
	}

	@RequestMapping("/sseDelete")
	public ResponseBodyEmitter sseDelete (@CookieValue("JSESSIONID") String submissionId) {
		SseEmitter  emitter = new SseEmitter (Long.MAX_VALUE);
		emittersDelete.add(emitter);
		return emitter;
	}


}
