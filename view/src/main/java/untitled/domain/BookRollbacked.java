package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import untitled.infra.AbstractEvent;

@Data
public class BookRollbacked extends AbstractEvent {

    private String id;
    private String memberId;
    private String status;
}