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
package org.polarsys.capella.core.data.epbs.properties.fields;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.polarsys.capella.core.data.epbs.EpbsPackage;
import org.polarsys.capella.core.data.epbs.properties.Messages;
import org.polarsys.capella.core.ui.properties.fields.AbstractSemanticField;

/**
 */
public class ConfigurationItemIdGroup extends AbstractSemanticField {

  private Text _itemIdentifierField;

  /**
   * @param parent
   * @param widgetFactory
   */
  public ConfigurationItemIdGroup(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
    super(widgetFactory);

    Group textGroup = widgetFactory.createGroup(parent, ""); //$NON-NLS-1$
    textGroup.setLayout(new GridLayout(2, false));
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    textGroup.setLayoutData(gd);

    createConfigurationItemIdTextField(textGroup);
  }

  /**
   * @param textGroup
   */
  private void createConfigurationItemIdTextField(Group textGroup) {
    _widgetFactory.createCLabel(textGroup, Messages.getString("ConfigurationItemIdentifier.Label")); //$NON-NLS-1$
    _itemIdentifierField = _widgetFactory.createText(textGroup, ""); //$NON-NLS-1$
    _itemIdentifierField.addFocusListener(this);
    _itemIdentifierField.addKeyListener(this);
    _itemIdentifierField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadData(EObject semanticElement) {
    loadData(semanticElement, null);

    if (null != _semanticElement) {
      if (null != _itemIdentifierField)
        setTextValue(_itemIdentifierField, _semanticElement, EpbsPackage.eINSTANCE.getConfigurationItem_ItemIdentifier());
    }
  }

  /**
   * @param textField text field to be filled
   */
  @Override
  protected void fillTextField(Text textField) {
    if (textField.equals(_itemIdentifierField)) {
      setDataValue(_semanticElement, EpbsPackage.eINSTANCE.getConfigurationItem_ItemIdentifier(), _itemIdentifierField.getText());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEnabled(boolean enabled) {
    if (null != _itemIdentifierField && !_itemIdentifierField.isDisposed()) {
      _itemIdentifierField.setEnabled(enabled);
    }
  }
}
