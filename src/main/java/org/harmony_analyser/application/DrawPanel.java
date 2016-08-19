package org.harmony_analyser.application;

import javax.swing.*;
import java.awt.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

class DrawPanel extends JPanel {
	private Point cursor; // cursor of drawing (moves from left to right on the canvas)

	/* Public / Package methods */

	public DrawPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		cursor = new Point();
		cursor.setLocation(0, 0);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void paintChordSegmentation(Graphics g) {
		drawChordSegmentation(g);
	}

	public void paintComplexityColumnGraph(Graphics g) {
		drawComplexityColumnGraph(g, 3.1, 3.5, 1.4);
	}

	/* Private methods */

	/* Complet analysis */

	private void drawChordSegmentation(Graphics g) {
		drawSegment(g, 0.2, Color.CYAN);
		drawSegment(g, 0.2, Color.RED);
		drawSegment(g, 0.2, Color.YELLOW);
		drawSegment(g, 0.2, Color.BLUE);
		drawSegment(g, 0.2, Color.BLACK);
	}

	private void drawComplexityColumnGraph(Graphics g, double atc, double ahc, double rtc) {
		double[] descriptorValues = new double[3];
		String[] descriptorDescriptions = new String[3];
		String[] descriptorShortcuts = new String[3];

		descriptorValues[0] = atc;
		descriptorShortcuts[0] = "ATC";
		descriptorDescriptions[0] = "Avg. Transition Compl.";
		descriptorValues[1] = ahc;
		descriptorShortcuts[1] = "ACC";
		descriptorDescriptions[1] = "Avg. Chord Compl.";
		descriptorValues[2] = rtc;
		descriptorShortcuts[2] = "RTC";
		descriptorDescriptions[2] = "Rel. Transition Compl.";

		drawColumnChart(g, descriptorValues, descriptorShortcuts, descriptorDescriptions, "Complexity results", "", "", Color.BLACK);
	}

	/* Analysis components */

	/**
	 * Draws segment of relative length from the client length, using the moving cursor
	 * @param g [Graphics] main Graphics object
	 * @param length [length] relative length, e.g. 0.2 for 20%-filled segment
	 */
	private void drawSegment(Graphics g, double length, Color color) {
		int widthInPixels = (int) ((double) this.getWidth() * length);

		g.setColor(color);
		g.fillRect((int) cursor.getX(), (int) cursor.getY(), widthInPixels, this.getHeight());
		cursor.move((int) cursor.getX() + widthInPixels, (int) cursor.getY());
	}

	/**
	 * Draws simple column chart using JFreeChart
	 * @param g [Graphics] main Graphics object
	 * @param descriptorValues [int[]] descriptor values to plot
	 * @param descriptorDescriptions [String[]] descriptions of descriptors
	 * @param descriptorShortcuts [String[]] names of descriptors
	 * @param chartTitle [String] name of the chart
	 * @param xAxisTitle [String] name of X axis
	 * @param yAxisTitle [String] name of Y axis
	 * @param gridLinePaint [Color] color of the line paint
	 */
	private void drawColumnChart(Graphics g, double[] descriptorValues, String[] descriptorShortcuts, String[] descriptorDescriptions, String chartTitle, String xAxisTitle, String yAxisTitle, Color gridLinePaint) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < descriptorValues.length; i++) {
			dataset.setValue(descriptorValues[i], descriptorDescriptions[i], descriptorShortcuts[i]);
		}
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisTitle, yAxisTitle,dataset, PlotOrientation.VERTICAL, true, true, true);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		categoryPlot.setRangeGridlinePaint(gridLinePaint);
		categoryPlot.setBackgroundPaint(Color.WHITE);
		chart.getTitle().setFont(new Font("Sans", 1, 15));
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), false, true, true, true, true, true);
		this.add(chartPanel);
		this.validate();
	}
}