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

import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileWithNoteOpale;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class InstructionWhile extends WithNote implements Instruction, InstructionCollection {

	private final InstructionList repeatList = new InstructionList();
	private final Instruction parent;
	private final LinkRendering nextLinkRenderer;
	private final HColor color;
	private boolean killed = false;

	private final Display test;
	private Display yes;

	private boolean testCalled = false;

	private LinkRendering outColor = LinkRendering.none();
	private final Swimlane swimlane;
	private final ISkinParam skinParam;

	private Instruction specialOut;

	private BoxStyle boxStyle;
	private Swimlane swimlaneOut;
	private Display backward = Display.NULL;
	private LinkRendering incoming1 = LinkRendering.none();
	private LinkRendering incoming2 = LinkRendering.none();
	private boolean backwardCalled;

	public void overwriteYes(Display yes) {
		this.yes = yes;
	}

	public InstructionWhile(Swimlane swimlane, Instruction parent, Display test, LinkRendering nextLinkRenderer,
			Display yes, HColor color, ISkinParam skinParam) {
		if (test == null) {
			throw new IllegalArgumentException();
		}
		if (yes == null) {
			throw new IllegalArgumentException();
		}
		this.parent = parent;
		this.test = test;
		this.nextLinkRenderer = nextLinkRenderer;
		if (nextLinkRenderer == null) {
			throw new IllegalArgumentException();
		}
		this.yes = yes;
		this.swimlane = swimlane;
		this.color = color;
		this.skinParam = skinParam;
	}

	public void add(Instruction ins) {
		repeatList.add(ins);
	}

	public Ftile createFtile(FtileFactory factory) {
		final Ftile back = Display.isNull(backward) ? null
				: factory.activity(backward, swimlane, boxStyle, Colors.empty(), null);
		Ftile tmp = repeatList.createFtile(factory);
		tmp = factory.createWhile(outColor, swimlane, tmp, test, yes, color, specialOut, back, incoming1, incoming2);
		if (getPositionedNotes().size() > 0) {
			tmp = FtileWithNoteOpale.create(tmp, getPositionedNotes(), skinParam, false);
		}
		if (killed || specialOut != null) {
			return new FtileKilled(tmp);
		}
		return tmp;
	}

	public Instruction getParent() {
		return parent;
	}

	final public boolean kill() {
		if (testCalled) {
			this.killed = true;
			return true;
		}
		return repeatList.kill();
	}

	public LinkRendering getInLinkRendering() {
		return nextLinkRenderer;
	}

	public void outDisplay(Display out) {
		if (out == null) {
			throw new IllegalArgumentException();
		}
		this.outColor = outColor.withDisplay(out);
	}

	public void outColor(Rainbow rainbow) {
		this.outColor = outColor.withRainbow(rainbow);
	}

	@Override
	public boolean addNote(Display note, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		if (repeatList.isEmpty()) {
			return super.addNote(note, position, type, colors, swimlaneNote);
		} else {
			return repeatList.addNote(note, position, type, colors, swimlaneNote);
		}
	}

	public Set<Swimlane> getSwimlanes() {
		return repeatList.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return parent.getSwimlaneOut();
	}

	public Instruction getLast() {
		return repeatList.getLast();
	}

	public void setSpecial(Instruction special) {
		this.specialOut = special;
	}

	public boolean containsBreak() {
		return repeatList.containsBreak();
	}

	public void setBackward(Display label, Swimlane swimlaneOut, BoxStyle boxStyle, LinkRendering incoming1,
			LinkRendering incoming2) {
		this.backward = label;
		this.swimlaneOut = swimlaneOut;
		this.boxStyle = boxStyle;
		this.incoming1 = incoming1;
		this.incoming2 = incoming2;
		this.backwardCalled = true;
	}

	public void incoming(LinkRendering incoming) {
		if (backwardCalled == false) {
			this.incoming1 = incoming;
			this.incoming2 = incoming;
		}
		this.testCalled = true;
	}

}
