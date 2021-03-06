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

package org.polarsys.capella.core.data.helpers.epbs.delegates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.polarsys.capella.core.data.cs.ArchitectureAllocation;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.epbs.EPBSArchitecture;
import org.polarsys.capella.core.data.epbs.EpbsPackage;
import org.polarsys.capella.core.data.epbs.PhysicalArchitectureRealization;
import org.polarsys.capella.core.data.helpers.cs.delegates.BlockArchitectureHelper;
import org.polarsys.capella.core.data.la.CapabilityRealizationPkg;
import org.polarsys.capella.core.data.capellacommon.AbstractCapabilityPkg;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;

public class EpbsArchitectureHelper {
  private static EpbsArchitectureHelper instance;

  private EpbsArchitectureHelper() {
    // do nothing
  }

  public static EpbsArchitectureHelper getInstance() {
    if (instance == null)
      instance = new EpbsArchitectureHelper();
    return instance;
  }

  public Object doSwitch(EPBSArchitecture element, EStructuralFeature feature) {
    Object ret = null;

    if (feature.equals(EpbsPackage.Literals.EPBS_ARCHITECTURE__ALLOCATED_PHYSICAL_ARCHITECTURE_REALIZATIONS)) {
      ret = getAllocatedPhysicalArchitectureRealizations(element);
    } else if (feature.equals(EpbsPackage.Literals.EPBS_ARCHITECTURE__CONTAINED_CAPABILITY_REALIZATION_PKG)) {
      ret = getContainedCapabilityRealizationPkg(element);
    } else if (feature.equals(EpbsPackage.Literals.EPBS_ARCHITECTURE__ALLOCATED_PHYSICAL_ARCHITECTURES)) {
      ret = getAllocatedPhysicalArchitectures(element);
    }

    // no helper found... searching in super classes...
    if (null == ret) {
      ret = BlockArchitectureHelper.getInstance().doSwitch(element, feature);
    }

    return ret;
  }

  protected List<PhysicalArchitectureRealization> getAllocatedPhysicalArchitectureRealizations(EPBSArchitecture element) {
    List<PhysicalArchitectureRealization> ret = new ArrayList<PhysicalArchitectureRealization>();

    for (ArchitectureAllocation architectureAllocation : element.getProvisionedArchitectureAllocations()) {
      if (architectureAllocation instanceof PhysicalArchitectureRealization) {
        ret.add((PhysicalArchitectureRealization) architectureAllocation);
      }
    }
    return ret;
  }

  protected CapabilityRealizationPkg getContainedCapabilityRealizationPkg(EPBSArchitecture element) {
    AbstractCapabilityPkg abstractCapabilityPkg = element.getOwnedAbstractCapabilityPkg();
    if (abstractCapabilityPkg instanceof CapabilityRealizationPkg) {
      return (CapabilityRealizationPkg) abstractCapabilityPkg;
    }
    return null;
  }

  protected List<PhysicalArchitecture> getAllocatedPhysicalArchitectures(EPBSArchitecture element){
    List <PhysicalArchitecture> ret = new ArrayList<PhysicalArchitecture>();
    for (BlockArchitecture architecture : element.getAllocatedArchitectures()) {
      if (architecture instanceof PhysicalArchitecture) {
        ret.add((PhysicalArchitecture) architecture);
      }
    }
    return ret;
  }
}
