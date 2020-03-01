package com.fratics.precis.fis.base;

import com.fratics.precis.fis.util.PrecisConfigProperties;

//import java.util.ArrayList;


public class MetricList {
	
	private double [] metrics = null;
	private static String [] metricNames;
	private static int numOfMetric = 0;
	private static double [] threshold;
	private static int [] metricIndexInSchema;
	private static boolean isCountMetric = false;
	
	
	public static boolean isCountMetric() {
		return isCountMetric;
	}

	public static void setCountMetric() {
		MetricList.isCountMetric = true;
		metricNames = new String [1];
		metricNames[0] = "Count";
		MetricList.numOfMetric =  1;
		MetricList.metricIndexInSchema = null;
		MetricList.threshold = new double[1];
		MetricList.threshold[0] = PrecisConfigProperties.THRESHOLD;
	}

	public static int[] getMetricIndexInSchema() {
		return metricIndexInSchema;
	}

	public static void setMetricIndexInSchema(int[] metricIndexInSchema) {
		MetricList.metricIndexInSchema = metricIndexInSchema;
	}

	public static double[] getThreshold() {
		return threshold;
	}

	public static void setThreshold(double[] threshold) {
		MetricList.threshold = threshold;
	}
	
	public static int getNumOfMetric() {
		return MetricList.numOfMetric;
	}

	public static void setNumOfMetric(int numOfMetric) {
		MetricList.numOfMetric = numOfMetric;
	}

	public MetricList() {
		if (metrics == null)
			metrics = new double[MetricList.numOfMetric];
		if (MetricList.threshold == null) {
			MetricList.threshold = new double[MetricList.numOfMetric];
		}
	}
	
	public MetricList(int numOfMetric) {
		if (metrics == null) metrics = new double[numOfMetric];
		if (MetricList.threshold == null) {
			MetricList.threshold = new double[numOfMetric];
		}
	}
	public static void setMetricNames(String [] names) {
		MetricList.metricNames = names;
	}
	
	public void updateMetrics (int index, double value) {
		if (index > metricNames.length - 1) {
			//throw Exception
		}
		metrics[index] += value;
	}
	public void updateMetrics (double [] values) {
		int index = 0;
		for (double d: values) {
			metrics[index] += d;
			index++;
		}
	}
	
	public void updateMetrics (MetricList ml) {
		int index = 0;
		for (double d: ml.metrics) {
			metrics[index] += d;
			index++;
		}
	}
	public static int getMetricListSize() {
		return metricNames.length;
	}
	public void incrementMetrics() {
		for (int i = 0; i < metrics.length; i++) {
			metrics[i]++;
		}
	}
	
	public boolean isThresholdSatisfied() {
		boolean ret = false;
		for (int i = 0; i < MetricList.numOfMetric; i++) {
			if (MetricList.threshold[i] > 0 && metrics[i] >= MetricList.threshold[i]) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public static String getMetricNamesConcat() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < MetricList.metricNames.length - 1; i++) {
			sb.append(MetricList.metricNames[i]);
			sb.append(PrecisConfigProperties.OUTPUT_RECORD_SEPERATOR_METRIC);
		}
		sb.append(MetricList.metricNames[MetricList.metricNames.length - 1]);
		return sb.toString();
	}
	
	private static StringBuilder sb_thresholdStr = new StringBuilder();
	public static String getThresholdsString () {
		if (MetricList.sb_thresholdStr.length() == 0) {
			for (double d : MetricList.threshold) {
				MetricList.sb_thresholdStr.append(d);
				MetricList.sb_thresholdStr.append(",");
			}
			MetricList.sb_thresholdStr.deleteCharAt(MetricList.sb_thresholdStr.length() - 1);
		}
		return MetricList.sb_thresholdStr.toString();
	}
	
	public static boolean isMetricIndexPresentAndThresholdSet() {
		boolean ret = (MetricList.metricNames.length > 0)?true:false;
		ret = ret && (MetricList.threshold.length > 0)?true:false;
		ret = ret && (MetricList.isCountMetric()==true || ((MetricList.metricIndexInSchema.length > 0)?true:false));
		ret = ret && (MetricList.numOfMetric > 0)?true:false;
		boolean ret2 = false;
		for (double d : MetricList.threshold ) {
			if (d > 0) {
				ret2 = true;
				break;
			}
		}
		ret = ret && ret2;
		return ret;
	}
	
	public static MetricList clone(MetricList ml) {
		MetricList ret = new MetricList();
		int i = 0;
		for (double d : ml.metrics) {
			ret.metrics[i] = d;
			i++;
		}
		return ret;
	}
//	public static int getIndexInLine(int i) {
//		return MetricList.metricIndexInSchema[i];
//	}
	static StringBuilder sb_ = new StringBuilder();
	public String toString() {
		sb_.delete(0, sb_.length());
		for (int i = 0; i < this.metrics.length - 1; i++) {
			sb_.append(metrics[i]);
			sb_.append(PrecisConfigProperties.OUTPUT_RECORD_SEPERATOR_METRIC);
		}
		sb_.append(metrics[this.metrics.length - 1]);
		return sb_.toString();
	}
}
