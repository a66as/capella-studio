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

import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.Interface;
import org.polarsys.capella.core.data.cs.InterfacePkg;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.information.Port;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.oa.OperationalAnalysis;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.model.helpers.CapellaElementExt;
import org.polarsys.capella.core.model.helpers.InterfaceExt;
import org.polarsys.capella.core.model.helpers.InterfacePkgExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;
import org.polarsys.capella.core.model.utils.CapellaLayerCheckingExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_ComponentPort_RequiredInterfaces extends AbstractQuery {

  @Override
  public List<Object> execute(Object input, IQueryContext context) {
    CapellaElement element = (CapellaElement) input;
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
    List<CapellaElement> tempAvailableElements = new ArrayList<CapellaElement>(1);
    SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element);
    if (element instanceof Port) {
      Port ele = (Port) element;
      tempAvailableElements.addAll(getRule_MQRY_Port_ProvidedInterfaces_11(systemEngineering, ele));
      // if port is in physical layer
      // filter all the interface realized by physical interface
      if (CapellaLayerCheckingExt.isInPhysicalLayer(ele)) {
        availableElements.addAll(InterfaceExt.filterLCRealizedInterfaces(tempAvailableElements));
      } else {
        availableElements = tempAvailableElements;
      }

    }
    availableElements = ListExt.removeDuplicates(availableElements);
    availableElements.remove(element);
    return (List) availableElements;
  }

  // same level
  private List<CapellaElement> getRule_MQRY_Port_ProvidedInterfaces_11(SystemEngineering systemEng_p, Port element_p) {
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
    BlockArchitecture arch = null;
    arch = SystemEngineeringExt.getRootBlockArchitecture(element_p);

    availableElements.addAll(getElementsFromBlockArchitecture(arch, element_p));

    // For the layer visibility
    if (!(arch instanceof OperationalAnalysis)) {
      availableElements.addAll(getRule_MQRY_Port_ProvidedInterfaces_11_1(systemEng_p, element_p));
    }

    return availableElements;
  }

  /*
   * For the layer visibility
   */
  private List<CapellaElement> getRule_MQRY_Port_ProvidedInterfaces_11_1(SystemEngineering systemEng_p, Port element_p) {
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
    BlockArchitecture arch = null;
    arch = SystemEngineeringExt.getRootBlockArchitecture(element_p);

    OperationalAnalysis oa = SystemEngineeringExt.getOwnedOperationalAnalysis(systemEng_p);
    if (null != oa) {
      availableElements.addAll(getElementsFromBlockArchitecture(oa, element_p));
    } else {
      SystemAnalysis ca = SystemEngineeringExt.getOwnedSystemAnalysis(systemEng_p);
      availableElements.addAll(getElementsFromBlockArchitecture(ca, element_p));
    }

    if (null != arch) {
      if (((null != oa) && (arch instanceof LogicalArchitecture)) || (arch instanceof PhysicalArchitecture)) {
        SystemAnalysis ctxArch = SystemEngineeringExt.getOwnedSystemAnalysis(systemEng_p);
        availableElements.addAll(getElementsFromBlockArchitecture(ctxArch, element_p));
      }
      if ((arch instanceof PhysicalArchitecture)) {
        LogicalArchitecture logArch = SystemEngineeringExt.getOwnedLogicalArchitecture(systemEng_p);
        availableElements.addAll(getElementsFromBlockArchitecture(logArch, element_p));
      }
    }
    return availableElements;
  }

  /**
   * All the Interfaces contained by the Interface Package (and all of its sub-packages) of the current Element's parent (can be a Component, a Component
   * Architecture Decomposition package, or a Component Architecture root package).
   */
  private List<CapellaElement> getElementsFromBlockArchitecture(BlockArchitecture arch_p, Port element_p) {
    List<CapellaElement> availableElements = new ArrayList<CapellaElement>();

    InterfacePkg interfacePkg = arch_p.getOwnedInterfacePkg();

    if (interfacePkg != null) {
      for (Interface inter : InterfacePkgExt.getAllInterfaces(interfacePkg)) {
        if (inter != null) {
          availableElements.add(inter);
        }
      }
    }

    for (Component cpnt : CapellaElementExt.getComponentHierarchy(element_p)) {
      InterfacePkg intPkg = cpnt.getOwnedInterfacePkg();
      if (null != intPkg) {
        EObject container = element_p.eContainer();
        if ((container != null) && (container instanceof Component)) {
          availableElements.addAll(InterfacePkgExt.getAllInterfacesFiltered(intPkg, cpnt, false));
        }
      }
    }

    return availableElements;
  }
}