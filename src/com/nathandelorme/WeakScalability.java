package com.nathandelorme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WeakScalability
{
    private String testName;
    public ArrayList<Long> testedNbTotal;
    public ArrayList<Integer> testedThreadNb;
    public ArrayList<Double> testedProcessTimes;
    private int iteration;

    public WeakScalability(Class<? extends PiMonteCarlo> testedClass, long initNbTotal, int powerTestNumber, int iteration)
    {
        testName = testedClass.getSimpleName();
        this.iteration = iteration;
        testedNbTotal = new ArrayList<Long>();
        testedThreadNb = new ArrayList<Integer>();
        testedProcessTimes = new ArrayList<Double>();
        long nbTotal = initNbTotal;
        int thread = 1;

        for(int power = 0; power < powerTestNumber; power++)
        {
            testedNbTotal.add(nbTotal);
            testedThreadNb.add(thread);
            nbTotal *= 2;
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
            long averageProcessTime = 0;

            for(int iter = 0; iter < iteration; iter++)
            {
                PiParallelLoop testAlgorithm = new PiParallelLoop(testedNbTotal.get(idx), testedThreadNb.get(idx));
                testAlgorithm.launch();
                averageProcessTime += testAlgorithm.processTime;
            }
            testedProcessTimes.add((double) (averageProcessTime / iteration));
        }
    }

    public void TestMasterWorker()
    {
        for(int idx = 0; idx < testedNbTotal.size(); idx++)
        {
            long averageProcessTime = 0;

            for(int iter = 0; iter < iteration; iter++)
            {
                PiMasterWorker testAlgorithm = new PiMasterWorker(testedNbTotal.get(idx), testedThreadNb.get(idx));
                testAlgorithm.launch();
                averageProcessTime += testAlgorithm.processTime;
            }
            testedProcessTimes.add((double) (averageProcessTime / iteration));
        }
    }

    public void BuildChart()
    {
        new WeakScalabilityChart(testName);
    }

    class WeakScalabilityChart extends JFrame
    {

        public WeakScalabilityChart(String title)
        {
            super(title);
            // Create dataset
            XYDataset dataset = createDataset();
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Scalabilité faible - " + testName, // Chart title
                    "Nombre de Threads", // X-Axis Label
                    "Efficacité", // Y-Axis Label
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

        private XYDataset createDataset() {
            XYSeriesCollection dataset = new XYSeriesCollection();

            XYSeries idealEfficiency = new XYSeries("Efficacité idéale");
            for(Integer thread : testedThreadNb)
                idealEfficiency.add((double) thread, 1);

            XYSeries realEfficiency = new XYSeries("Efficacité réelle");
            for(int idx = 0; idx < testedThreadNb.size(); idx++)
            {
                System.out.println(testedProcessTimes.get(0));
                System.out.println((double) testedProcessTimes.get(0) / (double)testedProcessTimes.get(idx));
                System.out.println(testedProcessTimes.get(idx));
                System.out.println();
                realEfficiency.add((double) testedThreadNb.get(idx), (double)testedProcessTimes.get(0) / (double)testedProcessTimes.get(idx));
            }

            // Add series to dataset
            dataset.addSeries(idealEfficiency);
            dataset.addSeries(realEfficiency);
            return dataset;
        }
    }
}
