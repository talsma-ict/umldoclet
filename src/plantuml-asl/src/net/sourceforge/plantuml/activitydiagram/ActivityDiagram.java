/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
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
package net.sourceforge.plantuml.activitydiagram;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.plasma.Quark;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.utils.Direction;

public class ActivityDiagram extends CucaDiagram {

	private Entity lastEntityConsulted;
	private Entity lastEntityBrancheConsulted;
	private ConditionalContext currentContext;

	public ActivityDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.ACTIVITY, skinParam);
		setNamespaceSeparator(null);
	}

	private String getAutoBranch() {
		return "#" + this.getUniqueSequence();
	}

	public void startIf(String optionalCodeString) {
		final String idShort = optionalCodeString == null ? getAutoBranch() : optionalCodeString;
		final Quark<Entity> quark = quarkInContext(true, cleanId(idShort));
		final Entity br = reallyCreateLeaf(quark, Display.create(""), LeafType.BRANCH, null);
		currentContext = new ConditionalContext(currentContext, br, Direction.DOWN);
	}

	public void endif() {
		currentContext = currentContext.getParent();
	}

	public Entity getStart() {
		final Quark<Entity> quark = quarkInContext(true, "start");
		if (quark.getData() == null)
			reallyCreateLeaf(quark, Display.getWithNewlines("start"), LeafType.CIRCLE_START, null);

		return quark.getData();
	}

	public Entity getEnd(String suppId) {
		final String tmp = suppId == null ? "end" : "end$" + suppId;
		final Quark<Entity> quark = quarkInContext(true, tmp);
		if (quark.getData() == null)
			reallyCreateLeaf(quark, Display.getWithNewlines("end"), LeafType.CIRCLE_END, null);

		return quark.getData();
	}

	@Override
	protected void updateLasts(Entity result) {
		if (result == null || result.getLeafType() == LeafType.NOTE)
			return;

		// System.err.println("updateLasts " + result);
		this.lastEntityConsulted = result;
		if (result.getLeafType() == LeafType.BRANCH)
			lastEntityBrancheConsulted = result;

	}

	public Entity createNote(Quark<Entity> idNewLong, String code__, Display display) {
		return reallyCreateLeaf(Objects.requireNonNull(idNewLong), display, LeafType.NOTE, null);
	}

	final protected List<String> getDotStrings() {
		return Arrays.asList("nodesep=.20;", "ranksep=0.4;", "edge [fontsize=11,labelfontsize=11];",
				"node [fontsize=11];");
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(" + getEntityFactory().leafs().size() + " activities)");
	}

	public Entity getLastEntityConsulted() {
		return lastEntityConsulted;
	}

	@Deprecated
	public Entity getLastEntityBrancheConsulted() {
		return lastEntityBrancheConsulted;
	}

	public final ConditionalContext getCurrentContext() {
		return currentContext;
	}

	public final void setLastEntityConsulted(Entity lastEntityConsulted) {
		// System.err.println("setLastEntityConsulted " + lastEntityConsulted);
		this.lastEntityConsulted = lastEntityConsulted;
	}

	public Entity createInnerActivity() {

		final String idShort = "##" + this.getUniqueSequence();

		final Quark<Entity> quark = quarkInContext(true, idShort);
		gotoGroup(quark, Display.getWithNewlines(quark.getName()), GroupType.INNER_ACTIVITY);
		final Entity g = getCurrentGroup();

		lastEntityConsulted = null;
		lastEntityBrancheConsulted = null;

		return g;
	}

	public void concurrentActivity(String name) {
		if (getCurrentGroup().getGroupType() == GroupType.CONCURRENT_ACTIVITY)
			endGroup();

		final String idShort = "##" + this.getUniqueSequence();

		if (getCurrentGroup().getGroupType() != GroupType.INNER_ACTIVITY)
			throw new IllegalStateException("type=" + getCurrentGroup().getGroupType());

		final Quark<Entity> idNewLong = quarkInContext(true, idShort);
		gotoGroup(idNewLong, Display.getWithNewlines("code"), GroupType.CONCURRENT_ACTIVITY);
		lastEntityConsulted = null;
		lastEntityBrancheConsulted = null;
	}

}
