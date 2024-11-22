package untitled.domain;

import untitled.domain.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class LackOfPointsReturned extends AbstractEvent {

    private String id;
    private Integer rentalPoint;
    private String bookId;

    public LackOfPointsReturned(Member aggregate) {
        super(aggregate);
    }

    public LackOfPointsReturned() {
        super();
    }
}
//>>> DDD / Domain Event