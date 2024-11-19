package untitled.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "BookList_table")
@Data
public class BookList {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private String bookId;

    private String rentalStatus;
    private String recentRentalMemberId;
    private Integer rentalCost;
    private Date recentRentalDate;
    private Date requiredReturnDate;
}
