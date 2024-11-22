package untitled.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import untitled.domain.*;

@RepositoryRestResource(collectionResourceRel = "bookLists", path = "bookLists")
public interface BookListRepository
        extends PagingAndSortingRepository<BookList, Long> {
    Optional<BookList> findByBookId(String id);
}