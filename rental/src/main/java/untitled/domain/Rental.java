package untitled.domain;

import java.time.LocalDate;
import java.util.Calendar;
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

    private String result;

    @PostPersist
    public void onPostPersist() {
//        BookRent bookRent = new BookRent(this);
//        bookRent.publishAfterCommit();
//
//        BookReturned bookReturned = new BookReturned(this);
//        bookReturned.publishAfterCommit();
    }

    public static RentalRepository repository() {
        RentalRepository rentalRepository = RentalApplication.applicationContext.getBean(
            RentalRepository.class
        );
        return rentalRepository;
    }

    //<<< Clean Arch / Port Method
    public void rentBook(RentBookCommand rentBookCommand) {
        Date now = new Date();

        setBookId(rentBookCommand.getBookId());
        setMemberId(rentBookCommand.getMemberId());
        setRentalDate(now);
        setResult("rent success");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7); // 일 계산
        Date requiredReturnDate = new Date(cal.getTimeInMillis());
        setRequiredReturnDate(requiredReturnDate);

        BookRent bookRent = new BookRent(this);
        bookRent.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void returnBook(ReturnBookCommand returnBookCommand) {
        Date now = new Date();

        setReturnDate(now);
        setResult("return success");
        if (getRequiredReturnDate().compareTo(now) < 0)
            setOverdueYn("Y");

        BookReturned bookReturned = new BookReturned(this);
        bookReturned.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void updateNotAvailable(NotAvailableReturned notAvailableReturned) {
        //implement business logic here:

        /** Example 1:  new item
         Rental rental = new Rental();
         repository().save(rental);
         */

        // Example 2:  finding and process

//        repository().findById(notAvailableReturned.getRentalId().longValue()).ifPresent(rental->{
//            rental.setResult("fail: NotAvailableBook");
//            repository().save(rental);
//        });

        repository().findByBookIdAndMemberIdAndResult(notAvailableReturned.getId(), notAvailableReturned.getMemberId(), "rent success").ifPresent(rental->{
            rental.setResult("fail: NotAvailableBook");
            repository().save(rental);
        });

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateLackOfPoints(BookRollbacked bookRollbacked) {
        //implement business logic here:

        /** Example 1:  new item
         Rental rental = new Rental();
         repository().save(rental);
         */

        // Example 2:  finding and process

        repository().findByBookIdAndMemberIdAndResult(bookRollbacked.getId(), bookRollbacked.getMemberId(), "rent success").ifPresent(rental->{
            rental.setResult("fail: LackOfPoints");
            repository().save(rental);
        });

//         repository().findById(bookRollbacked.getRentalId().longValue()).ifPresent(rental->{
//
//             rental.setResult("fail: LackOfPoints");
//             repository().save(rental);
//         });

    }
}
//>>> DDD / Aggregate Root
