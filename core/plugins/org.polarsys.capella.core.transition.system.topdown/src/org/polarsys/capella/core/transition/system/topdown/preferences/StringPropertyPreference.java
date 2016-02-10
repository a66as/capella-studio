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
package org.polarsys.capella.core.transition.system.topdown.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.polarsys.capella.common.flexibility.properties.property.AbstractProperty;
import org.polarsys.capella.common.flexibility.properties.property.IDefaultValueProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IEditableProperty;
import org.polarsys.capella.common.flexibility.properties.schema.IPropertyContext;
import org.polarsys.capella.common.flexibility.properties.schema.PropertiesSchemaConstants;
import org.polarsys.capella.core.commands.preferences.service.ScopedCapellaPreferencesStore;
import org.polarsys.capella.core.commands.preferences.util.PreferencesHelper;

/**
 *
 */
public class StringPropertyPreference extends AbstractProperty implements IEditableProperty, IDefaultValueProperty {

  public StringPropertyPreference() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public Object getType() {
    return String.class;
  }

  /**
   * {@inheritDoc}
   */
  public Object getValue(IPropertyContext context) {
    String scope = getParameter(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__SCOPE);
    String preferenceId = getId();
    if (isArgumentSet(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__PREFERENCE_ID)) {
      preferenceId = getParameter(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__PREFERENCE_ID);
    }
    return ScopedCapellaPreferencesStore.getInstance(scope).getString(preferenceId);
  }

  /**
   * {@inheritDoc}
   */
  public void setValue(IPropertyContext context) {
    Object value = context.getCurrentValue(this);
    String preferenceId = getId();
    if (isArgumentSet(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__PREFERENCE_ID)) {
      preferenceId = getParameter(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__PREFERENCE_ID);
    }

    IProject project = PreferencesHelper.getSelectedCapellaProject();
    String value2 = ((String) this.toType(value, context));
    ScopedCapellaPreferencesStore.getOptions().put(preferenceId, String.valueOf(value2));
    if (project != null) {
      ScopedCapellaPreferencesStore.putString(project, preferenceId, value2);
    } else {
      ScopedCapellaPreferencesStore.putString(null, preferenceId, value2);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IStatus validate(Object newValue, IPropertyContext context) {
    try {
      if (newValue != null) {
        String value = String.valueOf(newValue);

        String validOnEmpty = getParameter(PropertiesSchemaConstants.PropertiesSchema_STRING_PROPERTY__EMPTY_IS_VALID);
        if ((validOnEmpty != null) && "false".equals(validOnEmpty) && (value.length() == 0)) {
          return new Status(IStatus.ERROR, getId(), "Empty value isn't valid");
        }
      }
      return Status.OK_STATUS;
    } catch (Exception e) {
      return new Status(IStatus.ERROR, getId(), e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  public Object getDefaultValue(IPropertyContext context) {
    String argument = getParameter(PropertiesSchemaConstants.PropertiesSchema_PROPERTY_PREFERENCE__DEFAULT);
    return toType(argument, context);
  }

  @Override
  public Object toType(Object value, IPropertyContext context) {
    return value;
  }
}
