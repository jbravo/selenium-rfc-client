package org.bitmarte.architecture.utils.testingframework.selenium.validators;

import org.bitmarte.architecture.utils.testingframework.selenium.beans.Config;
import org.bitmarte.architecture.utils.testingframework.selenium.beans.Plan;
import org.bitmarte.architecture.utils.testingframework.selenium.validators.exceptions.ValidatorException;
import org.bitmarte.architecture.utils.testingframework.selenium.validators.impl.ConfigValidator;
import org.bitmarte.architecture.utils.testingframework.selenium.validators.impl.PlanValidator;

/**
 * @author bitmarte
 *
 */
public class ValidatorHandler {

	public static I_Validator execute(Object inValidation) throws Exception {
		if (inValidation instanceof Config) {
			return new ConfigValidator((Config) inValidation);
		}
		if (inValidation instanceof Plan) {
			return new PlanValidator((Plan) inValidation);
		}

		throw new ValidatorException("Unknown object type: " + inValidation.getClass().getName());
	}
}
