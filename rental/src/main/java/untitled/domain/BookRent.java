package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookRent extends AbstractEvent {

    private String memberId;
    private String bookId;

    public BookRent(Rental aggregate) {
        super(aggregate);
    }

    public BookRent() {
        super();
    }
}
//>>> DDD / Domain Event
