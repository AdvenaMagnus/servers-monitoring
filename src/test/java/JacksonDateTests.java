import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.server.entities.ServerStatusCached;
import core.utils.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alexander on 01.07.2017.
 */
public class JacksonDateTests {

    @Test
    public void serializeTest() throws ParseException, JsonProcessingException {
//        SimpleDateFormat df = new SimpleDateFormat(DateUtils.dateFormat);
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        String toParse = "13:30 20.12.2014";
//        Date date = df.parse(toParse);
        ServerStatusCached status = new ServerStatusCached();
        //status.setDate(date);
        //status.setDate(new Date());
        status.setDate(DateUtils.getCurrentTime());

        SimpleDateFormat df2 = new SimpleDateFormat(DateUtils.dateFormat);
        String parsedDate = df2.format(status.getDate());


        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(status);
        assertTrue(result.contains(parsedDate));
    }

}
