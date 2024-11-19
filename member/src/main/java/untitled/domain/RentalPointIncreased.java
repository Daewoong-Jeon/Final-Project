package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class RentalPointIncreased extends AbstractEvent {

    private String id;
    private Integer rentalPoint;

    public RentalPointIncreased(Member aggregate) {
        super(aggregate);
    }

    public RentalPointIncreased() {
        super();
    }
}
//>>> DDD / Domain Event
