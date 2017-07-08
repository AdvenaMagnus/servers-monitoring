package core.server.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import core.enums.DowntimeReason;
import core.utils.DateUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Alexander on 04.07.2017.
 */

@Entity
@Repository
public class OnMaintenanceStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@ManyToOne
	Server owner;

	@Enumerated(EnumType.STRING)
	DowntimeReason cause;

	Date dateFrom;
	Date dateTo;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@JsonIgnore
	public Server getOwner() {
		return owner;
	}
	public void setOwner(Server owner) {
		this.owner = owner;
	}

	public DowntimeReason getCause() {
		return cause;
	}
	public void setCause(DowntimeReason cause) {
		this.cause = cause;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat, timezone = "GMT+5")
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern= DateUtils.dateFormat, timezone = "GMT+5")
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
}
