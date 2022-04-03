package com.nathandelorme;

import java.util.ArrayList;

public class Main
{
    public static void main(String[] args)
    {
        ArrayList<Long> testedNbTotal = new ArrayList<Long>();
        testedNbTotal.add(1600L);
        testedNbTotal.add(16000L);
        new StrongScalability(PiParallelLoop.class, testedNbTotal, 6, 3).BuildChart();
        new StrongScalability(PiMasterWorker.class, testedNbTotal, 6, 3).BuildChart();

        new WeakScalability(PiParallelLoop.class, 200000, 6, 3).BuildChart();
        new WeakScalability(PiMasterWorker.class, 200000, 6, 3).BuildChart();
    }
}
