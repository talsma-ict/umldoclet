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
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlanes;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtile;
import net.sourceforge.plantuml.activitydiagram3.gtile.GtileBox;
import net.sourceforge.plantuml.activitydiagram3.gtile.GtileRepeat;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class InstructionRepeat extends AbstractInstruction implements Instruction {

	private final InstructionList repeatList;
	private final Instruction parent;
	private final LinkRendering nextLinkRenderer;
	private final Swimlane swimlane;
	private final Swimlanes swimlanes;
	private Swimlane swimlaneOut;
	private Swimlane swimlaneBackward;
	private BoxStyle boxStyle;
	private boolean killed = false;
	private final BoxStyle boxStyleIn;

	private Display backward = Display.NULL;

	private LinkRendering incoming1 = LinkRendering.none();
	private LinkRendering incoming2 = LinkRendering.none();
	private List<PositionedNote> backwardNotes = new ArrayList<>();
	private Display test = Display.NULL;
	private Display yes = Display.NULL;
	private Display out = Display.NULL;
	private final Display startLabel;
	private boolean testCalled = false;
	private LinkRendering endRepeatLinkRendering = LinkRendering.none();

	private final Colors colors;

	public boolean containsBreak() {
		return repeatList.containsBreak();
	}

	public InstructionRepeat(Swimlanes swimlanes, Instruction parent, LinkRendering nextLinkRenderer, HColor color,
			Display startLabel, BoxStyle boxStyleIn, Colors colors) {
		this.swimlanes = swimlanes;
		this.swimlane = swimlanes.getCurrentSwimlane();
		this.repeatList = new InstructionList(this.swimlane);
		this.boxStyleIn = boxStyleIn;
		this.startLabel = startLabel;
		this.parent = parent;
		this.nextLinkRenderer = Objects.requireNonNull(nextLinkRenderer);
		this.colors = colors;
	}

	private boolean isLastOfTheParent() {
		if (parent instanceof InstructionList)
			return ((InstructionList) parent).getLast() == this;

		return false;
	}

	public void setBackward(Display label, Swimlane swimlaneBackward, BoxStyle boxStyle, LinkRendering incoming1,
			LinkRendering incoming2) {
		this.backward = label;
		this.swimlaneBackward = swimlaneBackward;
		this.boxStyle = boxStyle;
		this.incoming1 = incoming1;
		this.incoming2 = incoming2;
	}

	public boolean hasBackward() {
		return this.backward != Display.NULL;
	}

	@Override
	public CommandExecutionResult add(Instruction ins) {
		return repeatList.add(ins);
	}

	@Override
	public Gtile createGtile(ISkinParam skinParam, StringBounder stringBounder) {

		final Gtile tile = repeatList.createGtile(skinParam, stringBounder);
		final Gtile backward = getGtileBackward(skinParam, stringBounder);

		return new GtileRepeat(swimlane, tile, null, test, backward);
	}

	private Gtile getGtileBackward(ISkinParam skinParam, StringBounder stringBounder) {
		if (Display.isNull(backward))
			return null;

		GtileBox result = GtileBox.create(stringBounder, skinParam, backward, getSwimlaneIn(), boxStyle, null);
//		if (backwardNotes.size() > 0) {
//			result = factory.addNote(result, swimlaneOut, backwardNotes);
//		}
		return result;
	}

	public Ftile createFtile(FtileFactory factory) {
		final Ftile back = getFtileBackward(factory);
		final Ftile decorateOut = factory.decorateOut(repeatList.createFtile(factory), endRepeatLinkRendering);
		if (this.testCalled == false && incoming1.isNone())
			incoming1 = swimlanes.nextLinkRenderer();
		final Ftile result = factory.repeat(boxStyleIn, swimlane, swimlaneOut, startLabel, decorateOut, test, yes, out,
				colors, back, isLastOfTheParent(), incoming1, incoming2);
		if (killed)
			return new FtileKilled(result);

		return result;
	}

	private Ftile getFtileBackward(FtileFactory factory) {
		if (Display.isNull(backward))
			return null;

		Ftile result = factory.activity(backward, swimlaneBackward, boxStyle, Colors.empty(), null);
		if (backwardNotes.size() > 0)
			result = factory.addNote(result, swimlaneBackward, backwardNotes);

		return result;
	}

	public Instruction getParent() {
		return parent;
	}

	public void setTest(Display test, Display yes, Display out, LinkRendering endRepeatLinkRendering,
			LinkRendering back, Swimlane swimlaneOut) {
		this.swimlaneOut = swimlaneOut;
		this.test = Objects.requireNonNull(test);
		this.yes = Objects.requireNonNull(yes);
		this.out = Objects.requireNonNull(out);
		this.endRepeatLinkRendering = endRepeatLinkRendering;
		if (back.isNone() == false)
			this.incoming1 = back;
		this.testCalled = true;
	}

	final public boolean kill() {
		if (testCalled) {
			this.killed = true;
			return true;
		}
		return repeatList.kill();
	}

	@Override
	public LinkRendering getInLinkRendering() {
		return nextLinkRenderer;
	}

	@Override
	public boolean addNote(Display note, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		if (Display.isNull(backward))
			return repeatList.addNote(note, position, type, colors, swimlaneNote);

		this.backwardNotes.add(new PositionedNote(note, position, type, colors, swimlaneNote));
		return true;

	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return repeatList.getSwimlanes();
	}

	@Override
	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneOut();
	}

	@Override
	public Swimlane getSwimlaneOut() {
		return parent.getSwimlaneOut();
	}

}
