package untitled.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/rentals")
@Transactional
public class RentalController {

    @Autowired
    RentalRepository rentalRepository;

    @RequestMapping(
            value = "/rentals/rentbook",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8"
    )
    public Rental rentBook(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody RentBookCommand rentBookCommand
    ) throws Exception {
        System.out.println("##### /rental/rentBook  called #####");
        Rental rental = new Rental();
        rental.rentBook(rentBookCommand);
        rentalRepository.save(rental);
        return rental;
    }

    @RequestMapping(
            value = "/rentals/{id}/returnbook",
            method = RequestMethod.PUT,
            produces = "application/json;charset=UTF-8"
    )
    public Rental returnBook(
            @PathVariable(value = "id") Long id,
            @RequestBody ReturnBookCommand returnBookCommand,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /rental/returnBook  called #####");
        Optional<Rental> optionalRental = rentalRepository.findById(id);

        optionalRental.orElseThrow(() -> new Exception("No Entity Found"));
        Rental rental = optionalRental.get();
        rental.returnBook(returnBookCommand);

        rentalRepository.save(rental);
        return rental;
    }
}
//>>> Clean Arch / Inbound Adaptor
