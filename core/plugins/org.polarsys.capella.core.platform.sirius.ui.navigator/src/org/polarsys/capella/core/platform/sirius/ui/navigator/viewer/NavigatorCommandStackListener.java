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
package org.polarsys.capella.core.platform.sirius.ui.navigator.viewer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.polarsys.capella.common.platform.sirius.ted.SemanticEditingDomainFactory.SemanticEditingDomain;
import org.polarsys.capella.core.platform.sirius.ui.commands.CapellaDeleteCommand;

/**
 * A command stack listener used to retrieve elements created or relevant from the last command triggered on a command stack
 * This listener triggers an event on the registered ICommandStackSelectionProvider
 */
public class NavigatorCommandStackListener implements CommandStackListener {

  // Field is never null
  WeakReference<Command> _mostRecent = new WeakReference<Command>(null);

  ICommandStackSelectionProvider callback;

  public NavigatorCommandStackListener(ICommandStackSelectionProvider callback_p) {
    callback = callback_p;
  }

  @Override
  public void commandStackChanged(final EventObject event_P) {

    // Get the most recent command.
    final Command mostRecentCommand = ((CommandStack) event_P.getSource()).getMostRecentCommand();

    // We also get notified on rollbacks, so better verify if we've handled this command before
    // Not sure if this is the best solution.
    if (_mostRecent.get() != mostRecentCommand) {
      _mostRecent = new WeakReference<Command>(mostRecentCommand);
    } else {
      return;
    }

    if (isRelevant(mostRecentCommand)) {

      // Try to select the affected objects.
      Collection<?> affectedObjects = mostRecentCommand.getAffectedObjects();
      // Select and reveal objects.
      if ((null != affectedObjects) && !affectedObjects.isEmpty()) {
        Object selectedElement = affectedObjects.iterator().next();
        // Take into account the first contained element, we don't want to select the other ones.
        // For instance, when creating a property for a class, we don't want to select its min & max cards.
        if (selectedElement != null) {
          ISelection selection = new StructuredSelection(selectedElement);
          if ((mostRecentCommand instanceof RecordingCommand) && (mostRecentCommand.getLabel().startsWith(CapellaDeleteCommand.ID))) {
            selection = handleDeleteCommand(affectedObjects);
          }
          selectionChanged(selection);
        }
      }
    }

  }

  protected void selectionChanged(ISelection selection_p) {
    callback.commandStackSelectionChanged(selection_p);
  }

  /**
   * Handle Delete commands.
   * @param affectedObjects
   */
  protected ISelection handleDeleteCommand(Collection<?> affectedObjects_p) {
    List<EObject> elementsToSelect = new ArrayList<EObject>(0);
    for (Object affectedObject : affectedObjects_p) {
      if (affectedObject instanceof EObject) {
        EObject parent = null;
        try {
          parent = ((EObject) affectedObject).eContainer();
        } catch (Exception exception_p) {
          // With CDO when closing the sirius session, a late event to select a cdo object can occur.
          // If the underlying cdo transaction is closed, we can't call eContainer(). in that case don't select something.
        }
        if (null != parent) {
          elementsToSelect.add(parent);
        }
      }
    }
    return new StructuredSelection(elementsToSelect);
  }

  /**
   * Default implementations filters out all recording commands apart from {@link CapellaDeleteCommand} (identified by {@link CapellaDeleteCommand#ID}
   * @param mostRecentCommand_p
   * @return
   */
  protected boolean shouldSelectAndReveal(RecordingCommand mostRecentCommand_p) {
    boolean shouldHandleMostRecentCommand;
    if (mostRecentCommand_p.getLabel().startsWith(CapellaDeleteCommand.ID)) {
      shouldHandleMostRecentCommand = true;
    } else {
      shouldHandleMostRecentCommand = false;
    }
    return shouldHandleMostRecentCommand;
  }

  /**
   * Should handle most recent command to select and reveal elements in current common viewer.<br>
   * Default implementation filters out :
   * <ul>
   * <li> <code>SetCommand</code> performed with new value of String kind.</li>
   * <li><code>CompoundCommand</code> containing SetCommand matching previous case.</li>
   * <li><code>RecordingCommand</code> see {@link #shouldSelectAndReveal(RecordingCommand)}.
   * </ul>
   * @param mostRecentCommand_p
   * @return <code>true</code> means selecteAndReveal needed.
   */
  protected boolean isRelevant(Command mostRecentCommand_p) {
    // Precondition
    if (null == mostRecentCommand_p) {
      return false;
    }
    boolean shouldHandleMostRecentCommand = true;
    if (mostRecentCommand_p instanceof SetCommand) {
      SetCommand setCommand = (SetCommand) mostRecentCommand_p;
      // Filter out eSet with String value.
      shouldHandleMostRecentCommand = !(setCommand.getValue() instanceof String);
    } else if (mostRecentCommand_p instanceof CompoundCommand) {
      CompoundCommand compoundCommand = (CompoundCommand) mostRecentCommand_p;
      // Loop over contained commands.
      for (Command command : compoundCommand.getCommandList()) {
        shouldHandleMostRecentCommand = isRelevant(command);
        if (!shouldHandleMostRecentCommand) {
          break;
        }
      }
    } else if (mostRecentCommand_p instanceof RecordingCommand) {
      //shouldHandleMostRecentCommand = shouldSelectAndReveal(mostRecentCommand_p);
    }
    return shouldHandleMostRecentCommand;
  }

  /**
   * @param editingDomain_p
   */
  public void registerCommandStackListener(SemanticEditingDomain editingDomain_p) {
    editingDomain_p.getCommandStack().addCommandStackListener(this);
  }

  /**
   * @param editingDomain_p
   */
  public void unregisterCommandStackListener(SemanticEditingDomain editingDomain_p) {
    // TODO Auto-generated method stub
  }

}