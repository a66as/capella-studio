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
package org.polarsys.capella.core.business.queries.queries.cs;

import java.util.Collections;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.interpretor.QueryInterpretor;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.InterfaceImplementation;
import org.polarsys.capella.core.data.ctx.Actor;
import org.polarsys.capella.core.data.la.LogicalActor;
import org.polarsys.capella.core.data.la.LogicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalActor;
import org.polarsys.capella.core.data.pa.PhysicalComponent;

public class GetAvailable_InterfaceImplementation_ImplementedInterface extends AbstractQuery {

  @Override
  public List<Object> execute(Object element_p, IQueryContext context) {

    if (element_p instanceof InterfaceImplementation) {

      InterfaceImplementation implem = (InterfaceImplementation) element_p;
      String queryIdentifier = "GetAvailable_Actor_ImplementedInterfaces__Lib"; //$NON-NLS-1$
      Component component = implem.getInterfaceImplementor();

      if (component instanceof org.polarsys.capella.core.data.ctx.System) {
        queryIdentifier = "GetAvailable_System_ImplementedInterfaces__Lib"; //$NON-NLS-1$

      } else if (component instanceof Actor) {
        queryIdentifier = "GetAvailable_Actor_ImplementedInterfaces__Lib"; //$NON-NLS-1$

      } else if (component instanceof LogicalComponent) {
        queryIdentifier = "GetAvailable_LogicalComponent_ImplementedInterfaces__Lib"; //$NON-NLS-1$

      } else if (component instanceof LogicalActor) {
        queryIdentifier = "GetAvailable_LogicalActor_ImplInterfaces__Lib"; //$NON-NLS-1$

      } else if (component instanceof PhysicalComponent) {
        queryIdentifier = "GetAvailable_PhysicalComp_ImplementedInterface__Lib"; //$NON-NLS-1$

      } else if (component instanceof PhysicalActor) {
        queryIdentifier = "GetAvailable_PhysicalActor_ImplementedInterfaces__Lib"; //$NON-NLS-1$
      }
      return QueryInterpretor.executeQuery(queryIdentifier, component, context);
    }
    return Collections.emptyList();

  }
}