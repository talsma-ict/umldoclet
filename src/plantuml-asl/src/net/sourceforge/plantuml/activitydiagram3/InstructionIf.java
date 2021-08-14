/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileDecorateWelding;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.WeldingPoint;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileWithNoteOpale;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class InstructionIf extends WithNote implements Instruction, InstructionCollection {

	private final List<Branch> thens = new ArrayList<>();
	private Branch elseBranch;
	private boolean endifCalled = false;
	private final ISkinParam skinParam;
	private final Url url;

	private final Instruction parent;

	private Branch current;
	private final LinkRendering topInlinkRendering;
	private LinkRendering outColor = LinkRendering.none();

	private final Swimlane swimlane;

	public boolean containsBreak() {
		for (Branch branch : thens) {
			if (branch.containsBreak()) {
				return true;
			}
		}
		if (elseBranch != null) {
			return elseBranch.containsBreak();
		}
		return false;
	}

	public InstructionIf(Swimlane swimlane, Instruction parent, Display labelTest, LinkRendering whenThen,
			LinkRendering inlinkRendering, HColor color, ISkinParam skinParam, Url url) {
		this.url = url;
		this.parent = parent;
		this.skinParam = skinParam;
		this.topInlinkRendering = Objects.requireNonNull(inlinkRendering);
		this.swimlane = swimlane;
		this.thens.add(new Branch(skinParam.getCurrentStyleBuilder(), swimlane, whenThen, labelTest, color,
				LinkRendering.none()));
		this.current = this.thens.get(0);
	}

	public CommandExecutionResult add(Instruction ins) {
		return current.add(ins);
	}

	public Ftile createFtile(FtileFactory factory) {
		for (Branch branch : thens) {
			branch.updateFtile(factory);
		}
		if (elseBranch == null) {
			this.elseBranch = new Branch(skinParam.getCurrentStyleBuilder(), swimlane, LinkRendering.none(),
					Display.NULL, null, LinkRendering.none());
		}
		elseBranch.updateFtile(factory);
		Ftile result = factory.createIf(swimlane, thens, elseBranch, outColor, topInlinkRendering, url);
		if (getPositionedNotes().size() > 0) {
			result = FtileWithNoteOpale.create(result, getPositionedNotes(), skinParam, false);
		}
		final List<WeldingPoint> weldingPoints = new ArrayList<>();
		for (Branch branch : thens) {
			weldingPoints.addAll(branch.getWeldingPoints());
		}
		weldingPoints.addAll(elseBranch.getWeldingPoints());
		if (weldingPoints.size() > 0) {
			result = new FtileDecorateWelding(result, weldingPoints);
		}
		return result;
	}

	public Instruction getParent() {
		return parent;
	}

	public boolean swithToElse2(LinkRendering whenElse, LinkRendering nextLinkRenderer) {
		if (elseBranch != null) {
			return false;
		}
		this.current.setInlinkRendering(nextLinkRenderer);
		this.elseBranch = new Branch(skinParam.getCurrentStyleBuilder(), swimlane, whenElse, Display.NULL, null,
				LinkRendering.none());
		this.current = elseBranch;
		return true;
	}

	public boolean elseIf(LinkRendering inlabel, Display test, LinkRendering whenThen, LinkRendering nextLinkRenderer,
			HColor color) {
		if (elseBranch != null) {
			return false;
		}
		// this.current.setInlinkRendering(nextLinkRenderer);
		this.current.setSpecial(nextLinkRenderer);
		this.current = new Branch(skinParam.getCurrentStyleBuilder(), swimlane, whenThen, test, color, inlabel);
		this.thens.add(current);
		return true;

	}

	public void endif(LinkRendering nextLinkRenderer) {
		endifCalled = true;
		if (elseBranch == null) {
			this.elseBranch = new Branch(skinParam.getCurrentStyleBuilder(), swimlane, LinkRendering.none(),
					Display.NULL, null, LinkRendering.none());
		}
		this.elseBranch.setSpecial(nextLinkRenderer);
		this.current.setInlinkRendering(nextLinkRenderer);
	}

	final public boolean kill() {
		if (endifCalled) {
			for (Branch branch : thens) {
				if (branch.getLast() != null && branch.getLast().kill() == false) {
					return false;
				}
				if (elseBranch != null && elseBranch.getLast() != null && elseBranch.getLast().kill() == false) {
					return false;
				}
				return true;
			}
		}
		return current.kill();
	}

	public LinkRendering getInLinkRendering() {
		return topInlinkRendering;
	}

	@Override
	public boolean addNote(Display note, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		if (endifCalled || current.isEmpty()) {
			return super.addNote(note, position, type, colors, swimlaneNote);
		} else {
			return current.addNote(note, position, type, colors, swimlaneNote);
		}
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		if (swimlane != null) {
			result.add(swimlane);
		}
		for (Branch branch : thens) {
			result.addAll(branch.getSwimlanes());
		}
		if (elseBranch != null) {
			result.addAll(elseBranch.getSwimlanes());
		}
		return Collections.unmodifiableSet(result);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	public Instruction getLast() {
		if (elseBranch == null) {
			return thens.get(thens.size() - 1).getLast();
		}
		return elseBranch.getLast();
	}

	public void outColor(LinkRendering outColor) {
		this.outColor = outColor;
	}

}
