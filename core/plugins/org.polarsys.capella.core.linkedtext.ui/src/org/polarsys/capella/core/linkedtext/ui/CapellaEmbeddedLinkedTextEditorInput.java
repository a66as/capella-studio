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
package org.polarsys.capella.core.linkedtext.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ILabelProvider;
import org.polarsys.capella.common.data.modellingcore.AbstractNamedElement;
import org.polarsys.capella.common.data.modellingcore.ValueSpecification;
import org.polarsys.capella.common.linkedtext.ui.DefaultLinkedTextResolver;
import org.polarsys.capella.common.linkedtext.ui.LinkedTextDocument;
import org.polarsys.capella.common.linkedtext.ui.LinkedTextDocument.Resolver;
import org.polarsys.capella.common.ui.providers.MDEAdapterFactoryLabelProvider;
import org.polarsys.capella.common.ui.services.helper.EObjectLabelProviderHelper;
import org.polarsys.capella.core.data.capellacore.Constraint;
import org.polarsys.capella.core.data.information.datavalue.AbstractExpressionValue;
import org.polarsys.capella.core.data.information.datavalue.OpaqueExpression;
import org.polarsys.capella.core.model.handler.command.CapellaResourceHelper;

import com.google.common.base.Predicate;

public abstract class CapellaEmbeddedLinkedTextEditorInput implements LinkedTextDocument.Input {

  private final EObject _documentBase;
  private ILabelProvider _labelProvider;

  public CapellaEmbeddedLinkedTextEditorInput(EObject documentBase_p) {
    _documentBase = documentBase_p;
  }

  private static final LinkedTextDocument.Resolver _resolver = new DefaultLinkedTextResolver(new Predicate<Resource>() {
    @Override
    public boolean apply(Resource res_p) {
      return CapellaResourceHelper.isCapellaResource(res_p);
    }
  });

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getDocumentBase() {
    return _documentBase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILabelProvider getLabelProvider() {
    if (_labelProvider == null) {
      AdapterFactoryEditingDomain domain = (AdapterFactoryEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(_documentBase);
      _labelProvider = new MDEAdapterFactoryLabelProvider((TransactionalEditingDomain) domain, domain.getAdapterFactory()) {
        @Override
        public String getText(Object object_p) {
          if (object_p instanceof AbstractNamedElement) {
            String name = ((AbstractNamedElement) object_p).getName();
            if ((name != null) && (name.length() > 0)) {
              return name;
            }
            return "[Unnamed " + ((EObject) object_p).eClass().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
          }
          return super.getText(object_p);
        }
      };
      ((AdapterFactoryLabelProvider) _labelProvider).setFireLabelUpdateNotifications(true);
    }
    return _labelProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Resolver getResolver() {
    return _resolver;
  }

  public void dispose() {
    if (_labelProvider != null) {
      _labelProvider.dispose();
    }
  }

  public static String getDefaultText(Constraint contraint) {
    String result = contraint.getName();

    if ((result == null) || result.isEmpty()) {
      ValueSpecification vspec = contraint.getOwnedSpecification();

      if (vspec instanceof OpaqueExpression) {
        final OpaqueExpression exp = (OpaqueExpression) vspec;
        if ((exp.getLanguages().size() > 0) && (exp.getBodies().size() > 0)) {
          if (CapellaLinkedTextConstants.OPAQUE_EXPRESSION_LINKED_TEXT.equals(exp.getLanguages().get(0))) {
            CapellaEmbeddedLinkedTextEditorInput input = new CapellaEmbeddedLinkedTextEditorInput.Readonly(exp, exp.getBodies().get(0));
            result = LinkedTextDocument.load(input).get();
            input.dispose();
          } else {
            result = exp.getBodies().get(0);
          }
        }
      } else if (vspec instanceof AbstractExpressionValue) {
        result = EObjectLabelProviderHelper.getText(vspec);
      }
    }
    return result == null ? "" : result; //$NON-NLS-1$
  }

  /**
   * A read-only input, useful for one-way conversion of linked text to something else.
   */
  public static class Readonly extends CapellaEmbeddedLinkedTextEditorInput {
    final String text;

    public Readonly(EObject documentBase_p, String text_p) {
      super(documentBase_p);
      text = text_p;
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public void setText(String linkedText_p) {
      throw new UnsupportedOperationException();
    }
  }

}