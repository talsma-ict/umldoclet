/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.statediagram;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.NamespaceStrategy;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.utils.UniqueSequence;

public class StateDiagram extends AbstractEntityDiagram {

	private static final String CONCURRENT_PREFIX = "CONC";

	public StateDiagram(ISkinSimple skinParam) {
		super(skinParam);
	}

	public boolean checkConcurrentStateOk(Code code) {
		if (leafExist(code) == false) {
			return true;
		}
		final IEntity existing = this.getLeafsget(code);
		if (getCurrentGroup().getGroupType() == GroupType.CONCURRENT_STATE
				&& getCurrentGroup() != existing.getParentContainer()) {
			return false;
		}
		if (existing.getParentContainer().getGroupType() == GroupType.CONCURRENT_STATE
				&& getCurrentGroup() != existing.getParentContainer()) {
			return false;
		}
		return true;
	}

	@Override
	public IEntity getOrCreateLeaf(Code code, LeafType type, USymbol symbol) {
		if (checkConcurrentStateOk(code) == false) {
			throw new IllegalStateException("Concurrent State " + code);
		}
		if (type == null) {
			if (code.getFullName().startsWith("[*]")) {
				throw new IllegalArgumentException();
			}
			if (isGroup(code)) {
				return getGroup(code);
			}
			return getOrCreateLeafDefault(code, LeafType.STATE, null);
		}
		return getOrCreateLeafDefault(code, type, symbol);
	}

	public IEntity getStart() {
		final IGroup g = getCurrentGroup();
		if (EntityUtils.groupRoot(g)) {
			return getOrCreateLeaf(Code.of("*start"), LeafType.CIRCLE_START, null);
		}
		return getOrCreateLeaf(Code.of("*start*" + g.getCode().getFullName()), LeafType.CIRCLE_START, null);
	}

	public IEntity getEnd() {
		final IGroup p = getCurrentGroup();
		if (EntityUtils.groupRoot(p)) {
			return getOrCreateLeaf(Code.of("*end"), LeafType.CIRCLE_END, null);
		}
		return getOrCreateLeaf(Code.of("*end*" + p.getCode().getFullName()), LeafType.CIRCLE_END, null);
	}

	public IEntity getHistorical() {
		final IGroup g = getCurrentGroup();
		if (EntityUtils.groupRoot(g)) {
			return getOrCreateLeaf(Code.of("*historical"), LeafType.PSEUDO_STATE, null);
		}
		return getOrCreateLeaf(Code.of("*historical*" + g.getCode().getFullName()), LeafType.PSEUDO_STATE, null);
	}

	public IEntity getHistorical(Code codeGroup) {
		gotoGroup2(codeGroup, Display.getWithNewlines(codeGroup), GroupType.STATE, getRootGroup(),
				NamespaceStrategy.SINGLE);
		final IEntity g = getCurrentGroup();
		final IEntity result = getOrCreateLeaf(Code.of("*historical*" + g.getCode().getFullName()),
				LeafType.PSEUDO_STATE, null);
		endGroup();
		return result;
	}

	public boolean concurrentState(char direction) {
		final IGroup cur = getCurrentGroup();
		// printlink("BEFORE");
		if (EntityUtils.groupRoot(cur) == false && cur.getGroupType() == GroupType.CONCURRENT_STATE) {
			super.endGroup();
		}
		getCurrentGroup().setConcurrentSeparator(direction);
		gotoGroup2(UniqueSequence.getCode(CONCURRENT_PREFIX), Display.create(""), GroupType.CONCURRENT_STATE,
				getCurrentGroup(), NamespaceStrategy.SINGLE);
		final IGroup conc1 = getCurrentGroup();
		if (EntityUtils.groupRoot(cur) == false && cur.getGroupType() == GroupType.STATE) {
			cur.moveEntitiesTo(conc1);
			super.endGroup();
			gotoGroup2(UniqueSequence.getCode(CONCURRENT_PREFIX), Display.create(""), GroupType.CONCURRENT_STATE,
					getCurrentGroup(), NamespaceStrategy.SINGLE);
		}
		// printlink("AFTER");
		return true;
	}

	// private void printlink(String comment) {
	// Log.println("COMMENT="+comment);
	// for (Link l : getLinks()) {
	// Log.println(l);
	// }
	// }

	@Override
	public void endGroup() {
		final IGroup cur = getCurrentGroup();
		if (EntityUtils.groupRoot(cur) == false && cur.getGroupType() == GroupType.CONCURRENT_STATE) {
			super.endGroup();
		}
		super.endGroup();
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.STATE;
	}

	private boolean hideEmptyDescription = false;

	@Override
	public final void setHideEmptyDescription(boolean hideEmptyDescription) {
		this.hideEmptyDescription = hideEmptyDescription;
	}

	public final boolean isHideEmptyDescriptionForState() {
		return hideEmptyDescription;
	}

	@Override
	public String checkFinalError() {
		for (Link link : this.getLinks()) {
			final IGroup parent1 = getGroupParentIfItIsConcurrentState(link.getEntity1());
			final IGroup parent2 = getGroupParentIfItIsConcurrentState(link.getEntity2());
			if (isCompatible(parent1, parent2) == false) {
				return "State within concurrent state cannot be linked out of this concurrent state (between "
						+ link.getEntity1().getCode().getFullName() + " and "
						+ link.getEntity2().getCode().getFullName() + ")";
			}
		}
		return super.checkFinalError();
	}

	private static boolean isCompatible(IGroup parent1, IGroup parent2) {
		if (parent1 == null && parent2 == null) {
			return true;
		}
		if (parent1 != null ^ parent2 != null) {
			return false;
		}
		assert parent1 != null && parent2 != null;
		return parent1 == parent2;
	}

	private static IGroup getGroupParentIfItIsConcurrentState(IEntity ent) {
		IGroup parent = ent.getParentContainer();
		while (parent != null) {
			if (parent.getGroupType() == GroupType.CONCURRENT_STATE) {
				return parent;
			}
			parent = parent.getParentContainer();
		}
		return null;

	}

}
