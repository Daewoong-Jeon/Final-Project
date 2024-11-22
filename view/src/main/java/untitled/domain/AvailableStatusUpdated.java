package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import untitled.infra.AbstractEvent;

@Data
public class AvailableStatusUpdated extends AbstractEvent {

    private String memberId;
    private Integer cost;
    private String id;
    private String status;
}
