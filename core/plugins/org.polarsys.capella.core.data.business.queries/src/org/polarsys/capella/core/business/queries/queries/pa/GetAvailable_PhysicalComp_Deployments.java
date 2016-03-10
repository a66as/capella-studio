/*******************************************************************************
 * Copyright (c) 2006, 2016 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.core.business.queries.queries.pa;

import java.util.ArrayList;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.AbstractDeploymentLink;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalNode;
import org.polarsys.capella.core.data.pa.deployment.TypeDeploymentLink;
import org.polarsys.capella.core.model.helpers.PhysicalComponentExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_PhysicalComp_Deployments extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (element instanceof PhysicalComponent) {
			PhysicalComponent pc = (PhysicalComponent) element;
			List<CapellaElement> candidates = getRule_MQRY_PC_Deployments_11(pc);
			for (CapellaElement capellaElement : candidates) {
				if (!PhysicalComponentExt.isDeployedOn(pc, (PhysicalComponent) capellaElement)) {
					availableElements.add(capellaElement);
				}
			}
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		availableElements.remove(element.eContainer());
		availableElements.remove(element);
		availableElements.removeAll(getCurrentElements(element, false));
		return availableElements;
	}

	/** 
	 * get all the PC within current PC's SystemEngineering if currentPC is
	 * not of PhysicalNode, it can not deploy PhysicalNode. Other cases are
	 * possible
	 * @param currentPCactual element
	 * @return all PC within currentPC's SystemEngineering
	 */
	private List<CapellaElement> getRule_MQRY_PC_Deployments_11(PhysicalComponent currentPC) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		List<PhysicalComponent> comps = SystemEngineeringExt.getAllPhysicalComponents(currentPC);
		for (PhysicalComponent physicalComponent : comps) {
			if (!(currentPC instanceof PhysicalNode)) {
				if (!(physicalComponent instanceof PhysicalNode)) {
					availableElements.add(physicalComponent);
				}
			} else {
				availableElements.add(physicalComponent);
			}
		}
		return availableElements;
	}

	/** 
	 */
	public List<CapellaElement> getCurrentElements(CapellaElement element, boolean onlyGenerated) {
		List<CapellaElement> currentElements = new ArrayList<CapellaElement>();
		if (element instanceof PhysicalComponent) {
			PhysicalComponent pc = (PhysicalComponent) element;
			for (AbstractDeploymentLink abstractDeployment : pc.getDeploymentLinks()) {
				if (abstractDeployment instanceof TypeDeploymentLink) {
					currentElements.add(abstractDeployment.getDeployedElement());
				}
			}
		}
		return currentElements;
	}

}