package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import untitled.infra.AbstractEvent;

@Data
public class BookReturned extends AbstractEvent {

    private String bookId;
    private String memberId;
    private String overdueYn;
    private Date returnDate;
}