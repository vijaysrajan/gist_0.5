package com.fratics.precis.fis.base;

import com.fratics.precis.fis.base.Schema.SchemaElement;
import com.fratics.precis.fis.feed.BaseFeedPartitioner;
import com.fratics.precis.fis.feed.dimval.DimValIndex;
import com.fratics.precis.fis.util.BitSet;

import java.io.Serializable;
import java.util.*;

/*
 * The Input Object is the Abstract Base Class for all the Input Objects in this application.
 * The is the main holder class on which all the data is captured from the input feed.
 * 
 * The input object will be passed from one flow processor to the other in sequence of data massaging steps.
 * The Input feed's state alters the input object and gets captured as we progress in applying all our data 
 * massaging processors.
 *  
 */

public abstract class InputObject implements Serializable {

    private static final long serialVersionUID = -6326248205037370805L;
    // Current Precis Stage's Candidate Partition,
    // The Candidates as partitioned & grouped in a arraylist by a bitset
    // object.
    // The bitset object is derived from the first (n-1) dim bits set & (n-1)
    // value bits set.
    public HashMap<BitSet, ArrayList<BaseCandidateElement>> currCandidatePart = new HashMap<BitSet, ArrayList<BaseCandidateElement>>();
    // Previous Precis Stage's Candidates Partition, the current candidate
    // Partition
    // is generated from the previous candidate partition.
    public HashMap<BitSet, ArrayList<BaseCandidateElement>> prevCandidatePart = new HashMap<BitSet, ArrayList<BaseCandidateElement>>();
    // Current Precis Stage's Candidates List.
    public HashSet<BitSet> currCandidateSet = new HashSet<BitSet>();
    // Previous Precis Stage's Candidate List.
    public HashSet<BitSet> prevCandidateSet = new HashSet<BitSet>();
    // Precis First Stage Candidate Partition.
    public HashMap<BitSet, BaseCandidateElement> firstStageCandidates = new HashMap<BitSet, BaseCandidateElement>();
    // Threshold Counter Object
    public TreeMap<String, MutableDouble> thresholdCounter = new TreeMap<String, MutableDouble>();
    // Line Number Value Map.
    public HashMap<Long, MetricList> lineNumberValue = new HashMap<Long, MetricList>();
    // Candidate Line Numbers.
    public HashMap<BitSet, BitSet> candidateLineNumberList = new HashMap<BitSet, BitSet>();
    public int currentStage = -1;
    // No of Dimensions in the Input feed.
    protected int noOfFields = 0;
    // Field Objects represents a column in the data feed.
    protected FieldObject[] fieldObjects = null;
    // the current application is a Count Precis (or) Metric Precis.
    protected boolean countPrecis = true;
    // Partitioned Input Feed.
    protected BaseFeedPartitioner partitioner = null;
    // Metric Field Name.
    protected String [] metricName = null;
    // No of Lines in the data stream (or) flat file.
    private long noOfLines = 0;
    
    
    public int [] getIndexOfMetrics() {
    	return MetricList.getMetricIndexInSchema();
    }
    public void setIndexOfMetrics(int [] idx) {
    	MetricList.setMetricIndexInSchema(idx);
    }
    public int getNoOfMetrics() {
	return MetricList.getNumOfMetric();
    }
    public void setNoOfMetrics(int noOfMetrics) {
	MetricList.setNumOfMetric(noOfMetrics);
    }	
    public double [] getThreshold() {
        return MetricList.getThreshold();
    }

    public void setThreshold(double [] threshold) {
        MetricList.setThreshold(threshold);
    }
    public BaseFeedPartitioner getPartitioner() {
        return partitioner;
    }

