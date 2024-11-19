package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class RentalPointDecreased extends AbstractEvent {

    private String id;
    private Integer rentalPoint;

    public RentalPointDecreased(Member aggregate) {
        super(aggregate);
    }

    public RentalPointDecreased() {
        super();
    }
}
//>>> DDD / Domain Event
