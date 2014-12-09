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
package org.polarsys.capella.common.ef;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Execution manager registry.<br>
 * Allows to get access to a specific {@link ExecutionManager} from its editing domain.<br>
 */
public class ExecutionManagerRegistry {
  /**
   * Unique instance reference.
   */
  private static ExecutionManagerRegistry __uniqueInstance;
  /**
   * The main map of (String, {@link ExecutionManager}).
   */
  private Map<TransactionalEditingDomain, ExecutionManager> _managers;

  /**
   * Private constructor.
   */
  private ExecutionManagerRegistry() {
    _managers = new HashMap<TransactionalEditingDomain, ExecutionManager>(0);
  }

  /**
   * Dispose registry content.
   */
  public void dispose() {
    _managers.clear();
  }

  /**
   * Get {@link ExecutionManagerRegistry} unique instance.
   * @return
   */
  public static ExecutionManagerRegistry getInstance() {
    if (null == __uniqueInstance) {
      __uniqueInstance = new ExecutionManagerRegistry();
    }
    return __uniqueInstance;
  }

  /**
   * Create and register a new {@link ExecutionManager}.
   *
   * @return the new execution manager
   */
  public ExecutionManager addNewManager() {
    ExecutionManager executionManager = new ExecutionManager();
    _managers.put(executionManager.getEditingDomain(), executionManager);
    return executionManager;
  }

  /**
   * Remove manager from all maps.
   * @param id_p The unique manager ID, also being the ID of its editing domain provider. Must be not <code>null</code>.
   */
  public void removeManager(ExecutionManager executionManager_p) {
    if ((executionManager_p != null) && (executionManager_p.getEditingDomain() != null)) {
      _managers.remove(executionManager_p.getEditingDomain());
    }
  }

  /**
   * Get execution manager from editing domain.<br>
   *
   * @param editingDomain the editing domain.
   * @return the execution manager, or <code>null</code> if none could be found.
   */
  public ExecutionManager getExecutionManager(EditingDomain editingDomain) {
    return _managers.get(editingDomain);
  }

  /**
   * @return All existing editing domains.
   */
  public Collection<TransactionalEditingDomain> getAllEditingDomains() {
    return _managers.keySet();
  }
}