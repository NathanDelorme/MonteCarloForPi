package com.nathandelorme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StrongScalability
{
    private String testName;
    public ArrayList<Long> testedNbTotal;
    public ArrayList<Integer> testedThreadNb;
    public ArrayList<ArrayList<Double>> testedProcessTimesSeries;
    private int iteration;

    public StrongScalability(Class<? extends PiMonteCarlo> testedClass, ArrayList<Long> testedNbTotal, int threadsTestNumber, int iteration)
    {
        testName = testedClass.getSimpleName();
        this.iteration = iteration;
        this.testedNbTotal = testedNbTotal;
        testedThreadNb = new ArrayList<Integer>();
        testedProcessTimesSeries = new ArrayList<ArrayList<Double>>();
        int thread = 1;

        for(int test = 0; test < threadsTestNumber; test++)
        {
            testedThreadNb.add(thread);
            thread *= 2;
        }
        if (PiParallelLoop.class.equals(testedClass))
            TestParallelLoop();
        else if (PiMasterWorker.class.equals(testedClass))
            TestMasterWorker();
    }

    public void TestParallelLoop()
    {
        for(int idx = 0; idx < testedNbTotal.size(); idx++)
        {
            testedProcessTimesSeries.add(new ArrayList<Double>());

            for(int idx2 = 0; idx2 < testedThreadNb.size(); idx2++)
            {
                long averageProcessTime = 0;

                for(int iter = 0; iter < iteration; iter++)
                {
                    PiParallelLoop testAlgorithm = new PiParallelLoop(testedNbTotal.get(idx), testedThreadNb.get(idx2));
                    testAlgorithm.launch();
                    averageProcessTime += testAlgorithm.processTime;
                }
                testedProcessTimesSeries.get(idx).add((double) (averageProcessTime / iteration));
            }
        }
    }

    public void TestMasterWorker()
    {
        for(int idx = 0; idx < testedNbTotal.size(); idx++)
        {
            testedProcessTimesSeries.add(new ArrayList<Double>());

            for(int idx2 = 0; idx2 < testedThreadNb.size(); idx2++)
            {
                long averageProcessTime = 0;

                for(int iter = 0; iter < iteration; iter++)
                {
                    PiMasterWorker testAlgorithm = new PiMasterWorker(testedNbTotal.get(idx), testedThreadNb.get(idx2));
                    testAlgorithm.launch();
                    averageProcessTime += testAlgorithm.processTime;
                }
                testedProcessTimesSeries.get(idx).add((double) (averageProcessTime / iteration));
            }
        }
    }

    public void BuildChart()
    {
        new StrongScalabilityChart(testName);
    }

    class StrongScalabilityChart extends JFrame
    {

        public StrongScalabilityChart(String title)
        {
            super(title);
            // Create dataset
            XYDataset dataset = createFixedDataset();
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Scalabilité forte - " + testName, // Chart title
                    "Nombre de Threads", // X-Axis Label
                    "Speedup = T1 / Tp", // Y-Axis Label
                    dataset
            );

            XYPlot plot=(XYPlot)chart.getPlot();
            plot.setBackgroundPaint(new Color(255, 255, 255, 60));
            plot.setDomainGridlinePaint(Color.gray);
            plot.setRangeGridlinePaint(Color.gray);
            for(int i = 0; i < plot.getSeriesCount(); i++)
                plot.getRenderer().setSeriesStroke(i, new BasicStroke(2.0f));
            ChartPanel panel = new ChartPanel(chart);
            setContentPane(panel);
            setSize(800, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private XYDataset createFixedDataset() {
            XYSeriesCollection dataset = new XYSeriesCollection();

            XYSeries idealEfficiency = new XYSeries("Prévisions - 32 000 000");
            idealEfficiency.add(2, 3045);
            idealEfficiency.add(4, 3045/2);
            idealEfficiency.add(8, 3045/4);

            XYSeries realEfficiency1 = new XYSeries("Workers Moitié/Moitié entre Léo et Nathan - 32 000 000");
            realEfficiency1.add(2, 3045);
            realEfficiency1.add(4, 738);
            realEfficiency1.add(8, 510);

            XYSeries realEfficiency2 = new XYSeries("Workers Nathan - 32 000 000");
            realEfficiency2.add(2, 558);
            realEfficiency2.add(4, 307);
            realEfficiency2.add(8, 218);

            XYSeries realEfficiency3 = new XYSeries("Workers Léo - 32 000 000");
            realEfficiency3.add(2, 1364);
            realEfficiency3.add(4, 728);
            realEfficiency3.add(8, 502);

            // Add series to dataset
            dataset.addSeries(idealEfficiency);
            dataset.addSeries(realEfficiency1);
            dataset.addSeries(realEfficiency2);
            dataset.addSeries(realEfficiency3);

            return dataset;
        }

        private XYDataset createDataset() {
            XYSeriesCollection dataset = new XYSeriesCollection();
            ArrayList<XYSeries> seriesTest = new ArrayList<XYSeries>();

            XYSeries idealEfficiency = new XYSeries("Prévisions");
            for(int idx2 = 0; idx2 < testedThreadNb.size(); idx2++)
                idealEfficiency.add((double) testedThreadNb.get(idx2), testedThreadNb.get(idx2));
            seriesTest.add(idealEfficiency);

            for(int idx = 0; idx < testedNbTotal.size(); idx++)
            {
                XYSeries realEfficiency = new XYSeries("Efficacité réelle - " + testedNbTotal.get(idx));
                for(int idx2 = 0; idx2 < testedThreadNb.size(); idx2++)
                    realEfficiency.add((double) testedThreadNb.get(idx2), (double)testedProcessTimesSeries.get(idx).get(0) / (double)testedProcessTimesSeries.get(idx).get(idx2));
                seriesTest.add(realEfficiency);
            }

            // Add series to dataset
            for(XYSeries  series : seriesTest)
                dataset.addSeries(series);
            return dataset;
        }
    }
}
