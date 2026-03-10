package com.example.walrusevents;

import org.junit.Test;

import java.util.ArrayList;

public class LotteryTest {
    private ArrayList<Applicant> makeList() {
        ArrayList<Applicant> applicants = new ArrayList();
        for(int i = 0; i< 10; i++) {
            applicants.add(new Applicant(i));
        }
        return applicants;
    }

    @Test
    public void testDraw() {
        ArrayList<Applicant> applicants = makeList();
        Lottery lottery = new Lottery();

        // Draw 3 winners
        lottery.drawLottery(applicants, 3);

        // Count winners
        int winCount = 0;
        for(int i = 0; i< 10; i++) {
            if(applicants.get(i).getStatus() == 1) winCount++;
        }
        assert(winCount == 3);
    }

    @Test
    public void testOverdraw() {
        ArrayList<Applicant> applicants = makeList();
        Lottery lottery = new Lottery();

        // Draw too many winners
        assert(lottery.drawLottery(applicants, 20) < 0);
    }

    @Test
    public void testSubsequentOverdraw() {
        ArrayList<Applicant> applicants = makeList();
        Lottery lottery = new Lottery();

        // Draw 6 winners
        lottery.drawLottery(applicants, 6);

        // Draw more winners than there are unselected
        assert(lottery.drawLottery(applicants, 5) < 0);
    }
}
