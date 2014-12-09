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
package org.polarsys.capella.core.business.queries.information;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.polarsys.capella.common.queries.interpretor.QueryInterpretor;
import org.polarsys.capella.common.queries.queryContext.QueryContext;
import org.polarsys.capella.core.business.queries.IBusinessQuery;
import org.polarsys.capella.core.business.queries.QueryConstants;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellacore.CapellacorePackage;
import org.polarsys.capella.core.data.information.ExchangeItem;
import org.polarsys.capella.core.data.information.InformationPackage;
import org.polarsys.capella.core.model.helpers.queries.filters.RemoveFinalElement;

/**
 */
public class ExchangeItem_InheritedExchangeItem extends AbstractClassInheritedClasses implements IBusinessQuery {

	@Override
	public EClass getEClass() {
		return InformationPackage.Literals.EXCHANGE_ITEM;
	}

	@Override
	public List<EReference> getEStructuralFeatures() {
	  List<EReference> list = new ArrayList<EReference>(2);
	  list.add(CapellacorePackage.Literals.GENERALIZABLE_ELEMENT__OWNED_GENERALIZATIONS);
	  list.add(CapellacorePackage.Literals.GENERALIZABLE_ELEMENT__SUPER_GENERALIZATIONS);
    return list;
	}

	
	@Override
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		List<CapellaElement> result = new ArrayList<CapellaElement>();
		if (element_p instanceof ExchangeItem) {
			List<CapellaElement> elements = QueryInterpretor.executeQuery(QueryConstants.GET_AVAILABLE__EXCHANGE_ITEM__INHERITED_EXCHANGE_ITEM___LIB, element_p, context, new RemoveFinalElement());
			result.addAll(elements);
		}
		result.remove(element_p);
		return result;
	}
	@Override
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		List<CapellaElement> result = new ArrayList<CapellaElement>();
		if (element_p instanceof ExchangeItem) {			
			List<CapellaElement> elements = QueryInterpretor.executeQuery(QueryConstants.GET_CURRENT__EXCHANGE_ITEM__INHERITED_EXCHANGE_ITEM, element_p, context, new RemoveFinalElement());
			result.addAll(elements);
		}
		return result;
	}
	
}