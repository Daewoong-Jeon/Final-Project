package untitled.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import untitled.config.kafka.KafkaProcessor;
import untitled.domain.*;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    MemberRepository memberRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='RentalStatusUpdated'"
    )
    public void wheneverRentalStatusUpdated_DecreaseRentalPoint(
        @Payload RentalStatusUpdated rentalStatusUpdated
    ) {
        RentalStatusUpdated event = rentalStatusUpdated;
        System.out.println(
            "\n\n##### listener DecreaseRentalPoint : " +
            rentalStatusUpdated +
            "\n\n"
        );

        // Sample Logic //
        Member.decreaseRentalPoint(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AvailableStatusUpdated'"
    )
    public void wheneverAvailableStatusUpdated_IncreaseRentalPoint(
        @Payload AvailableStatusUpdated availableStatusUpdated
    ) {
        AvailableStatusUpdated event = availableStatusUpdated;
        System.out.println(
            "\n\n##### listener IncreaseRentalPoint : " +
            availableStatusUpdated +
            "\n\n"
        );

        // Sample Logic //
        Member.increaseRentalPoint(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
