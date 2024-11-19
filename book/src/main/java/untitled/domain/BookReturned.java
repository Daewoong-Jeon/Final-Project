package untitled.domain;

import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

@Data
@ToString
public class BookReturned extends AbstractEvent {

    private String bookId;
    private String memberId;
    private String overdueYn;
}
