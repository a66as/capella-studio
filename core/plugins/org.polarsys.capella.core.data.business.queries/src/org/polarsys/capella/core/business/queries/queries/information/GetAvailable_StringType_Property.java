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
package org.polarsys.capella.core.business.queries.queries.information;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.polarsys.capella.common.data.modellingcore.AbstractType;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellacore.Classifier;
import org.polarsys.capella.core.data.capellacore.Feature;
import org.polarsys.capella.core.data.capellacore.ReuseLink;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.epbs.EPBSArchitecture;
import org.polarsys.capella.core.data.information.DataPkg;
import org.polarsys.capella.core.data.information.Property;
import org.polarsys.capella.core.data.information.communication.Exception;
import org.polarsys.capella.core.data.information.communication.Message;
import org.polarsys.capella.core.data.information.communication.Signal;
import org.polarsys.capella.core.data.information.datatype.DataType;
import org.polarsys.capella.core.data.information.datatype.StringType;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.oa.OperationalAnalysis;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.sharedmodel.GenericPkg;
import org.polarsys.capella.core.data.sharedmodel.SharedPkg;
import org.polarsys.capella.core.model.helpers.DataPkgExt;
import org.polarsys.capella.core.model.helpers.DataValueExt;
import org.polarsys.capella.core.model.helpers.GenericPkgExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_StringType_Property extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * @see org.polarsys.capella.core.business.queries.capellacore.common.ui.business.queries.IBusinessQuery#getAvailableElements(org.polarsys.capella.core.common.model.CapellaElement)
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element_p);
		boolean isElementFromSharedPkg = false;
		if (null == systemEngineering) {
			SharedPkg sharedPkg = SystemEngineeringExt.getSharedPkg(element_p);
			for (ReuseLink link : sharedPkg.getReuseLinks()) {
				if (SystemEngineeringExt.getSystemEngineering(link) != null) {
					systemEngineering = SystemEngineeringExt.getSystemEngineering(link);
					isElementFromSharedPkg = true;
					break;
				}
			}
			if (systemEngineering == null)
				return availableElements;
		}
		if (element_p instanceof StringType) {
			StringType currentBooleanType = (StringType) element_p;
			if (!isElementFromSharedPkg) {
				availableElements.addAll(getRule_MQRY_BooleanValue_Type_11(currentBooleanType));
			}
			availableElements.addAll(getRule_MQRY_BooleanValue_Type_13(currentBooleanType, systemEngineering));
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		return availableElements;
	}

	private List<CapellaElement> getRule_MQRY_BooleanValue_Type_11(StringType currentBooleanType_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		List<DataPkg> dataPkgList = DataValueExt.getDataPkgsFromParentHierarchy(currentBooleanType_p);
		for (DataPkg dataPkg : dataPkgList) {
			getAllPropertyFromDataPkg(availableElements, dataPkg, currentBooleanType_p);
		}
		availableElements.addAll(getRule_MQRY_BooleanValue_Type_11_1(currentBooleanType_p));
		return availableElements;
	}

	private List<CapellaElement> getRule_MQRY_BooleanValue_Type_13(StringType currentBooleanType_p, SystemEngineering systemEngineering_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		for (SharedPkg sharedPkg : SystemEngineeringExt.getSharedPkgs(systemEngineering_p)) {
			GenericPkg pkg = sharedPkg.getOwnedGenericPkg();
			if (pkg != null) {
				for (org.polarsys.capella.core.data.information.Class object : GenericPkgExt.getAllClasses(pkg)) {
					getAllPropertiesFromClassifier(availableElements, object, currentBooleanType_p);
				}
				for (Exception object : GenericPkgExt.getAllExceptions(pkg)) {
					getAllPropertiesFromClassifier(availableElements, object, currentBooleanType_p);
				}
				for (Message object : GenericPkgExt.getAllMessages(pkg)) {
					getAllPropertiesFromClassifier(availableElements, object, currentBooleanType_p);
				}
				for (Signal object : GenericPkgExt.getAllSignals(pkg)) {
					getAllPropertiesFromClassifier(availableElements, object, currentBooleanType_p);
				}
			}
		}
		return availableElements;
	}

	private void getAllPropertyFromDataPkg(List<CapellaElement> availableElements_p, DataPkg dataPkg_p, DataType type) {
		if (null != dataPkg_p) {
			for (CapellaElement dataType : DataPkgExt.getAllClassifierFromDataPkg(dataPkg_p)) {
				if (dataType instanceof Classifier) {
					getAllPropertiesFromClassifier(availableElements_p, (Classifier) dataType, type);
				}
			}
		}
	}

	private List<CapellaElement> getRule_MQRY_BooleanValue_Type_11_1(StringType currentBooleanType_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		BlockArchitecture arch = DataPkgExt.getRootBlockArchitecture(currentBooleanType_p);
		SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(currentBooleanType_p);
		OperationalAnalysis oa = SystemEngineeringExt.getOwnedOperationalAnalysis(systemEngineering);
		if (null != oa) {
			availableElements.addAll(getElementsFromBlockArchitecture(oa, currentBooleanType_p));
		} else {
			SystemAnalysis ca = SystemEngineeringExt.getOwnedSystemAnalysis(systemEngineering);
			availableElements.addAll(getElementsFromBlockArchitecture(ca, currentBooleanType_p));
		}
		if (arch != null) {
			if (null != oa && (arch instanceof LogicalArchitecture) || (arch instanceof PhysicalArchitecture) || (arch instanceof EPBSArchitecture)) {
				SystemAnalysis ctxArch = SystemEngineeringExt.getOwnedSystemAnalysis(systemEngineering);
				availableElements.addAll(getElementsFromBlockArchitecture(ctxArch, currentBooleanType_p));
			}
			if ((arch instanceof PhysicalArchitecture) || (arch instanceof EPBSArchitecture)) {
				LogicalArchitecture logArch = SystemEngineeringExt.getOwnedLogicalArchitecture(systemEngineering);
				availableElements.addAll(getElementsFromBlockArchitecture(logArch, currentBooleanType_p));
			}
			if ((arch instanceof EPBSArchitecture)) {
				PhysicalArchitecture physArch = SystemEngineeringExt.getOwnedPhysicalArchitecture(systemEngineering);
				availableElements.addAll(getElementsFromBlockArchitecture(physArch, currentBooleanType_p));
			}
		}
		return availableElements;
	}

	private void getAllPropertiesFromClassifier(List<CapellaElement> availableElements_p, Classifier classifier_p, DataType type) {
		EList<Feature> ownedFeatures = classifier_p.getOwnedFeatures();
		for (Feature feature : ownedFeatures) {
			if (feature instanceof Property) {
				AbstractType abstractType = ((Property) feature).getAbstractType();
				if (null != abstractType)
					if (abstractType instanceof StringType) {
						availableElements_p.add(feature);
					}
			}
		}
	}

	private List<CapellaElement> getElementsFromBlockArchitecture(BlockArchitecture arch, DataType type) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		if (null != arch) {
			DataPkg dataPkg = DataPkgExt.getDataPkgOfBlockArchitecture(arch);
			getAllPropertyFromDataPkg(availableElements, dataPkg, type);
		}
		return availableElements;
	}

}