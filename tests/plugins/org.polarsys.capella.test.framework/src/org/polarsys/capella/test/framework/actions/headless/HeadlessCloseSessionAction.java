/*******************************************************************************
 * Copyright (c) 2006, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.test.framework.actions.headless;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.polarsys.capella.core.sirius.ui.actions.CloseSessionAction;

public class HeadlessCloseSessionAction extends CloseSessionAction {

  protected List<Session> sessionsToClose = new ArrayList<Session>();

  public HeadlessCloseSessionAction(List<Session> sessionsToClose_p) {
    super();
    sessionsToClose.addAll(sessionsToClose_p);
    showDialog(false);
    shouldSaveIfNoDialog(true);
  }

  @Override
  public void run() {
    // Launch the close sessions operation on all selected sessions.
    IRunnableWithProgress closeSessionOperation = new CloseSessionOperation(sessionsToClose);

    Shell activeShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    ProgressMonitorDialog monitor = new ProgressMonitorDialog(activeShell);
    try {
      monitor.run(false, false, closeSessionOperation);
    } catch (final InvocationTargetException ite) {
      SiriusPlugin.getDefault().error("the program was not able close the session", ite); //$NON-NLS-1$
    } catch (final InterruptedException ie) {
      SiriusPlugin.getDefault().warning("the close session action was interrupted", ie); //$NON-NLS-1$
    }
  }
}