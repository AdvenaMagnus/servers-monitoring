package core.server.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.enums.ServerStatus;
import core.utils.DateUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by Alexander on 12.05.2017.
 */

@Entity
public class ServerStatusCached {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    ServerStatus status;
    String revision;
    Date revisionDate;

    Date date;
    int min;
    int hours;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat)
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }

    public int getHours() {
        return hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }

    public ServerStatus getStatus() {
        return status;
    }
    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat)
    public Date getRevisionDate() {
        return revisionDate;
    }
    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }
}
