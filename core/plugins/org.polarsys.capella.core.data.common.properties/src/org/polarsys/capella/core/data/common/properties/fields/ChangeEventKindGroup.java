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
package org.polarsys.capella.core.data.common.properties.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.polarsys.capella.core.data.capellacommon.ChangeEventKind;
import org.polarsys.capella.core.data.common.properties.Messages;
import org.polarsys.capella.core.ui.properties.fields.AbstractSemanticKindGroup;

/**
 *
 */
public class ChangeEventKindGroup extends AbstractSemanticKindGroup {

  private Button _kindBtnWhen;

  /**
   * @param parent_p
   * @param widgetFactory_p
   * @param groupLabel_p
   * @param numColumns_p
   */
  public ChangeEventKindGroup(Composite parent_p, TabbedPropertySheetWidgetFactory widgetFactory_p, boolean enabled_p) {
    super(parent_p, widgetFactory_p, Messages.getString("StateEvent.ChangeEventKind"), 2); //$NON-NLS-1$

    _kindBtnWhen = createButton(ChangeEventKind.WHEN, enabled_p);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Button getDefaultSemanticField() {
    return _kindBtnWhen;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Button> getSemanticFields() {
    List<Button> buttons = new ArrayList<Button>();

    buttons.add(_kindBtnWhen);

    return buttons;
  }

}