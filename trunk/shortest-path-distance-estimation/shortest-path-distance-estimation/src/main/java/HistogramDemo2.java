/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * HistogramDemo2.java
 * -------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HistogramDemo2.java,v 1.1 2005/04/28 16:29:15 harrym_nu Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo of the {@link HistogramDataset} class.
 */
public class HistogramDemo2 extends ApplicationFrame {

  /**
   * Creates a new demo.
   * 
   * @param title  the frame title.
   */
  public HistogramDemo2(String title, double[] values, int bins) {
    super(title);
    HistogramDataset dataset1 = new HistogramDataset();
    dataset1.addSeries("H1", values, bins);
    JFreeChart chart1 =
    ChartFactory.createHistogram("Histogram Demo", null, null, dataset1, PlotOrientation.VERTICAL, true, false, false);
    chart1.getXYPlot().setForegroundAlpha(0.75f);
    JFreeChart chart = chart1;
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    setContentPane(chartPanel);
  }

  public static void run(double[] values, int bins) {

    HistogramDemo2 demo = new HistogramDemo2("Histogram Demo 2", values, bins);
    demo.pack();
    RefineryUtilities.centerFrameOnScreen(demo);
    demo.setVisible(true);

  }
}