package com.nathandelorme;

public class PiMonteCarlo
{
    public String methodName;
    public long nbTotal;
    public long nbCible;

    public double piValue;
    public double piError;

    public long processTime;
    protected long startTime;
    protected long endTime;

    public PiMonteCarlo(String _methodName, long _nbTotal)
    {
        methodName = _methodName;
        nbTotal = _nbTotal;
        nbCible = 0;
        piValue = 0f;
        piError = 0f;
        processTime = 0;
    }

    public void launch()
    {
        throw new UnsupportedOperationException("Function not implemented with method" + methodName);
    }

    public String toString()
    {
        String res = "Avec la méthode '" + methodName + "'";
        res += "\n\tpiValue = " + piValue;
        res += "\n\tpiError = " + piError;
        res += "\n\tnbTotal = " + nbTotal;
        res += "\n\tnbCible = " + nbCible;
        res += "\n\tprocessTime = " + processTime + "ms";

        return res;
    }
}
