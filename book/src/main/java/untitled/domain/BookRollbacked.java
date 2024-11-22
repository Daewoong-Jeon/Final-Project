package untitled.domain;

import untitled.domain.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import untitled.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookRollbacked extends AbstractEvent {

    private String id;
    private String memberId;
    private String status;

    public BookRollbacked(Book aggregate) {
        super(aggregate);
    }

    public BookRollbacked() {
        super();
    }
}
//>>> DDD / Domain Event