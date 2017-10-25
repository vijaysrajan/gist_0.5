package com.fratics.precis.util;

import com.fratics.precis.fis.base.PrecisBase;
import com.fratics.precis.fis.util.PrecisConfigProperties;

public class ThresholdCalculator extends PrecisBase {
    private static ThresholdCalculator thresholdCalculator;
    private double totalValue = 0;
    private double thresholdValues[] = new double[256]; //Max 255, ignore 0th slot

    private void loadThresholds(String formulae){
        //4:1,5:2,6:3,7:4,8:5
        int key = 0;
        double val = 0;
        int prevKey = 0;
        double prevVal = 0;
        String [] fields =  formulae.split(",");
        for(int i = 0; i < fields.length; i++){
            String [] tmp = fields[i].split(":");
            key = Integer.parseInt(tmp[0]);
            val = Double.parseDouble(tmp[1]);
            thresholdValues[key] = val;
            if(prevKey + 1 != key){
                for(int j = prevKey + 1; j < key; j++)
                    thresholdValues[j] = prevVal;
            }
            prevKey = key;
            prevVal = val;
        }
        for(prevKey++; prevKey < 255; prevKey++){
            thresholdValues[prevKey] = prevVal;
        }
    }

    public boolean initialize()  {
        if(PrecisConfigProperties.USE_THRESHOLD_GEN){
            loadThresholds(PrecisConfigProperties.THRESHOLD_GEN_FORMULA_AFTER_LEVEL_3);
        }
        return true;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        System.err.println("Total Metric Value for Calculation of Threshold ::" +  totalValue);
        this.totalValue = totalValue;
    }

    public static ThresholdCalculator getInstance() {
        if(thresholdCalculator == null) {
            thresholdCalculator = new ThresholdCalculator();
        }
        return thresholdCalculator;
    }

    public double getThresholdValue(int currentStage){
        double value = getThreshold(currentStage);
        System.err.println("Threshold for Stage " + currentStage + " is " + value);
        return value;
    }

    private double getThreshold(int currentStage){
        if(!PrecisConfigProperties.USE_THRESHOLD_GEN) return PrecisConfigProperties.THRESHOLD;
        if(currentStage <= 3){
            if(PrecisConfigProperties.USE_THRESHOLD_PERCENTAGE_UPTO_LEVEL_3){
                //generate percentage.
                return (PrecisConfigProperties.THRESHOLD_UPTO_LEVEL_3 * totalValue / 100 );
            }
            else{
                return PrecisConfigProperties.THRESHOLD_UPTO_LEVEL_3;
            }
        }else{
            if(PrecisConfigProperties.USE_THRESHOLE_PERCENTAGE_AFTER_LEVEL_3){
                //System.err.println("1:" + thresholdValues[currentStage] + " 2: " + totalValue);
                return (thresholdValues[currentStage] * totalValue / 100);
            }else{
                return thresholdValues[currentStage];
            }
        }
    }

    public static void main(String [] args) throws Exception{
        PrecisConfigProperties.init();
        ThresholdCalculator  thresholdCalculator = ThresholdCalculator.getInstance();
        thresholdCalculator.initialize();
        thresholdCalculator.setTotalValue(100000);
        System.err.println(thresholdCalculator.getThresholdValue(1));
        System.err.println(thresholdCalculator.getThresholdValue(2));
        System.err.println(thresholdCalculator.getThresholdValue(3));
        System.err.println(thresholdCalculator.getThresholdValue(4));
        System.err.println(thresholdCalculator.getThresholdValue(5));
        System.err.println(thresholdCalculator.getThresholdValue(6));
        System.err.println(thresholdCalculator.getThresholdValue(7));
        System.err.println(thresholdCalculator.getThresholdValue(8));
        System.err.println(thresholdCalculator.getThresholdValue(9));
    }
}
