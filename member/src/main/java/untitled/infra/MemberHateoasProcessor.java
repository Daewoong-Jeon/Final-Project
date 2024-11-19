package untitled.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import untitled.domain.*;

@Component
public class MemberHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Member>> {

    @Override
    public EntityModel<Member> process(EntityModel<Member> model) {
        return model;
    }
}
