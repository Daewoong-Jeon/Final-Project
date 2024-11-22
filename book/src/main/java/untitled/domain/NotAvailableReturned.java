package untitled.domain;

import untitled.domain.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class NotAvailableReturned extends AbstractEvent {

    private String id;
    private String memberId;
    private String status;

    public NotAvailableReturned(Book aggregate) {
        super(aggregate);
    }

    public NotAvailableReturned() {
        super();
    }
}
//>>> DDD / Domain Event