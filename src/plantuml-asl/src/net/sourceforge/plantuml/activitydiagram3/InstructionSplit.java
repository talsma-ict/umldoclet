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
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtile;
import net.sourceforge.plantuml.activitydiagram3.gtile.GtileSplit;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtiles;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;

public class InstructionSplit extends AbstractInstruction implements Instruction {

	private final List<InstructionList> splits = new ArrayList<>();
	private final Instruction parent;
	private final LinkRendering inlinkRendering;
	private final Swimlane swimlaneIn;
	private Swimlane swimlaneOut;

	public InstructionSplit(Instruction parent, LinkRendering inlinkRendering, Swimlane swimlane) {
		this.parent = parent;
		this.swimlaneIn = swimlane;

		this.splits.add(new InstructionList(swimlane));
		this.inlinkRendering = Objects.requireNonNull(inlinkRendering);
	}

	@Override
	public boolean containsBreak() {
		for (InstructionList split : splits) {
			if (split.containsBreak()) {
				return true;
			}
		}
		return false;
	}

	private InstructionList getLast() {
		return splits.get(splits.size() - 1);
	}

	@Override
	public CommandExecutionResult add(Instruction ins) {
		return getLast().add(ins);
	}

	@Override
	public Gtile createGtile(ISkinParam skinParam, StringBounder stringBounder) {
		final List<Gtile> all = new ArrayList<>();
		for (InstructionList list : splits) {
			Gtile tmp = list.createGtile(skinParam, stringBounder);
			tmp = Gtiles.withIncomingArrow(tmp, 20);
			tmp = Gtiles.withOutgoingArrow(tmp, 20);
			all.add(tmp);
		}

		return new GtileSplit(all, swimlaneIn, getInLinkRenderingColor(skinParam).getColor());
	}

	private Rainbow getInLinkRenderingColor(ISkinParam skinParam) {
		Rainbow color;
		color = Rainbow.build(skinParam);
		return color;
	}

	@Override
	public Ftile createFtile(FtileFactory factory) {
		final List<Ftile> all = new ArrayList<>();
		for (InstructionList list : splits) {
			all.add(list.createFtile(factory));
		}
		return factory.createParallel(all, ForkStyle.SPLIT, null, swimlaneIn, swimlaneOut);
	}

	public Instruction getParent() {
		return parent;
	}

	public void splitAgain(LinkRendering inlinkRendering) {
		if (inlinkRendering != null) {
			getLast().setOutRendering(inlinkRendering);
		}
		final InstructionList list = new InstructionList(swimlaneIn);
		this.splits.add(list);
	}

	public void endSplit(LinkRendering inlinkRendering, Swimlane endSwimlane) {
		if (inlinkRendering != null) {
			getLast().setOutRendering(inlinkRendering);
		}
		this.swimlaneOut = endSwimlane;

	}

	@Override
	final public boolean kill() {
		return getLast().kill();
	}

	@Override
	public LinkRendering getInLinkRendering() {
		return inlinkRendering;
	}

	@Override
	public boolean addNote(Display note, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		return getLast().addNote(note, position, type, colors, swimlaneNote);
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return InstructionList.getSwimlanes2(splits);
	}

	@Override
	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneOut();
	}

	@Override
	public Swimlane getSwimlaneOut() {
		return swimlaneOut;
		// return getLast().getSwimlaneOut();
	}

}
