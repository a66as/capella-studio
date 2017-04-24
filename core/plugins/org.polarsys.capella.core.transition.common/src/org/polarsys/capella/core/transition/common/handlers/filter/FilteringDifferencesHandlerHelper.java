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
package org.polarsys.capella.core.transition.common.handlers.filter;

import org.polarsys.capella.core.transition.common.constants.ITransitionConstants;
import org.polarsys.kitalpha.transposer.rules.handler.rules.api.IContext;

/**
 *
 */
public class FilteringDifferencesHandlerHelper {

  public static IFilteringDifferencesHandler getInstance(IContext context) {
    IFilteringDifferencesHandler handler = (IFilteringDifferencesHandler) context.get(ITransitionConstants.FILTERING_DIFFERENCES_HANDLER);
    if (handler == null) {
      handler = new DefaultFilteringDifferencesHandler();
      handler.init(context);
      context.put(ITransitionConstants.FILTERING_DIFFERENCES_HANDLER, handler);
    }
    return handler;
  }

}