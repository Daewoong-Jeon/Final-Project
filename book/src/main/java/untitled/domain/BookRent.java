package untitled.domain;

import java.util.*;
import lombok.*;
import untitled.domain.*;
import untitled.infra.AbstractEvent;

@Data
@ToString
public class BookRent extends AbstractEvent {

    private String memberId;
    private String bookId;
}
