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
package org.polarsys.capella.test.model.ju.testcases.intermodelInconsistencies;

import java.util.Arrays;
import java.util.List;

import org.polarsys.capella.common.ef.command.AbstractCommand;
import org.polarsys.capella.common.ef.command.AbstractReadWriteCommand;
import org.polarsys.capella.common.helpers.TransactionHelper;
import org.polarsys.capella.core.data.capellamodeller.Project;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.information.Class;
import org.polarsys.capella.core.data.information.DataPkg;
import org.polarsys.capella.core.libraries.model.CapellaModel;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.test.model.ju.CheckInterModelInconsistencyDetectionTestCase;

/**
 * @author Erwan Brottier
 */
public class CheckDependencyViolationOneLevel extends CheckInterModelInconsistencyDetectionTestCase {

	@Override
	protected List<String> getProjectNamesToLoad() {		
		return Arrays.asList(new String [] {"PROJECT", "LIB"});  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	public void test() throws Exception {
		setDetectorActive(true);
		
		CapellaModel modelA = (CapellaModel) getLoadedCapellaModel("PROJECT"); //$NON-NLS-1$
		Project projectA = modelA.getProject(modelA.getEditingDomain());
		SystemEngineering systemA = getSystemEngineering(projectA);		
		BlockArchitecture logArchA = SystemEngineeringExt.getOwnedLogicalArchitecture(systemA);		
		final DataPkg dataPckA = logArchA.getOwnedDataPkg();
		final Class class4 = getClassNamed("Class 4", dataPckA); //$NON-NLS-1$
		final Class class5 = getClassNamed("Class 5", dataPckA); //$NON-NLS-1$
		
		CapellaModel modelB = (CapellaModel) getLoadedCapellaModel("LIB"); //$NON-NLS-1$
		Project projectB = modelB.getProject(modelA.getEditingDomain());
		SystemEngineering systemB = getSystemEngineering(projectB);		
		BlockArchitecture logArchB = SystemEngineeringExt.getOwnedLogicalArchitecture(systemB);		
		final DataPkg dataPckB = logArchB.getOwnedDataPkg();
		
		// check must accept that
		AbstractCommand command = new AbstractReadWriteCommand() {			
			@Override
			public void run() {
				dataPckB.getOwnedClasses().add(class4);
			}		
		};
		TransactionHelper.getExecutionManager(projectA).execute(command);
		assertFalse(command.isRolledBack());
		
		// we undo the modification
		command = new AbstractReadWriteCommand() {			
			@Override
			public void run() {
				dataPckA.getOwnedClasses().add(class4);				
			}
		};
		TransactionHelper.getExecutionManager(projectA).execute(command);
		assertFalse(command.isRolledBack());
				
		// check must prevent that		
		command = new AbstractReadWriteCommand() {			
			@Override
			public void run() {
				dataPckB.getOwnedClasses().add(class5);				
			}
		};
		TransactionHelper.getExecutionManager(projectA).execute(command);
		assertTrue(command.isRolledBack());
		
		// check does not must prevent that if detector is not active
		setDetectorActive(false);
		command = new AbstractReadWriteCommand() {			
			@Override
			public void run() {
				dataPckB.getOwnedClasses().add(class5);				
			}
		};
		TransactionHelper.getExecutionManager(projectA).execute(command);
		assertFalse(command.isRolledBack());
	}

}