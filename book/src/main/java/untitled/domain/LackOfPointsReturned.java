package untitled.domain;

import untitled.domain.*;
import java.util.*;
import lombok.*;
import untitled.infra.AbstractEvent;

@Data
@ToString
public class LackOfPointsReturned extends AbstractEvent {

    private String id;
    private Integer rentalPoint;
    private String bookId;
}