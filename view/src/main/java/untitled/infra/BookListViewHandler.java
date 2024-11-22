package untitled.infra;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import untitled.config.kafka.KafkaProcessor;
import untitled.domain.*;

@Service
public class BookListViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private BookListRepository bookListRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookAdded_then_CREATE_1(@Payload BookAdded bookAdded) {
        try {
            if (!bookAdded.validate()) return;

            // view 객체 생성
            BookList bookList = new BookList();
            // view 객체에 이벤트의 Value 를 set 함
            bookList.setBookId(String.valueOf(bookAdded.getId()));
            bookList.setRentalStatus(bookAdded.getStatus());
            bookList.setRentalCost(bookAdded.getCost());
            // view 레파지 토리에 save
            bookListRepository.save(bookList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRentalStatusUpdated_then_UPDATE_1(
        @Payload RentalStatusUpdated rentalStatusUpdated
    ) {
        try {
            if (!rentalStatusUpdated.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                rentalStatusUpdated.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(rentalStatusUpdated.getStatus());
                bookList.setRecentRentalMemberId(
                    rentalStatusUpdated.getMemberId()
                );
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenAvailableStatusUpdated_then_UPDATE_2(
        @Payload AvailableStatusUpdated availableStatusUpdated
    ) {
        try {
            if (!availableStatusUpdated.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                availableStatusUpdated.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(availableStatusUpdated.getStatus());
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRent_then_UPDATE_3(@Payload BookRent bookRent) {
        try {
            if (!bookRent.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookRent.getBookId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRecentRentalDate(bookRent.getRentalDate());
                bookList.setRequiredReturnDate(
                        bookRent.getRequiredReturnDate()
                );
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookReturned_then_UPDATE_4(
            @Payload BookReturned bookReturned
    ) {
        try {
            if (!bookReturned.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookReturned.getBookId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRollbacked_then_UPDATE_5(
            @Payload BookRollbacked bookRollbacked
    ) {
        try {
            if (!bookRollbacked.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookRollbacked.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(bookRollbacked.getStatus());
                bookList.setRecentRentalMemberId(null);
                bookList.setRecentRentalDate(null);
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
