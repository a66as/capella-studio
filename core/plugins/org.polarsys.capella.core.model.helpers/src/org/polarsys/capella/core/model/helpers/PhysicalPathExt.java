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

package org.polarsys.capella.core.model.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.StrictCompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.osgi.util.NLS;

import org.polarsys.capella.common.helpers.SimpleOrientedGraph;
import org.polarsys.capella.common.helpers.TransactionHelper;
import org.polarsys.capella.core.data.cs.AbstractPathInvolvedElement;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.CsFactory;
import org.polarsys.capella.core.data.cs.CsPackage;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.cs.PhysicalLink;
import org.polarsys.capella.core.data.cs.PhysicalPath;
import org.polarsys.capella.core.data.cs.PhysicalPathInvolvement;
import org.polarsys.capella.core.data.cs.PhysicalPathReference;
import org.polarsys.capella.core.data.cs.PhysicalPort;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.fa.ComponentExchange;
import org.polarsys.capella.core.data.fa.ComponentPort;
import org.polarsys.capella.core.data.information.Port;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.common.data.modellingcore.InformationsExchanger;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.common.menu.dynamic.CreationHelper;

/**
 */
public class PhysicalPathExt {

  static String PLUGIN_ID = "org.polarsys.capella.core.model.helpers"; //$NON-NLS-1$

  public static boolean isFirstPhysicalPathInvolvement(PhysicalPathInvolvement involment) {
    if ((involment.getInvolvedElement() != null) && involment.getPreviousInvolvements().isEmpty()) {
      return true;
    }
    return false;
  }

