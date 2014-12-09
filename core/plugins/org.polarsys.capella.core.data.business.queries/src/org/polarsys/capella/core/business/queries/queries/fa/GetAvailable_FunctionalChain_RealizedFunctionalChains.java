/*******************************************************************************
 * Copyright (c) 2006, 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.core.business.queries.queries.fa;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.fa.FunctionalChain;
import org.polarsys.capella.core.model.helpers.BlockArchitectureExt;
import org.polarsys.capella.core.model.helpers.FunctionalChainExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_FunctionalChain_RealizedFunctionalChains extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * @see org.polarsys.capella.core.business.queries.capellacore.IBusinessQuery#getAvailableElements(org.polarsys.capella.core.data.capellacore.CapellaElement)
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		BlockArchitecture rootBlockArchitecture = BlockArchitectureExt.getRootBlockArchitecture(element_p);
		if (null != rootBlockArchitecture) {
			EList<BlockArchitecture> allocatedArchitectures = rootBlockArchitecture.getAllocatedArchitectures();
			for (BlockArchitecture blockArchitecture : allocatedArchitectures) {
				if (null != blockArchitecture) {
					List<FunctionalChain> allFunctionalChains = FunctionalChainExt.getAllFunctionalChains(blockArchitecture);
					if (!allFunctionalChains.isEmpty()) {
						availableElements.addAll(allFunctionalChains);
					}
				}
			}
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		availableElements.remove(element_p);
		return availableElements;
	}

}