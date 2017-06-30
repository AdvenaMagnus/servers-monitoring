package core.server.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.ServerStatus;
import core.utils.DateUtils;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Alexander on 12.05.2017.
 */

@Entity
public class ServerStatusCached {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    //@Cascade({org.hibernate.annotations.CascadeType.ALL})
    @JsonIgnore
    Server owner;

//    @Enumerated(EnumType.STRING)
//    ServerStatus status;
    String revision;
    Date revisionDate;

    Date date;

    Date createDate;


    boolean isClosed=false;

    @PrePersist
    public void onCreate(){
        this.setCreateDate(new Date());
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Server getOwner() {
        return owner;
    }
    public void setOwner(Server owner) {
        this.owner = owner;
    }

    public String getRevision() {
        return revision;
    }
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat, timezone = "GMT+3")
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean getIsClosed() {
        return isClosed;
    }
    public void setIsClosed(boolean closed) {
        isClosed = closed;
    }

    @Transient
    public ServerStatus getStatus() {
        return isClosed? ServerStatus.offline: ServerStatus.online;
    }


    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.revisionDateFormat)
    public Date getRevisionDate() {
        return revisionDate;
    }
    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }
}
