package untitled.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import untitled.domain.*;

import java.util.Optional;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "rentals", path = "rentals")
public interface RentalRepository
    extends PagingAndSortingRepository<Rental, Long> {
    Optional<Rental> findByBookIdAndMemberIdAndResult(String bookId, String memberId, String rentSuccess);
}
