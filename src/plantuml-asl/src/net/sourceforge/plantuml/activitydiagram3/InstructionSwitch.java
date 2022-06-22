/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.activitydiagram3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtile;
import net.sourceforge.plantuml.activitydiagram3.gtile.GtileIfHexagon;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class InstructionSwitch extends WithNote implements Instruction, InstructionCollection {

	private final List<Branch> switches = new ArrayList<>();
	private final ISkinParam skinParam;

	private final Instruction parent;

	private Branch current;
	private final LinkRendering topInlinkRendering;
	private LinkRendering afterEndwhile = LinkRendering.none();
	private final Display labelTest;

	private final Swimlane swimlane;

	@Override
	public boolean containsBreak() {
		for (Branch branch : switches)
			if (branch.containsBreak())
				return true;

		return false;
	}

	public InstructionSwitch(Swimlane swimlane, Instruction parent, Display labelTest, LinkRendering inlinkRendering,
			HColor color, ISkinParam skinParam) {
		this.topInlinkRendering = Objects.requireNonNull(inlinkRendering);
		this.parent = parent;
		this.skinParam = skinParam;
		this.labelTest = labelTest;
		this.swimlane = swimlane;
	}

	@Override
	public CommandExecutionResult add(Instruction ins) {
		if (current == null)
			return CommandExecutionResult.error("No 'case' in this switch");

		return current.add(ins);
	}

	@Override
	public Gtile createGtile(ISkinParam skinParam, StringBounder stringBounder) {
		for (Branch branch : switches)
			branch.updateGtile(skinParam, stringBounder);

		final List<Gtile> gtiles = new ArrayList<>();
		final List<Branch> branches = new ArrayList<>();
		for (Branch branch : switches) {
			gtiles.add(branch.getGtile());
			branches.add(branch);
		}

		return GtileIfHexagon.build(swimlane, gtiles, switches);
	}

	public Ftile createFtile(FtileFactory factory) {
		for (Branch branch : switches)
			branch.updateFtile(factory);

		Ftile result = factory.createSwitch(swimlane, switches, afterEndwhile, topInlinkRendering, labelTest);
		result = eventuallyAddNote(factory, result, getSwimlaneIn(), VerticalAlignment.TOP);
		return result;
	}

	@Override
	final public boolean kill() {
		return current.kill();
	}

	@Override
	public LinkRendering getInLinkRendering() {
		return topInlinkRendering;
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		if (swimlane != null)
			result.add(swimlane);

		for (Branch branch : switches)
			result.addAll(branch.getSwimlanes());

		return Collections.unmodifiableSet(result);
	}

	@Override
	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	@Override
	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	@Override
	public Instruction getLast() {
		return switches.get(switches.size() - 1).getLast();
	}

	public boolean switchCase(Display labelCase, LinkRendering nextLinkRenderer) {
		if (this.current != null)
			this.current.setSpecial(nextLinkRenderer);
		this.current = new Branch(skinParam.getCurrentStyleBuilder(), swimlane,
				LinkRendering.none().withDisplay(labelCase), labelCase, null,
				LinkRendering.none().withDisplay(labelCase));
		this.switches.add(this.current);
		return true;
	}

	public Instruction getParent() {
		return parent;
	}

	public void endSwitch(LinkRendering nextLinkRenderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addNote(Display note, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		if (current == null || current.isEmpty())
			return super.addNote(note, position, type, colors, swimlaneNote);
		else
			return current.addNote(note, position, type, colors, swimlaneNote);

	}

}
