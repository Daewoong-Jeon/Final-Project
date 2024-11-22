package untitled.domain;

import untitled.domain.*;
import java.util.*;
import lombok.*;
import untitled.infra.AbstractEvent;

@Data
@ToString
public class BookRollbacked extends AbstractEvent {

    private String id;
    private String memberId;
    private String status;
}