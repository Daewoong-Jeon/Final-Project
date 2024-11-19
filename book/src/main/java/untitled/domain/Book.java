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

    private Date rentalDate;

    private Date requiredReturnDate;

    @PostPersist
    public void onPostPersist() {
        RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(this);
        rentalStatusUpdated.publishAfterCommit();

        AvailableStatusUpdated availableStatusUpdated = new AvailableStatusUpdated(
            this
        );
        availableStatusUpdated.publishAfterCommit();

        BookAdded bookAdded = new BookAdded(this);
        bookAdded.publishAfterCommit();
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
        */

        /** Example 2:  finding and process
        
        repository().findById(bookRent.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);

            RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(book);
            rentalStatusUpdated.publishAfterCommit();

         });
        */

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

        /** Example 2:  finding and process
        
        repository().findById(bookReturned.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);

            AvailableStatusUpdated availableStatusUpdated = new AvailableStatusUpdated(book);
            availableStatusUpdated.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
