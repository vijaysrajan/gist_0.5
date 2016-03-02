package com.fratics.precis.fis.sanitation.rules;

import com.fratics.precis.fis.base.FieldObject;
import com.fratics.precis.fis.base.ValueObject;

public class UniqueValueRule extends SanitationRuleBase {
    public void applyRule(ValueObject vo) throws Exception {
	this.setRuleName("UniqueValuesRule");
	long numOfLines = vo.inputObject.getNoOfLines();
	FieldObject[] fo = vo.inputObject.getFieldObjects();
	for (int i = 0; i < fo.length; i++) {
	    if (fo[i].getNumberOfUniques() == numOfLines) {
		vo.resultObject.loadResult(new SingleRuleResult(ruleName, true,
			i));
	    } else {
		vo.resultObject.loadResult(new SingleRuleResult(ruleName,
			false, i));
	    }
	}
    }
}
