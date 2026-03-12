package com.example.walrusevents;

import org.junit.Test;

import java.util.ArrayList;

public class LotteryTest {
    private ArrayList<Entry> makeList() {
        ArrayList<Entry> entrants = new ArrayList();
        for(Integer i = 0; i< 10; i++) {
            entrants.add(new Entry(i.toString()));
        }
        return entrants;
    }

    private int winCount(ArrayList<Entry> entries) {
        int count = 0;
        for(Entry entrant: entries) {
            if(entrant.getStatus() == Status.INVITED) {
                count++;
            }
        }
        return count;
    }

    @Test
    public void testDrawToCapacity() {
        ArrayList<Entry> entrants = makeList();
        Lottery lottery = new Lottery();

    // Default: draw to capacity from a list of all PENDING entrants (capacity < entrants.size())
        // Draw 3 winners
        lottery.drawToCapacity(entrants, 3);

        // Count winners
        assert(winCount(entrants) == 3);

    // Subsequent draws (draw to 6)
        lottery.drawToCapacity(entrants, 6);
        assert(winCount(entrants) == 6);

    // Subsequent Overdraw
        lottery.drawToCapacity(entrants, 20);
        assert(winCount(entrants) == 10);
    }

    @Test
    public void testDrawToCapacityExceptions() {
        ArrayList<Entry> entrants = new ArrayList<>();
        Lottery lottery = new Lottery();

    // Empty list
        assert(!lottery.drawToCapacity(entrants, 3));

    // Invalid capacity
        entrants = makeList();
        assert(!lottery.drawToCapacity(entrants, -1));

    // No PENDING entrants
        lottery.drawToCapacity(entrants, 10);
        assert(!lottery.drawToCapacity(entrants, 1));
    }
}
