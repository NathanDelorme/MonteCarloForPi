package com.nathandelorme;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class PiParallelLoop extends PiMonteCarlo
{
    private AtomicLong nbInCible = new AtomicLong(0);
    public int nbThread;

    public PiParallelLoop(long _nbTotal, int _nbThread)
    {
        super("Boucle parallèle", _nbTotal);
        nbThread = _nbThread;
    }

    public PiParallelLoop(long _nbTotal)
    {
        super("Boucle parallèle", _nbTotal);
        nbThread = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void launch()
    {
        startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool(nbThread);

        for (int i = 1; i <= nbTotal; i++)
            executor.execute(new Iteration());

        executor.shutdown();
        while (!executor.isTerminated()) { }

        nbCible = nbInCible.get();
        piValue = 4.0 * nbCible / nbTotal;
        endTime = System.currentTimeMillis();
        processTime = (endTime - startTime);
        piError = (piValue - Math.PI);
    }

    public void launchOptimized()
    {
        startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newWorkStealingPool(nbThread);

        for (int i = 1; i <= nbTotal; i++)
            executor.execute(new IterationVariante());

        executor.shutdown();
        while (!executor.isTerminated()) { }

        nbCible = nbInCible.get();
        piValue = 4.0 * (nbTotal - nbCible) / nbTotal;
        endTime = System.currentTimeMillis();
        processTime = (endTime - startTime);
        piError = (piValue - Math.PI);
    }

    class Iteration implements Runnable
    {
        @Override
        public void run()
        {
            double xp = Math.random();
            double yp = Math.random();

            if (xp * xp + yp * yp <= 1)
                nbInCible.incrementAndGet();
        }
    }

    class IterationVariante implements Runnable
    {
        @Override
        public void run()
        {
            double xp = Math.random();
            double yp = Math.random();

            if (xp * xp + yp * yp > 1)
                nbInCible.incrementAndGet();
        }
    }
}