  public static boolean isLastPhysicalPathInvolvement(PhysicalPathInvolvement involment) {
    if ((involment.getInvolvedElement() != null) && involment.getNextInvolvements().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * retrieves the source functions of a functional chain. if functional chain is composite, returns also starting functions of sub functional chains
   * @param functionalChain_p a functional chain
   * @return source functions of functionalChain_p
   */
  public static Collection<PhysicalPathInvolvement> getFlatFirstPhysicalPathInvolvments(PhysicalPath physicalPath1) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    LinkedList<PhysicalPath> toVisit = new LinkedList<PhysicalPath>();
    HashSet<PhysicalPath> visited = new HashSet<PhysicalPath>();
    toVisit.add(physicalPath1);

    while (!toVisit.isEmpty()) {
      PhysicalPath chain = toVisit.removeFirst();
      if (visited.contains(chain)) {
        continue;
      }
      visited.add(chain);
      for (PhysicalPathInvolvement involvement : chain.getOwnedPhysicalPathInvolvements()) {
        if (isFirstPhysicalPathInvolvement(involvement)) {
          if (involvement.getInvolvedElement() instanceof PhysicalPath) {
            toVisit.add((PhysicalPath) involvement.getInvolvedElement());
          } else {
            result.add(involvement);
          }
        }
      }
    }
    return result;
  }

  /**
   * retrieves the source functions of a functional chain. if functional chain is composite, returns also starting functions of sub functional chains
   * @param functionalChain_p a functional chain
   * @return source functions of functionalChain_p
   */
  public static Collection<PhysicalPathInvolvement> getFlatLastPhysicalPathInvolvments(PhysicalPath physicalPath1) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    LinkedList<PhysicalPath> toVisit = new LinkedList<PhysicalPath>();
    HashSet<PhysicalPath> visited = new HashSet<PhysicalPath>();
    toVisit.add(physicalPath1);

    while (!toVisit.isEmpty()) {
      PhysicalPath chain = toVisit.removeFirst();
      if (visited.contains(chain)) {
        continue;
      }
      visited.add(chain);
      for (PhysicalPathInvolvement involvement : chain.getOwnedPhysicalPathInvolvements()) {
        if (isLastPhysicalPathInvolvement(involvement)) {
          if (involvement.getInvolvedElement() instanceof PhysicalPath) {
            toVisit.add((PhysicalPath) involvement.getInvolvedElement());
          } else {
            result.add(involvement);
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns all PhysicalPaths defined in the given architecture
   */
  public static List<PhysicalPath> getAllPhysicalPaths(BlockArchitecture architecture) {
    List<PhysicalPath> returnedList = new ArrayList<PhysicalPath>();
    if ((architecture instanceof PhysicalArchitecture) || (architecture instanceof SystemAnalysis) || (architecture instanceof LogicalArchitecture)) {
      for (Component aComponent : BlockArchitectureExt.getAllComponents(architecture)) {
        returnedList.addAll((aComponent).getOwnedPhysicalPath());
      }
    }
    return returnedList;
  }


  /**
   * @param involvement
   * @return the previous involvements that involve a Physical Link
   */
  public static Set<PhysicalPathInvolvement> getPreviousPhysicalLinkInvolvements(PhysicalPathInvolvement involvement) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();
    LinkedList<PhysicalPathInvolvement> toVisit = new LinkedList<PhysicalPathInvolvement>();
    HashSet<PhysicalPathInvolvement> visited = new HashSet<PhysicalPathInvolvement>();
    toVisit.add(involvement);

    while (!toVisit.isEmpty()) {
      PhysicalPathInvolvement involvment = toVisit.removeFirst();
      if (visited.contains(involvment)) {
        continue;
      }
      visited.add(involvment);

      for (PhysicalPathInvolvement aPreviousInv : involvment.getPreviousInvolvements()) {
        if (aPreviousInv.getInvolvedElement() != null) {
          if (aPreviousInv.getInvolvedElement() instanceof PhysicalLink) {
            result.add(aPreviousInv);
          } else {
            toVisit.add(aPreviousInv);
          }
        }
      }
    }
    return result;

  }

  /**
   * Returns previous exchanges involved before this involvement. if its a functional chain reference, returns last exchanges info this functional chain
   * @param involvement
   * @return the previous involvements that involve a Functional Exchange
   */
  public static Set<PhysicalPathInvolvement> getFlatPreviousLinkInvolvements(PhysicalPathInvolvement involvement) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    for (PhysicalPathInvolvement aPreviousInv : getFlatPreviousPhysicalPathInvolvements(involvement)) {
      if (aPreviousInv.getInvolvedElement() != null) {
        if (aPreviousInv.getInvolvedElement() instanceof PhysicalLink) {
          result.add(aPreviousInv);
        } else {
          for (PhysicalPathInvolvement aaanv : getFlatPreviousPhysicalPathInvolvements(aPreviousInv)) {
            if (aaanv.getInvolvedElement() instanceof PhysicalLink) {
              result.add(aaanv);
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * @param involvement
   * @return the next involvements that involve a Physical Link
   */
  public static Set<PhysicalPathInvolvement> getNextPhysicalLinkInvolvements(PhysicalPathInvolvement involvement) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();
    LinkedList<PhysicalPathInvolvement> toVisit = new LinkedList<PhysicalPathInvolvement>();
    HashSet<PhysicalPathInvolvement> visited = new HashSet<PhysicalPathInvolvement>();
    toVisit.add(involvement);

    while (!toVisit.isEmpty()) {
      PhysicalPathInvolvement involvment = toVisit.removeFirst();
      if (visited.contains(involvment)) {
        continue;
      }
      visited.add(involvment);

      for (PhysicalPathInvolvement aPreviousInv : involvment.getNextInvolvements()) {
        if (aPreviousInv.getInvolvedElement() != null) {
          if (aPreviousInv.getInvolvedElement() instanceof PhysicalLink) {
            result.add(aPreviousInv);
          } else {
            toVisit.add(aPreviousInv);
          }
        }
      }
    }

    return result;
  }

  /**
   * @param involvement
   * @return the next involvements that involve a Functional Exchange
   */
  public static Set<PhysicalPathInvolvement> getFlatNextExchangeInvolvements(PhysicalPathInvolvement involvement) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    for (PhysicalPathInvolvement aPreviousInv : getFlatNextPhysicalPathInvolvements(involvement)) {
      if (aPreviousInv.getInvolvedElement() != null) {
        if (aPreviousInv.getInvolvedElement() instanceof PhysicalLink) {
          result.add(aPreviousInv);
        } else {
          for (PhysicalPathInvolvement aaanv : getFlatNextPhysicalPathInvolvements(aPreviousInv)) {
            if (aaanv.getInvolvedElement() instanceof PhysicalLink) {
              result.add(aaanv);
            }
          }
        }
      }
    }
    return result;
  }

  public static Set<PhysicalPathInvolvement> getFlatPreviousPhysicalPathInvolvements(PhysicalPathInvolvement involvement) {

    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    for (PhysicalPathInvolvement in : involvement.getPreviousInvolvements()) {
      if (in.getInvolvedElement() instanceof PhysicalPath) {
        result.addAll(getFlatFirstPhysicalPathInvolvments((PhysicalPath) in.getInvolvedElement()));
        result.addAll(getFlatLastPhysicalPathInvolvments((PhysicalPath) in.getInvolvedElement()));
      } else {
        result.add(in);
      }
    }

    return result;

  }

  public static Set<PhysicalPathInvolvement> getFlatNextPhysicalPathInvolvements(PhysicalPathInvolvement involvement) {

    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();

    for (PhysicalPathInvolvement in : involvement.getNextInvolvements()) {
      if (in.getInvolvedElement() instanceof PhysicalPath) {
        result.addAll(getFlatLastPhysicalPathInvolvments((PhysicalPath) in.getInvolvedElement()));
        result.addAll(getFlatFirstPhysicalPathInvolvments((PhysicalPath) in.getInvolvedElement()));
      } else {
        result.add(in);
      }
    }

    return result;
  }

  /**
   * retrieves the source functions of a functional chain. if functional chain is composite, returns also starting functions of sub functional chains
   * @param functionalChain_p a functional chain
   * @return source functions of functionalChain_p
   */
  public static Set<Part> getFlatPhysicalPathFirstParts(PhysicalPath chain) {
    Set<Part> result = new HashSet<Part>();

    for (PhysicalPathInvolvement inv : getFlatFirstPhysicalPathInvolvments(chain)) {
      if (inv.getInvolvedElement() instanceof Part) {
        result.add((Part) inv.getInvolvedElement());
      }
    }

    return result;
  }

  /**
   * retrieves the target functions of a functional chain if functional chain is composite, returns also ending functions of sub functional chains
   * @param functionalChain_p a functional chain
   * @return target functions of functionalChain_p
   */
  public static Set<Part> getFlatPhysicalPathLastParts(PhysicalPath chain) {
    Set<Part> result = new HashSet<Part>();

    for (PhysicalPathInvolvement inv : getFlatLastPhysicalPathInvolvments(chain)) {
      if (inv.getInvolvedElement() instanceof Part) {
        result.add((Part) inv.getInvolvedElement());
      }
    }
    return result;
  }

  public static Collection<PhysicalLink> getFlatOutgoingIncomingLinks(PhysicalPathInvolvement element) {

    Collection<Part> targetFunctions = new HashSet<Part>();
    Collection<PhysicalLink> targetExchanges = new HashSet<PhysicalLink>();

    AbstractPathInvolvedElement involvedElement = element.getInvolvedElement();

    if (involvedElement instanceof PhysicalLink) {
      targetExchanges.add((PhysicalLink) involvedElement);

    } else if (involvedElement instanceof Part) {
      targetFunctions.add((Part) involvedElement);

    } else if (involvedElement instanceof PhysicalPath) {
      targetFunctions.addAll(getFlatPhysicalPathFirstParts((PhysicalPath) involvedElement));
      targetFunctions.addAll(getFlatPhysicalPathLastParts((PhysicalPath) involvedElement));
    }

    for (Part function : targetFunctions) {
      targetExchanges.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(function));
    }
    return targetExchanges;
  }

  @Deprecated
  public static Collection<PhysicalLink> getFlatIncomingLinks(PhysicalPathInvolvement element) {

    Collection<Part> targetFunctions = new HashSet<Part>();
    Collection<PhysicalLink> targetExchanges = new HashSet<PhysicalLink>();

    AbstractPathInvolvedElement involvedElement = element.getInvolvedElement();

    if (involvedElement instanceof PhysicalLink) {
      targetExchanges.add((PhysicalLink) involvedElement);

    } else if (involvedElement instanceof Part) {
      targetFunctions.add((Part) involvedElement);

    } else if (involvedElement instanceof PhysicalPath) {
      targetFunctions.addAll(getFlatPhysicalPathFirstParts((PhysicalPath) involvedElement));
    }

    for (Part function : targetFunctions) {
      targetExchanges.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(function));
    }
    return targetExchanges;
  }

  @Deprecated
  public static Collection<PhysicalLink> getFlatOutgoingLinks(PhysicalPathInvolvement element) {

    Collection<Part> sourceFunctions = new HashSet<Part>();
    Collection<PhysicalLink> sourceExchanges = new HashSet<PhysicalLink>();

    AbstractPathInvolvedElement involvedElement = element.getInvolvedElement();

    if (involvedElement instanceof PhysicalLink) {
      sourceExchanges.add((PhysicalLink) involvedElement);

    } else if (involvedElement instanceof Part) {
      sourceFunctions.add((Part) involvedElement);

    } else if (involvedElement instanceof PhysicalPath) {
      sourceFunctions.addAll(getFlatPhysicalPathLastParts((PhysicalPath) involvedElement));
    }

    for (Part function : sourceFunctions) {
      sourceExchanges.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(function));
    }

    return sourceExchanges;
  }

  /**
   * @param path
   * @return true if the physical path is valid, false otherwise
   */
  public static boolean isPhysicalPathValid(PhysicalPath path) {
    if (!isPhysicalPathWellFormed(path)) {
      return false;
    }
    List<PhysicalPathInvolvement> sources = path.getFirstPhysicalPathInvolvements();
    if (sources.isEmpty()) {
      return false;
    }
    for (PhysicalPathInvolvement source : sources) {
      if (containsACycle(source, new HashSet<PhysicalPathInvolvement>())) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param path
   * @return
   */
  @Deprecated
  public static Collection<PhysicalLink> getFlatIncomingLinks(PhysicalPath path) {
    Collection<Part> targetFunctions = new HashSet<Part>();
    Collection<PhysicalLink> targetExchanges = new HashSet<PhysicalLink>();

    targetFunctions.addAll(getFlatPhysicalPathFirstParts(path));

    for (Part function : targetFunctions) {
      targetExchanges.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(function));
    }
    return targetExchanges;
  }

  public static Collection<PhysicalLink> getFlatOutgoingIncomingLinks(PhysicalPath path) {
    Collection<Part> targetFunctions = new HashSet<Part>();
    Collection<PhysicalLink> targetExchanges = new HashSet<PhysicalLink>();

    targetFunctions.addAll(getFlatPhysicalPathFirstParts(path));
    targetFunctions.addAll(getFlatPhysicalPathLastParts(path));

    for (Part function : targetFunctions) {
      targetExchanges.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(function));
    }
    return targetExchanges;
  }

  /**
   * @param path
   * @return true if the physical path is well-formed, false otherwise
   */
  public static boolean isPhysicalPathWellFormed(PhysicalPath path) {
    SimpleOrientedGraph<Part> graph = new SimpleOrientedGraph<Part>();
    if (path.getOwnedPhysicalPathInvolvements().isEmpty()) {
      return false;
    }
    for (PhysicalPathInvolvement inv : getFlatInvolvements(path)) {
      if (!isPhysicalPathInvolvementValid(inv)) {
        return false;
      }
      if (inv.getInvolvedElement() instanceof PhysicalLink) {

        PhysicalLink link = (PhysicalLink) inv.getInvolvedElement();

        Set<PhysicalPathInvolvement> previouses = getFlatPreviousPhysicalPathInvolvements(inv);
        Set<PhysicalPathInvolvement> nexts = getFlatNextPhysicalPathInvolvements(inv);

        Collection<Part> previousParts = new HashSet<Part>();
        Collection<Part> nextParts = new HashSet<Part>();

        for (PhysicalPathInvolvement involvment : previouses) {
          if ((involvment.getInvolved() != null) && (involvment.getInvolved() instanceof Part)) {
            previousParts.add((Part) involvment.getInvolved());
          }
        }
        for (PhysicalPathInvolvement involvment : nexts) {
          if ((involvment.getInvolved() != null) && (involvment.getInvolved() instanceof Part)) {
            nextParts.add((Part) involvment.getInvolved());
          }
        }

        Collection<Part> linkedParts = new HashSet<Part>();
        linkedParts.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getSourceParts(link));
        linkedParts.addAll(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getTargetParts(link));

        previousParts.retainAll(linkedParts);
        nextParts.retainAll(linkedParts);

        for (Part previousPart : previousParts) {
          for (Part nextPart : nextParts) {
            graph.addNode(previousPart, nextPart);
          }
        }
      }
    }
    if (graph.isEmpty()) {
      return false;
    }
    if (!graph.isAConnectedGraph()) {
      return false;
    }
    return true;
  }

  /**
   * @param involvement an involvement that involves a Physical Link
   * @return the source part of the involved physical Link
   */
  public static Part getSourcePart(PhysicalPathInvolvement involvement) {
    if ((involvement == null) || (involvement.getInvolvedElement() == null) || !(involvement.getInvolvedElement() instanceof PhysicalLink)
        || involvement.getNextInvolvements().isEmpty() || (involvement.getNextInvolvements().get(0).getInvolvedElement() == null)) {
      return null;
    }
    PhysicalLink involvedLink = (PhysicalLink) involvement.getInvolvedElement();
    Part end1 = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getSourcePart(involvedLink);
    Part end2 = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getTargetPart(involvedLink);
    AbstractPathInvolvedElement nextInvoledElement = involvement.getNextInvolvements().get(0).getInvolvedElement();
    if (end1.equals(nextInvoledElement)) {
      return end2;
    }
    return end1;
  }

  /**
   * @param involvement an involvement that involves a Physical Link
   * @return the target part of the involved physical Link
   */
  public static Part getTargetPart(PhysicalPathInvolvement involvement) {
    if ((involvement == null) || (involvement.getInvolvedElement() == null) || !(involvement.getInvolvedElement() instanceof PhysicalLink)
        || involvement.getNextInvolvements().isEmpty() || (involvement.getNextInvolvements().get(0).getInvolvedElement() == null)) {
      return null;
    }
    PhysicalLink involvedLink = (PhysicalLink) involvement.getInvolvedElement();
    Part end1 = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getSourcePart(involvedLink);
    Part end2 = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getTargetPart(involvedLink);
    AbstractPathInvolvedElement nextInvoledElement = involvement.getNextInvolvements().get(0).getInvolvedElement();
    if (end1.equals(nextInvoledElement)) {
      return end1;
    }
    return end2;
  }

  /**
   * @param inv
   * @return a status to know if the physical path involvement is valid
   */
  public static IStatus isPhysicalPathInvolvementValidWithStatus(PhysicalPathInvolvement inv) {
    IStatus status = Status.OK_STATUS;

    String element = Messages.PhysicalPathExt_PhysicalComponent;
    String link = Messages.PhysicalPathExt_PhysicalLink;
    String path = Messages.PhysicalPathExt_PhysicalPath;
    String aElement = Messages.FunctionalChainExt_a + element;
    String aLink = Messages.FunctionalChainExt_a + link;
    String aPath = Messages.FunctionalChainExt_a + path;

    if (inv.getInvolvedElement() == null) {
      return new Status(IStatus.ERROR, PLUGIN_ID, Messages.Involvement_InvolvedNull);
    }

    // Check correct involved element
    if (inv instanceof PhysicalPathReference) {
      if (!(inv.getInvolved() instanceof PhysicalPath)) {
        return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_InvolvedElementNot, aPath));
      }
    } else {
      if (!((inv.getInvolved() instanceof Part) || (inv.getInvolved() instanceof PhysicalLink))) {
        return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_InvolvedElementNotAndNot, aElement, aLink));
      }
    }

