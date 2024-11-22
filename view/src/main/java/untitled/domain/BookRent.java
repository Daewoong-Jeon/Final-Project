package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import untitled.infra.AbstractEvent;

@Data
public class BookRent extends AbstractEvent {

    private String memberId;
    private String bookId;
    private Date rentalDate;
    private Date requiredReturnDate;
    private Long id;
}