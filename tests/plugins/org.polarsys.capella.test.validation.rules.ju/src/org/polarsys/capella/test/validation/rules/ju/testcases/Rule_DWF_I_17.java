/*******************************************************************************
 * Copyright (c) 2006, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.test.validation.rules.ju.testcases;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.polarsys.capella.core.data.cs.CsPackage;
import org.polarsys.capella.test.framework.api.OracleDefinition;

/**
 * @author Erwan Brottier
 */
public class Rule_DWF_I_17 extends ValidationRuleTestCase {

	@Override
	protected String getTestProjectName() {
		return "Project_validation8"; //$NON-NLS-1$
	}

	@Override
	protected EClass getTargetedEClass() {
		return CsPackage.Literals.INTERFACE;
	}

	@Override
	protected String getRuleID() {
		return "org.polarsys.capella.core.data.cs.validation.DWF_I_17"; //$NON-NLS-1$
	}

	@Override
	protected List<OracleDefinition> getOracleDefinitions() {
		return Arrays.asList(new OracleDefinition [] {
				new OracleDefinition("1b331828-2dba-430f-8687-37910b65f5f6", 1), //$NON-NLS-1$
			});
	}

  public void testRule_DWF_I_17() throws Exception {
    test();
  }
}