package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import untitled.infra.AbstractEvent;

@Data
public class BookAdded extends AbstractEvent {

    private String id;
    private String status;
    private Integer cost;
}
