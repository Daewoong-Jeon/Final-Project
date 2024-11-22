package untitled.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import untitled.BookApplication;
import untitled.domain.AvailableStatusUpdated;
import untitled.domain.BookAdded;
import untitled.domain.RentalStatusUpdated;

@Entity
@Table(name = "Book_table")
@Data
//<<< DDD / Aggregate Root
public class Book {

    @Id
    private String id;

    private String memberId;

    private String status;

    private Integer cost;


    @PostPersist
    public void onPostPersist() {
//        RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(this);
//        rentalStatusUpdated.publishAfterCommit();
//
//        AvailableStatusUpdated availableStatusUpdated = new AvailableStatusUpdated(
//            this
//        );
//        availableStatusUpdated.publishAfterCommit();
//
        BookAdded bookAdded = new BookAdded(this);
        bookAdded.publishAfterCommit();

//        NotAvailableReturned notAvailableReturned = new NotAvailableReturned(this);
//        notAvailableReturned.publishAfterCommit();
//
//        BookRollbacked bookRollbacked = new BookRollbacked(this);
//        bookRollbacked.publishAfterCommit();
    }

    public static BookRepository repository() {
        BookRepository bookRepository = BookApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static void updateRentalStatus(BookRent bookRent) {
        //implement business logic here:

        /** Example 1:  new item 
        Book book = new Book();
        repository().save(book);

        RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(book);
        rentalStatusUpdated.publishAfterCommit();

        NotAvailableReturned notAvailableReturned = new NotAvailableReturned(this);
        notAvailableReturned.publishAfterCommit();
        */

        // Example 2:  finding and process

        repository().findById(bookRent.getBookId()).ifPresent(book->{
            if ("available".equals(book.getStatus())) {

                book.setStatus("rental");
                book.setMemberId(bookRent.getMemberId());
                repository().save(book);

                RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(book);
                rentalStatusUpdated.publishAfterCommit();

            } else {

//                Book bookParam = new Book();
//                bookParam.setId(book.getId());
//                bookParam.setMemberId(bookRent.getMemberId());
//                bookParam.setStatus(book.getStatus());

        //        NotAvailableReturned notAvailableReturned = new NotAvailableReturned(this);
        //        notAvailableReturned.publishAfterCommit();

                NotAvailableReturned notAvailableReturned = new NotAvailableReturned();
                notAvailableReturned.setId(book.getId());
                notAvailableReturned.setMemberId(bookRent.getMemberId());
                notAvailableReturned.setStatus(book.getStatus());
                notAvailableReturned.publishAfterCommit();

            }

         });

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateAvailableStatus(BookReturned bookReturned) {
        //implement business logic here:

        /** Example 1:  new item 
        Book book = new Book();
        repository().save(book);

        AvailableStatusUpdated availableStatusUpdated = new AvailableStatusUpdated(book);
        availableStatusUpdated.publishAfterCommit();
        */

        // Example 2:  finding and process
        
        repository().findById(bookReturned.getBookId()).ifPresent(book->{

            book.setStatus("available");
            book.setMemberId(null);
            repository().save(book);

            Book bookParam = new Book();
            bookParam.setId(book.getId());
            bookParam.setMemberId(bookReturned.getMemberId());
            bookParam.setStatus("available");
            if ("Y".equals(bookReturned.getOverdueYn()))
                bookParam.setCost(0);
            else
                bookParam.setCost(book.getCost() / 10);

            AvailableStatusUpdated availableStatusUpdated = new AvailableStatusUpdated(bookParam);
            availableStatusUpdated.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void rollbackBook(LackOfPointsReturned lackOfPointsReturned) {
        //implement business logic here:

        /** Example 1:  new item
         Book book = new Book();
         repository().save(book);
         BookRollbacked bookRollbacked = new BookRollbacked(book);
         bookRollbacked.publishAfterCommit();
         */

        // Example 2:  finding and process

         repository().findById(lackOfPointsReturned.getBookId()).ifPresent(book->{

             Book bookParam = new Book();
             bookParam.setStatus("available");
             bookParam.setMemberId(book.getMemberId());
             bookParam.setCost(book.getCost());
             bookParam.setId(book.getId());

             book.setStatus("available");
             book.setMemberId(null);
             repository().save(book);

             BookRollbacked bookRollbacked = new BookRollbacked(bookParam);
             bookRollbacked.publishAfterCommit();
         });

    }

}
//>>> DDD / Aggregate Root
