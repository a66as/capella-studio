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
package org.polarsys.capella.core.business.queries.la;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.polarsys.capella.common.queries.interpretor.QueryInterpretor;
import org.polarsys.capella.common.queries.queryContext.QueryContext;
import org.polarsys.capella.core.business.queries.IBusinessQuery;
import org.polarsys.capella.core.business.queries.QueryConstants;
import org.polarsys.capella.core.business.queries.cs.Component_FunctionalAllocation;
import org.polarsys.capella.core.data.fa.FaPackage;
import org.polarsys.capella.core.data.la.LaPackage;

/**
 */
public class LogicalComponent_FunctionalAllocation extends Component_FunctionalAllocation implements IBusinessQuery {

	@Override
	public EClass getEClass() {
		return LaPackage.Literals.LOGICAL_COMPONENT;
	}

	@Override
	public List<EReference> getEStructuralFeatures() {
		return Collections.singletonList(FaPackage.Literals.ABSTRACT_FUNCTIONAL_BLOCK__OWNED_FUNCTIONAL_ALLOCATION);
	}

	@Override
	public List<EObject> getAvailableElements(EObject element) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		return QueryInterpretor.executeQuery(QueryConstants.GET_AVAILABLE__LOGICAL_COMPONENT__FUNCTIONAL_ALLOCATION, element, context);
	}

	@Override
	public List<EObject> getCurrentElements(EObject element, boolean onlyGenerated) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		return QueryInterpretor.executeQuery(QueryConstants.GET_CURRENT__LOGICAL_COMPONENT__FUNCTIONAL_ALLOCATION, element, context);
	}
}
