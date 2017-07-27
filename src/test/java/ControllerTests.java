import controller.MainController;
import controller.NotifyService;
import core.server.ServerDAO;
import core.server.status.StatusServiceImplGEO;
import core.server.entities.Server;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alexander on 01.07.2017.
 */
public class ControllerTests {

    private MockMvc mockMvc;

    MainController localController;
    Server server;
    ServerStatusCached status;

    @Before
    public void init() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DateUtils.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        String toParse = "14:34 26.10.2014";
        Date date = df.parse(toParse);

        server = new Server();
        server.setId(10);
        //serverDAO.saveOrUpdate(server);

        status = new ServerStatusCached();
        status.setId(1);
        status.setDate(date);
        status.setOwner(server);
        //statusService.saveOrUpdate(status);

        StatusServiceImplGEO statusDAOMock = mock(StatusServiceImplGEO.class);
        doReturn(status).when(statusDAOMock).updateStatus(any());

        ServerDAO serverDAOMock = mock(ServerDAO.class);
        doReturn(server).when(serverDAOMock).serverById(server.getId());

        NotifyService notifyService = spy(NotifyService.class);
        doNothing().when(notifyService).notifyStatus(any());

        localController = new MainController(statusDAOMock, serverDAOMock, notifyService);

        mockMvc = MockMvcBuilders.standaloneSetup(localController).build();
    }

    @Test
    public void testDatesFromService() throws Exception {

        //Server servFromDB = (Server)testDAO.getAll(Server.class).get(0);

        mockMvc.perform(get("/servers/status/" + server.getId()))
                //.andExpect(view().name("home"));
                //.andDo(print())
                //.andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(status().isOk());
    }

}
