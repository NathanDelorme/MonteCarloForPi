package com.nathandelorme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class PiMasterWorker extends PiMonteCarlo
{
    public int nbThread;

    public PiMasterWorker(long _nbTotal, int _nbThread)
    {
        super("Master / Worker", _nbTotal);
        nbThread = _nbThread;
    }

    public PiMasterWorker(long _nbTotal)
    {
        super("Master / Worker", _nbTotal);
        nbThread = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void launch()
    {
        if(nbTotal % nbThread != 0)
            throw new IllegalArgumentException("nbTotal isn't divisible by nbProcess");

        startTime = System.currentTimeMillis();
        new Master().doRun(nbTotal / nbThread, nbThread);
        endTime = System.currentTimeMillis();
        processTime = (endTime - startTime);
        piError = (piValue - Math.PI);
    }

    public String toString()
    {
        String res = super.toString();
        res += "\n\tnbThread = " + nbThread;

        return res;
    }

    class Master
    {
        public void doRun(long nbTotalPerWorker, int nbProcess)
        {
            List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
            ExecutorService exec = Executors.newFixedThreadPool(nbProcess);

            for (int i = 0; i < nbProcess; ++i)
                tasks.add(new Worker(nbTotalPerWorker));

            try
            {
                List<Future<Long>> results = exec.invokeAll(tasks);

                for (Future<Long> f : results)
                    nbCible += f.get();
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }

            piValue = 4.0 * nbCible / nbTotal;
            exec.shutdown();
        }
    }

    class Worker implements Callable<Long>
    {
        private long nbTotal;

        public Worker(long _nbTotal)
        {
            this.nbTotal = _nbTotal;
        }

        @Override
        public Long call()
        {
            long nbCible = 0;
            Random prng = new Random();

            for (int j = 0; j < nbTotal; j++)
            {
                double x = prng.nextDouble();
                double y = prng.nextDouble();

                if ((x * x + y * y) < 1)
                    ++nbCible;
            }
            return nbCible;
        }
    }
}
