package com.fratics.precis.fis.schema;

import com.fratics.precis.exception.PrecisException;
import com.fratics.precis.fis.base.MetricList;
import com.fratics.precis.fis.base.PrecisProcessor;
import com.fratics.precis.fis.base.PrecisStream;
import com.fratics.precis.fis.base.Schema;
import com.fratics.precis.fis.base.ValueObject;
import com.fratics.precis.fis.util.PrecisConfigProperties;

import java.util.ArrayList;

public class PrecisSchemaProcessor extends PrecisProcessor {
    private PrecisStream ps = null;

    public PrecisSchemaProcessor(PrecisStream ps) {
        this.ps = ps;
    }

    public boolean initialize() throws Exception {
        return this.ps.initialize();
    }

    public boolean unInitialize() throws Exception {
        return this.ps.unInitialize();
    }
    
    

    
    private static double [] toPrimitive(Double [] D) {
    		int sz = D.length;
    		double [] ret = new double[sz];
    		int i = 0;
    		for (Double d : D) {
    			ret[i] = d.doubleValue();
    			i++;
    		}
    		return ret;
    }
    
    private static int [] toPrimitive(Integer [] I) {
		int sz = I.length;
		int [] ret = new int[sz];
		int i = 0;
		for (Integer ii : I) {
			ret[i] = ii.intValue();
			i++;
		}
		return ret;
    }


    /*
     * (non-Javadoc)
     * @see com.fratics.precis.fis.base.PrecisProcessor#process(com.fratics.precis.fis.base.ValueObject)
     * 
     * 
     *	source:d:string:t
		estimated_usage_bins:d:string:t
		city:d:string:t
		zone:d:string:t
		zone_demand_popularity:d:string:t
		dayType:d:string:t
		sla:d:string:t
		booking_type:d:string:t
		CATEGORY:d:string:t
		Good_cnt:m:double:t:20
		Bad_cnt:m:double:t:20
		Total_cnt:m:double:t:30
     */
    
    
    public boolean process(ValueObject o) throws Exception {
        String[] str = null;
        Schema schema = new Schema();
        int i = 0;
        int metricCount = 0;
        ArrayList<String> metricNames = new ArrayList<String>();
        ArrayList<Double> supportThresholdList = new ArrayList<Double>();
        ArrayList<Integer> metricIndex = new ArrayList<Integer>();
        while ((str = ps.readStream()) != null) {
            if (str[3].equalsIgnoreCase("t")) {
                Schema.FieldType fieldType;
                if (str[1].equalsIgnoreCase("d")) {
                    fieldType = Schema.FieldType.DIMENSION;
                } else {
                    fieldType = Schema.FieldType.METRIC;
                    metricNames.add(str[0]);
                    double support = 0;
                    if (str.length > 4) {
                    	support = Double.parseDouble(str[4]);
                    }
                    supportThresholdList.add(support);
                    metricIndex.add(i);
                    metricCount++;
                }
                schema.addSchemaElement(str[0], i, fieldType);
            }
            i++;
        }
        String [] t2 = metricNames.toArray(new String [metricNames.size()]);
        if (PrecisConfigProperties.IS_COUNT_PRECIS == true) {
	    		if (metricNames.size() > 0) {
	    			throw new PrecisException("Found Metrics Field in Schema for a count gist task.");
	    		}
        		MetricList.setCountMetric();
        } else {
        		o.inputObject.setMetricName(t2);
        		o.inputObject.setNoOfMetrics(metricCount);
        		o.inputObject.setThreshold(PrecisSchemaProcessor.toPrimitive(supportThresholdList.toArray(new Double[supportThresholdList.size()])));
        		o.inputObject.setIndexOfMetrics(PrecisSchemaProcessor.toPrimitive(metricIndex.toArray(new Integer[metricIndex.size()])));
        }
        o.inputObject.loadSchema(schema);
        return true;
    }
}
