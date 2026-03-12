package com.example.walrusevents;

import java.util.ArrayList;
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
 ...
 */

/**
 * This is a class that handles the drawing of invitations for entrants.
 */
public class Lottery {
    /**
     * This method performs a draw to invite a randomly selected number of entrants from a list to enroll in an event.
     * @param entrants
     *  This is the list of entrants. It consists of a user id that can be used to refer back to their profile and the current status of their application
     * @param capacity
     *  This is the total number of entrants that can be enrolled in the event. capacity >= (number of INVITED + number of ACCEPTED)
     * @return
     *  Returns false if the draw fails
     */
    public boolean drawToCapacity(List<Entry> entrants, int capacity) {
        if (capacity < 1) return false; /* Draw failed: capacity needs to be at least one */

        int length = entrants.size();
        if (length < 1) return false; /* Draw failed: no entrants exist */

        int limit = capacity;
        // Create a new list of entrants that are PENDING and track how many seats are already reserved.
        ArrayList<Entry> pending = new ArrayList<>();
        for (Entry entrant : entrants) {
            if (entrant.getStatus() == Status.PENDING) {
                pending.add(entrant);
            } else if (entrant.getStatus() == Status.INVITED || entrant.getStatus() == Status.ACCEPTED) {
                limit--;
            }
        }

        if (pending.isEmpty()) return false; /* Draw failed: no pending entrants can be selected */

        // If event can seat all PENDING entrants, invite them all
        if (limit >= pending.size()) {
            for (Entry entrant : pending) {
                if (invite(entrant) < 0)
                    return false; /* Draw failed: non-PENDING entrant found in the draw */
            }
            return true;
        }

        // Else, do a random draw
        Random gen = new Random();

        for (int i = 0; i < limit; i++) {
            int index = gen.nextInt(pending.size());
            if (invite(pending.get(index)) < 0)
                return false; /* Draw failed: attempted to invite non-PENDING entrant */
            pending.remove(index);
        }

        return true;
    }

    /**
     * This method is used to set an entrant in a waitlist as INVITED
     * @param entrant
     *  The entrant
     * @return
     *  Returns -1 if the entrant is not PENDING and therefore cannot be invited
     */
    private int invite(Entry entrant) {
        if(entrant.getStatus() != Status.PENDING) return -1;
        entrant.setStatus(Status.INVITED);
        return 0;
    }
}