    public void setPartitioner(BaseFeedPartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public boolean isCountPrecis() {
        return this.countPrecis;
    }

    private static StringBuilder sb_getMetricName = new StringBuilder(); 
    public String getMetricNamesConcat() {
    		if (InputObject.sb_getMetricName.length() == 0) {
    			sb_getMetricName.append(MetricList.getMetricNamesConcat());
    		} else {
    			sb_getMetricName.toString();
    		}
    		return InputObject.sb_getMetricName.toString();
    }
    
    public String [] getMetricName() {
        return this.metricName;
    }

    public void setMetricName(String [] metricName) {
        this.metricName = metricName ;
        MetricList.setMetricNames(metricName);
    }

    public int getNoOfFields() {
        return noOfFields;
    }

    public void setNoOfFields(int noOfFields) {
        this.noOfFields = noOfFields;
    }

    public FieldObject[] getFieldObjects() {
        return fieldObjects;
    }

    public long getNoOfLines() {
        return this.noOfLines;
    }

    public void setNoOfLines(long noOfLines) {
        this.noOfLines = noOfLines;
    }

    public void incNoOfLines(long inc) {
        noOfLines = noOfLines + inc;
    }

    /*
     *
     */
    public void addLineNumberMetric(long lineNumber, MetricList d) {
        this.lineNumberValue.put(lineNumber, d);
    }

	/*
     * Add a candidate to the Candidate Partition and Candidate List.
	 */

    public void addCandidate(BitSet b) {
        int dimSetBit = b.previousSetBit(DimValIndex.dimMap.size() - 1);
        int valSetBit = b.previousSetBit(DimValIndex.dimMap.size() + DimValIndex.dimValMap.size() - 1);
        BitSet tmp = (BitSet) b.clone();
        tmp.clear(dimSetBit);
        tmp.clear(valSetBit);
        if (currCandidatePart.containsKey(tmp)) {
            currCandidatePart.get(tmp).add(new BaseCandidateElement(b, new double[MetricList.getNumOfMetric()]));
        } else {
            ArrayList<BaseCandidateElement> al = new ArrayList<BaseCandidateElement>();
            al.add(new BaseCandidateElement(b, new double[MetricList.getNumOfMetric()]));
            currCandidatePart.put(tmp, al);
        }
        currCandidateSet.add(b);
    }

	/*
	 * Add bit set & corresponding line numbers.
	 */

    public void addCandidateLineNumber(BitSet b, long lineNumber) {
        if (this.candidateLineNumberList.containsKey(b)) {
            this.candidateLineNumberList.get(b).set((int) lineNumber);
        } else {
            BitSet x = new BitSet((int) this.noOfLines + 100);
            x.set((int) lineNumber);
            this.candidateLineNumberList.put(b, x);
        }
    }

	/*
	 * Add a Fist Stage Candidate.
	 */

    public void addFirstStageCandidateElement(BaseCandidateElement b, long lineNumber) {
        if (this.firstStageCandidates.containsKey(b.getBitSet())) {
            this.firstStageCandidates.get(b.getBitSet()).incrMetricBy(b.getMetric());
        } else {
            this.firstStageCandidates.put(b.getBitSet(), b);
        }
        if (lineNumber > 0)
            addCandidateLineNumber(b.getBitSet(), lineNumber);
    }

	/*
	 * apply base feed on candidates.
	 */

    public void apply(BaseCandidateElement bce) {
        BitSet b = new BitSet();
        ArrayList<BitSet> al = new ArrayList<BitSet>();
        int prevDimBit = bce.getBitSet().previousSetBit(DimValIndex.dimMap.size() - 1);
        int prevDimValBit = bce.getBitSet().previousSetBit(DimValIndex.getPrecisBitSetLength() - 1);

        for (int i = 0; i < this.currentStage; i++) {
            b.set(prevDimBit);
            b.set(prevDimValBit);
            BitSet x = this.candidateLineNumberList.get(b);
            b.clear(prevDimBit);
            b.clear(prevDimValBit);
            al.add(x);
            prevDimBit = bce.getBitSet().previousSetBit(prevDimBit - 1);
            prevDimValBit = bce.getBitSet().previousSetBit(prevDimValBit - 1);
        }

        // al.sort(new BitSetComparator());


        BitSet z = null;
        for (BitSet x : al) {
            if (z == null) {
                z = (BitSet) x.clone();
                continue;
            }
            z.and(x);
        }
        if (this.countPrecis) {
            MetricList ml = new MetricList(1);
            double [] m = new double[1];
            m[0] = z.cardinality();
            ml.updateMetrics(m);
            bce.setMetric(ml);
        } else {
            MetricList ml = new MetricList(MetricList.getNumOfMetric());
            for (int i = z.nextSetBit(0); i != -1; i = z.nextSetBit(i + 1)) {
                ml.updateMetrics(this.lineNumberValue.get((long) i));
            }
            bce.setMetric(ml);
        }
        z = null;
    }

	/*
	 * As a Precis Stage Completes, this method moves Precis Execution context
	 * from the current stage to the Next Stage.
	 */

    public void moveToNextStage() {
        prevCandidatePart = currCandidatePart;
        currCandidatePart = new HashMap<BitSet, ArrayList<BaseCandidateElement>>();
        prevCandidateSet = currCandidateSet;
        currCandidateSet = new HashSet<BitSet>();
    }

	/*
	 * Applies the Threshold on the current stage candidates, keeps the
	 * candidates which has passed the threshold and rejects/removes the fail
	 * over candidates.
	 * 
	 * Returns success if candidates are available for next stage, failure
	 * otherwise.
	 */

    public boolean applyThreshold() {

        if (this.currCandidateSet.size() <= 0)
            return false;

        for (ArrayList<BaseCandidateElement> al : this.currCandidatePart.values()) {
            ArrayList<BaseCandidateElement> removeList = new ArrayList<BaseCandidateElement>();
            for (BaseCandidateElement bce : al) {
                if (!bce.isThresholdSatisfied()) {
                    removeList.add(bce);
                }
            }
            for (BaseCandidateElement bce : removeList) {
                al.remove(bce);
                this.currCandidateSet.remove(bce.getBitSet());
            }
        }
        return (this.currCandidateSet.size() > 0);
    }

	/*
	 * Applies the Schema Object, generated by the PrecisSchemaProcessor on the
	 * input data feed. The schema elements are added to the respective fields
	 * in the field objects.
	 */

    public void loadSchema(Schema sch) {
        // need to alter loadSchema in case of Metrics.
        this.noOfFields = sch.getNoOfFields();
        fieldObjects = new FieldObject[noOfFields];
        SchemaElement[] list = sch.getSchemaList().toArray(new SchemaElement[0]);
        for (int i = 0; i < noOfFields; i++) {
            fieldObjects[i] = new FieldObject();
            fieldObjects[i].setSchemaElement(list[i]);
        }
    }

	/*
	 * The actual input data needs to loaded as per the input feed
	 * characteristics. The feed characteristics, delimiters, definitions,
	 * serialization, deserialization, data formats, block data, sequence data,
	 * encryption formats, field info needs to be taken care in the loadInput()
	 * method by the Deriving Class.
	 * 
	 * So loadInputCharacteristics() method is a abstract method.
	 */

    public abstract void loadInputCharacteristics(Object o) throws Exception;

    /*
     * A checker method to verify data initializations.
     */
    protected abstract boolean isInitialized();

	/*
	 * A simple toString() method defined to provide the current state of the
	 * Precis Execution and its relevent objects contained in the input object.
	 */

    public String toString() {
        return "=====================================================\n" + "\nNo of Records :: " + this.getNoOfLines()
                + "\n\n" + "isCountPrecis :: " + this.isCountPrecis() + "\n\n" + "Field Objects :: "
                + Arrays.toString(fieldObjects) + "\n\n" + "Stages Run :: " + this.currentStage + "\n\n"
                + "DimVal Indexes :: " + DimValIndex.dumpIndexes() + "\n"
                + "=====================================================\n";
    }
}
