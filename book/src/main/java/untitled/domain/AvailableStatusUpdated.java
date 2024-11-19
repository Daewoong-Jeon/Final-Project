package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class AvailableStatusUpdated extends AbstractEvent {

    private String memberId;
    private Integer cost;
    private String id;
    private String status;
    private Date rentalDate;
    private Date requiredReturnDate;

    public AvailableStatusUpdated(Book aggregate) {
        super(aggregate);
    }

    public AvailableStatusUpdated() {
        super();
    }
}
//>>> DDD / Domain Event