    if (((inv.getInvolvedElement() instanceof PhysicalLink) && (inv.getNextInvolvements().size() != 1))) {
      return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_InvolvedElementWithMultipleNext, aLink));
    }
    if (((inv.getInvolvedElement() instanceof Part) && inv.getNextInvolvements().isEmpty() && inv.getPreviousInvolvements().isEmpty())) {
      return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_InvolvementAlone, aElement));
    }

    for (PhysicalPathInvolvement aNext : inv.getNextInvolvements()) {

      // A Part should be linked to a physical link
      if (inv.getInvolvedElement() instanceof Part) {
        if ((aNext.getInvolvedElement() == null) || !(aNext.getInvolvedElement() instanceof PhysicalLink)) {
          return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsButNextIsNotA, aElement, aLink));
        }
        Part currentPart = (Part) inv.getInvolvedElement();
        if (!currentPart.equals(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getSourcePart((PhysicalLink) aNext.getInvolvedElement()))
            && !currentPart
                .equals(org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getTargetPart((PhysicalLink) aNext.getInvolvedElement()))) {
          return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsNotRelatedToSourceNext, aElement, link));
        }
      }

      // A Physical Link should be an incoming link of next involvement
      else if (inv.getInvolvedElement() instanceof PhysicalLink) {

        if ((aNext.getInvolved() == null) || !((aNext.getInvolved() instanceof Part) || (aNext.getInvolved() instanceof PhysicalPath))) {
          return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_NextIsNotOrNot, new Object[] { aLink, aElement, aPath }));
        }

        if (getFlatCommonPhysicalLinks(inv, aNext).isEmpty()) {
          if (aNext.getInvolved() instanceof PhysicalPath) {
            return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsNotRelatedToTargetNextFunctionalChain, new Object[] { aLink,
                                                                                                                                                    element,
                                                                                                                                                    path }));
          }
          return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsNotRelatedToTargetNext, new Object[] { aLink, element, element }));
        }
      }
      // A functional chain should be between both involvement
      else if (inv.getInvolved() instanceof PhysicalPath) {
        if (getFlatCommonPhysicalLinks(inv, aNext).isEmpty()) {
          if (aNext.getInvolved() instanceof PhysicalPath) {
            return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsNotRelatedToTargetNextFunctionalChain, new Object[] { aPath,
                                                                                                                                                    element,
                                                                                                                                                    path }));
          }
          return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.FunctionalChainExt_IsNotRelatedToOutgoingNext, new Object[] { aPath, link, link }));
        }
      }

    }
    return status;

  }

  /**
   * @param inv
   * @return true if the physical path involvement is valid, false otherwise
   */
  public static boolean isPhysicalPathInvolvementValid(PhysicalPathInvolvement inv) {
    return isPhysicalPathInvolvementValidWithStatus(inv).isOK();
  }

  /**
   * detect a cycle in a PhysicalPath
   * @param involvement1
   * @param visitedInvolvements1
   * @return
   */
  public static boolean containsACycle(PhysicalPathInvolvement involvement1, Set<PhysicalPathInvolvement> visitedInvolvements1) {
    Set<PhysicalPathInvolvement> visitedInvolvements = new HashSet<PhysicalPathInvolvement>();
    LinkedList<PhysicalPathInvolvement> toVisit = new LinkedList<PhysicalPathInvolvement>();
    toVisit.add(involvement1);
    while (!toVisit.isEmpty()) {
      PhysicalPathInvolvement involvment = toVisit.removeFirst();
      if (visitedInvolvements.contains(involvment)) {
        return true;
      }
      visitedInvolvements.add(involvment);

      for (PhysicalPathInvolvement aNext : involvment.getNextInvolvements()) {
        // we don't use flat involvement, we can use a FC more than once in another FC
        toVisit.add(aNext);
      }
    }
    return false;
  }

  /**
   * @param container
   * @param involvedPhysicalLinks
   * @param source the source of the path
   * @return a new PhysicalPath initialized with the given involved physical links
   */
  public static PhysicalPath createPhysicalPath(final Component container, final Collection<PhysicalLink> involvedPhysicalLinks, final Part source) {
    // Is not sure that this sturdiness is useful to avoid the recreation of several involvement part.
    HashMap<Part, PhysicalPathInvolvement> mapping = new HashMap<Part, PhysicalPathInvolvement>();

    PhysicalPath newPath = CsFactory.eINSTANCE.createPhysicalPath();

    container.getOwnedPhysicalPath().add(newPath);
    EditingDomain editingDomain = TransactionHelper.getEditingDomain(newPath);
    StrictCompoundCommand command = CreationHelper.getAdditionnalCommand(editingDomain, newPath);
    if (command.canExecute()) {
      command.execute();
    }

    PhysicalPathInvolvement previousPartInv = null;

    Iterator<PhysicalLink> iterator = involvedPhysicalLinks.iterator();
    while (iterator.hasNext()) {
      PhysicalLink pl = iterator.next();
      Part sourcePart = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getSourcePart(pl);
      Part targetPart = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getTargetPart(pl);
      Collection<PhysicalLink> relatedLinks = org.polarsys.capella.core.data.helpers.cs.services.PhysicalLinkExt.getAllRelatedPhysicalLinks(sourcePart);

      for (PhysicalLink aLink : relatedLinks) {
        if (pl.equals(aLink)) {

          if (!mapping.containsKey(sourcePart)) {
            previousPartInv = createInvolvement(newPath, sourcePart);
            mapping.put(sourcePart, previousPartInv);
          }
          PhysicalPathInvolvement newLinkInv = createInvolvement(newPath, aLink);
          mapping.get(sourcePart).getNextInvolvements().add(newLinkInv);

          if (!mapping.containsKey(targetPart)) {
            previousPartInv = createInvolvement(newPath, targetPart);
            mapping.put(targetPart, previousPartInv);
          }

          newLinkInv.getNextInvolvements().add(mapping.get(targetPart));
        }
      }
    }
    return newPath;
  }

  /**
   * @param path the physical path which contains the new involvement
   * @param involved the involved element
   * @return a new PhysicalPathInvolvement initialized with the given arguments
   */
  public static PhysicalPathInvolvement createInvolvement(PhysicalPath path, AbstractPathInvolvedElement involved) {
    PhysicalPathInvolvement newInv = CsFactory.eINSTANCE.createPhysicalPathInvolvement();
    path.getOwnedPhysicalPathInvolvements().add(newInv);
    newInv.setInvolved(involved);
    return newInv;
  }

  /**
   * @param path
   * @return involved elements of the physical path
   */
  public static List<AbstractPathInvolvedElement> getInvolvedElements(PhysicalPath path) {
    List<AbstractPathInvolvedElement> involvedElements = new ArrayList<AbstractPathInvolvedElement>();
    for (PhysicalPathInvolvement inv : path.getOwnedPhysicalPathInvolvements()) {
      if (inv.getInvolvedElement() != null) {
        involvedElements.add(inv.getInvolvedElement());
      }
    }
    return involvedElements;
  }

  /**
   * @param path
   * @return
   */
  private static Collection<PhysicalPathInvolvement> getFlatInvolvements(PhysicalPath path) {
    Collection<PhysicalPathInvolvement> involvments = new ArrayList<PhysicalPathInvolvement>();
    LinkedList<PhysicalPath> toVisit = new LinkedList<PhysicalPath>();
    HashSet<PhysicalPath> visited = new HashSet<PhysicalPath>();
    toVisit.add(path);

    while (!toVisit.isEmpty()) {
      PhysicalPath chain = toVisit.removeFirst();
      if (visited.contains(chain)) {
        continue;
      }
      visited.add(chain);
      for (PhysicalPathInvolvement involvement : chain.getOwnedPhysicalPathInvolvements()) {
        if ((involvement.getInvolvedElement() != null) && (involvement.getInvolvedElement() instanceof PhysicalPath)) {
          toVisit.add((PhysicalPath) involvement.getInvolvedElement());
        }
        involvments.add(involvement);
      }
    }
    return involvments;
  }

  /**
   * @param path
   * @param physicalLink_p
   * @return
   */
  public static Collection<PhysicalPathInvolvement> getFlatInvolvementsOf(PhysicalPath path, EClass involvedClass) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();
    for (PhysicalPathInvolvement involvement : getFlatInvolvements(path)) {
      if ((involvement.getInvolvedElement() != null) && involvedClass.isInstance(involvement.getInvolvedElement())) {
        result.add(involvement);
      }
    }
    return result;
  }

  /**
   * @param aPath
   * @param physicalLink_p
   * @return
   */
  public static Collection<EObject> getFlatInvolvedElements(PhysicalPath aPath, EClass involvedClass) {
    Set<EObject> result = new HashSet<EObject>();
    for (PhysicalPathInvolvement involvement : getFlatInvolvements(aPath)) {
      if ((involvement.getInvolvedElement() != null) && involvedClass.isInstance(involvement.getInvolvedElement())) {
        result.add(involvement.getInvolvedElement());
      }
    }
    return result;
  }

  /**
   * @param key
   * @return
   */
  public static Collection<PhysicalLink> getFlatPhysicalLinks(PhysicalPath key) {
    Set<PhysicalLink> result = new HashSet<PhysicalLink>();

    for (PhysicalPathInvolvement involvement : getFlatInvolvementsOf(key, CsPackage.Literals.PHYSICAL_LINK)) {
      if (involvement.getInvolvedElement() != null) {
        result.add((PhysicalLink) involvement.getInvolvedElement());
      }
    }
    return result;
  }

  /**
   * @param source
   * @param target
   * @return
   */
  public static Collection<PhysicalLink> getFlatCommonPhysicalLinks(PhysicalPathInvolvement source, PhysicalPathInvolvement target) {
    Collection<PhysicalLink> sourceExchanges = getFlatOutgoingIncomingLinks(source);
    Collection<PhysicalLink> targetExchanges = getFlatOutgoingIncomingLinks(target);
    sourceExchanges.retainAll(targetExchanges);
    return sourceExchanges;
  }

  /**
   * @param fc
   * @param involved_p
   * @return all the involvements of the PhysicalPath that involves the given element
   */
  public static Set<PhysicalPathInvolvement> getInvolvementsOf(PhysicalPath fc, EClass involvedClass) {
    Set<PhysicalPathInvolvement> result = new HashSet<PhysicalPathInvolvement>();
    for (PhysicalPathInvolvement anInvolvement : fc.getOwnedPhysicalPathInvolvements()) {
      if ((anInvolvement.getInvolvedElement() != null) && involvedClass.isInstance(anInvolvement.getInvolvedElement())) {
        result.add(anInvolvement);
      }
    }
    return result;
  }

  /**
   * @param pPath
   * @param cExchange
   */
  public static void synchronizeAllocations(PhysicalPath pPath, ComponentExchange cExchange) {
    Port ceSource = ComponentExchangeExt.getSourcePort(cExchange);
    Port ceTarget = ComponentExchangeExt.getTargetPort(cExchange);
    if ((ceSource instanceof ComponentPort) && (ceTarget instanceof ComponentPort)) {
      synchronizeAllocations(pPath, (ComponentPort) ceSource, (ComponentPort) ceTarget);
    }
  }

  /**
   * @param pPath
   * @param ceSource
   * @param ceTarget
   */
  private static void synchronizeAllocations(PhysicalPath pPath, ComponentPort ceSource, ComponentPort ceTarget) {
    PhysicalLinkExt.synchronizeAllocations(getPhysicalPortFrom(pPath, ceSource), ceSource);
    PhysicalLinkExt.synchronizeAllocations(getPhysicalPortFrom(pPath, ceTarget), ceTarget);
  }

  /**
   * @param pPath
   * @param cExchange
   * @param forceCleaning
   * @return
   */
  public static List<ModelElement> evaluateImpactsOfUnsynchronizeAllocations(PhysicalPath pPath, ComponentExchange cExchange, boolean forceCleaning) {
    List<ModelElement> result = new ArrayList<ModelElement>();
    Port ceSource = ComponentExchangeExt.getSourcePort(cExchange);
    Port ceTarget = ComponentExchangeExt.getTargetPort(cExchange);
    if ((ceSource instanceof ComponentPort) && (ceTarget instanceof ComponentPort)) {
      result.addAll(unsynchronizeAllocations(pPath, (ComponentPort) ceSource, (ComponentPort) ceTarget, forceCleaning));
    }
    return result;
  }

  /**
   * @param pPath
   * @param ceSource
   * @param ceTarget
   * @param forceCleaning
   */
  private static List<ModelElement> unsynchronizeAllocations(PhysicalPath pPath, ComponentPort ceSource, ComponentPort ceTarget, boolean forceCleaning) {
    List<ModelElement> result = new ArrayList<ModelElement>();
    if (forceCleaning || PhysicalLinkExt.getExchangesFrom(pPath, ceSource).isEmpty()) {
      result.addAll(PhysicalLinkExt.unsynchronizeAllocations(getPhysicalPortFrom(pPath, ceSource), ceSource));
    }
    if (forceCleaning || PhysicalLinkExt.getExchangesFrom(pPath, ceTarget).isEmpty()) {
      result.addAll(PhysicalLinkExt.unsynchronizeAllocations(getPhysicalPortFrom(pPath, ceTarget), ceTarget));
    }
    return result;
  }

  /**
   * Retrieves the physical port related to the given physical path and the given component port
   * @param pPath
   * @param cPort
   * @return
   */
  private static PhysicalPort getPhysicalPortFrom(PhysicalPath pPath, InformationsExchanger cPort) {
    List<PhysicalLink> terminalLinks = new ArrayList<PhysicalLink>();
    for (PhysicalPathInvolvement first : PhysicalPathExt.getFlatFirstPhysicalPathInvolvments(pPath)) {
      for (PhysicalPathInvolvement next : first.getNextInvolvements()) {
        AbstractPathInvolvedElement involvedElt = next.getInvolvedElement();
        if ((involvedElt instanceof PhysicalLink) && !terminalLinks.contains(involvedElt)) {
          terminalLinks.add((PhysicalLink) involvedElt);
        }
      }
    }
    for (PhysicalPathInvolvement last : PhysicalPathExt.getFlatLastPhysicalPathInvolvments(pPath)) {
      for (PhysicalPathInvolvement previous : last.getPreviousInvolvements()) {
        AbstractPathInvolvedElement involvedElt = previous.getInvolvedElement();
        if ((involvedElt instanceof PhysicalLink) && !terminalLinks.contains(involvedElt)) {
          terminalLinks.add((PhysicalLink) involvedElt);
        }
      }
    }

    for (PhysicalLink link : terminalLinks) {
      PhysicalPort port = PhysicalLinkExt.getPhysicalPortFrom(link, cPort);
      if (null != port) {
        return port;
      }
    }
    return null;
  }
}
