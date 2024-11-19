package untitled.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import untitled.RentalApplication;
import untitled.domain.BookRent;
import untitled.domain.BookReturned;

@Entity
@Table(name = "Rental_table")
@Data
//<<< DDD / Aggregate Root
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookId;

    private String memberId;

    private Date rentalDate;

    private Date requiredReturnDate;

    private Date returnDate;

    private String overdueYn;

    @PostPersist
    public void onPostPersist() {
        BookRent bookRent = new BookRent(this);
        bookRent.publishAfterCommit();

        BookReturned bookReturned = new BookReturned(this);
        bookReturned.publishAfterCommit();
    }

    public static RentalRepository repository() {
        RentalRepository rentalRepository = RentalApplication.applicationContext.getBean(
            RentalRepository.class
        );
        return rentalRepository;
    }
}
//>>> DDD / Aggregate Root
