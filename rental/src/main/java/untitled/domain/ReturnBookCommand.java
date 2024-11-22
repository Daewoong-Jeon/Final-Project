package untitled.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ReturnBookCommand {

    private String bookId;
    private String memberId;
    private Integer id;
}