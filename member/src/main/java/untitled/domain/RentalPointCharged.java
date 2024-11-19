package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class RentalPointCharged extends AbstractEvent {

    private String id;
    private Integer rentalPoint;

    public RentalPointCharged(Member aggregate) {
        super(aggregate);
    }

    public RentalPointCharged() {
        super();
    }
}
//>>> DDD / Domain Event
