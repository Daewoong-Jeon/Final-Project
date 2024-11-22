package untitled.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import untitled.MemberApplication;
import untitled.domain.RentalPointCharged;
import untitled.domain.RentalPointDecreased;
import untitled.domain.RentalPointIncreased;

@Entity
@Table(name = "Member_table")
@Data
//<<< DDD / Aggregate Root
public class Member {

    @Id
    private String id;

    private Date joinDate;

    private Integer rentalPoint;

    @PostPersist
    public void onPostPersist() {
//        RentalPointDecreased rentalPointDecreased = new RentalPointDecreased(
//            this
//        );
//        rentalPointDecreased.publishAfterCommit();
//
//        RentalPointIncreased rentalPointIncreased = new RentalPointIncreased(
//            this
//        );
//        rentalPointIncreased.publishAfterCommit();
//
//        LackOfPointsReturned lackOfPointsReturned = new LackOfPointsReturned(
//                this
//        );
//        lackOfPointsReturned.publishAfterCommit();
    }

    public static MemberRepository repository() {
        MemberRepository memberRepository = MemberApplication.applicationContext.getBean(
            MemberRepository.class
        );
        return memberRepository;
    }

    //<<< Clean Arch / Port Method
    public void chargeRentalPoint(
            ChargeRentalPointCommand chargeRentalPointCommand
    ) {
        setRentalPoint(getRentalPoint() + chargeRentalPointCommand.getRentalPoint());

        RentalPointCharged rentalPointCharged = new RentalPointCharged(this);
        rentalPointCharged.publishAfterCommit();

    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void decreaseRentalPoint(
        RentalStatusUpdated rentalStatusUpdated
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Member member = new Member();
        repository().save(member);

        RentalPointDecreased rentalPointDecreased = new RentalPointDecreased(member);
        rentalPointDecreased.publishAfterCommit();

         LackOfPointsReturned lackOfPointsReturned = new LackOfPointsReturned(member);
         lackOfPointsReturned.publishAfterCommit();
        */

        // Example 2:  finding and process
        
        repository().findById(rentalStatusUpdated.getMemberId()).ifPresent(member->{
            if (member.getRentalPoint() - rentalStatusUpdated.getCost() >= 0) {

                member.setRentalPoint(member.getRentalPoint() - rentalStatusUpdated.getCost());
                repository().save(member);

                RentalPointDecreased rentalPointDecreased = new RentalPointDecreased(member);
                rentalPointDecreased.publishAfterCommit();

            } else {

                LackOfPointsReturned lackOfPointsReturned = new LackOfPointsReturned();
                lackOfPointsReturned.setId(member.getId());
                lackOfPointsReturned.setRentalPoint(member.getRentalPoint());
                lackOfPointsReturned.setBookId(rentalStatusUpdated.getId());
                lackOfPointsReturned.publishAfterCommit();

//                LackOfPointsReturned lackOfPointsReturned = new LackOfPointsReturned(member);
//                lackOfPointsReturned.publishAfterCommit();

            }

         });

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void increaseRentalPoint(
        AvailableStatusUpdated availableStatusUpdated
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Member member = new Member();
        repository().save(member);

        RentalPointIncreased rentalPointIncreased = new RentalPointIncreased(member);
        rentalPointIncreased.publishAfterCommit();
        */

        // Example 2:  finding and process
        
        repository().findById(availableStatusUpdated.getMemberId()).ifPresent(member->{

            member.setRentalPoint(member.getRentalPoint() + availableStatusUpdated.getCost());
            repository().save(member);

            RentalPointIncreased rentalPointIncreased = new RentalPointIncreased(member);
            rentalPointIncreased.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
