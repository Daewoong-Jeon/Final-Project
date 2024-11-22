package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class RentBookCommand {

    private String bookId;
    private String memberId;
    private Integer id;
}