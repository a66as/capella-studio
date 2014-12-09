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
package org.polarsys.capella.core.business.queries.queries.epbs;

import java.util.ArrayList;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.epbs.ConfigurationItem;
import org.polarsys.capella.core.data.epbs.EPBSArchitecture;
import org.polarsys.capella.core.data.epbs.EPBSArchitecturePkg;
import org.polarsys.capella.core.data.la.CapabilityRealization;
import org.polarsys.capella.core.model.helpers.CapellaElementExt;
import org.polarsys.capella.core.model.helpers.SystemComponentExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_ConfigurationItem_InvolvedRealization extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * @see org.polarsys.capella.core.business.queries.capellacore.core.business.queries.IBusinessQuery#getAvailableElements(org.polarsys.capella.core.common.model.CapellaElement)
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element_p);
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (null == systemEngineering) {
			return availableElements;
		}
		if (element_p instanceof ConfigurationItem) {
			ConfigurationItem currentCI = (ConfigurationItem) element_p;
			availableElements.addAll(getRule_MQRY_ConfigurationItem_Realizations_11(currentCI, systemEngineering));
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		return availableElements;
	}

	private List<CapellaElement> getRule_MQRY_ConfigurationItem_Realizations_11(ConfigurationItem currentConfigurationItem_p,
			SystemEngineering systemEngineering_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		EPBSArchitecturePkg epbsArchPkg = SystemEngineeringExt.getEPBSArchitecturePkg(systemEngineering_p);
		if (null != epbsArchPkg) {
			for (EPBSArchitecture epbsArch : epbsArchPkg.getOwnedEPBSArchitectures()) {
				for (CapabilityRealization realization : CapellaElementExt.getAllCapabilityRealizationInvolvedWith(epbsArch)) {
					if (SystemComponentExt.isRealizationInvolved(currentConfigurationItem_p, realization))
						continue;
					availableElements.add(realization);
				}
			}
		}
		EPBSArchitecture epbsArch = SystemEngineeringExt.getEPBSArchitecture(systemEngineering_p);
		if (null != epbsArch) {
			for (CapabilityRealization realization : CapellaElementExt.getAllCapabilityRealizationInvolvedWith(epbsArch)) {
				if (SystemComponentExt.isRealizationInvolved(currentConfigurationItem_p, realization))
					continue;
				availableElements.add(realization);
			}
		}
		return availableElements;
	}

}