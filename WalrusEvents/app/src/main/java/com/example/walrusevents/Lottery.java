package com.example.walrusevents;

import java.util.List;
import java.util.Random;

/*
Functionality:
 + Randomly* select X entrants on an event's waitlist. These are the winners. All others applicants are losers.
 ? Update the Event with the winning applicants(Maybe not. If the only way to lock-in is through the notifs then maybe it can be handled when they accept.)
 + Alert notifications system to send winners/losers notifications that they won/lost
What we need:
 + The event we are drawing for
 + The list of applicants
 + The maximum number of entrants
 + Contact with the notification system, and relevant information to pass so that it may
 */
public class Lottery {
    public int drawLottery(List<Applicant> applicants, int capacity) { /* List is pass by reference, so edits made here should be accessible without retirning a value */
        int unselecteds = 0;
        for(int i = 0; i < applicants.size(); i++) {
            if(applicants.get(i).getStatus() == 0) unselecteds++;
        }
        if(capacity > applicants.size() | applicants.size() < 1 | capacity > unselecteds) return -1;

        Random gen = new Random();

        for(int i = 0; i < capacity; i++) {
            int x = gen.nextInt(capacity);
            while(applicants.get(x).getStatus() > 0) {
                x = gen.nextInt(capacity);
            }
            applicants.get(x).setStatus(1);
        }

        return 0;
    }
}
