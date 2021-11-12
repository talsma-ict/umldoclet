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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlanes;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockRecentred;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.comp.CompressionMode;
import net.sourceforge.plantuml.ugraphic.comp.CompressionXorYBuilder;

public class ActivityDiagram3 extends UmlDiagram {

	enum SwimlaneStrategy {
		SWIMLANE_FORBIDDEN, SWIMLANE_ALLOWED;
	}

	private SwimlaneStrategy swimlaneStrategy;

	private final Swimlanes swinlanes = new Swimlanes(getSkinParam(), getPragma());

	public ActivityDiagram3(UmlSource source, ISkinSimple skinParam) {
		super(source, UmlDiagramType.ACTIVITY, skinParam);
	}

	private void manageSwimlaneStrategy() {
		if (swimlaneStrategy == null) {
			swimlaneStrategy = SwimlaneStrategy.SWIMLANE_FORBIDDEN;
		}
	}

	public CommandExecutionResult swimlane(String name, HColor color, Display label) {
		if (swimlaneStrategy == null) {
			swimlaneStrategy = SwimlaneStrategy.SWIMLANE_ALLOWED;
		}
		if (swimlaneStrategy == SwimlaneStrategy.SWIMLANE_FORBIDDEN) {
			return CommandExecutionResult.error("This swimlane must be defined at the start of the diagram.");
		}

		swinlanes.swimlane(name, color, label);
		return CommandExecutionResult.ok();
	}

	private void setCurrent(Instruction ins) {
		swinlanes.setCurrent(ins);
	}

	private Instruction current() {
		return swinlanes.getCurrent();
	}

	private LinkRendering nextLinkRenderer() {
		return swinlanes.nextLinkRenderer();
	}

	public CommandExecutionResult addActivity(Display activity, BoxStyle style, Url url, Colors colors,
			Stereotype stereotype) {
		manageSwimlaneStrategy();
		final InstructionSimple ins = new InstructionSimple(activity, nextLinkRenderer(),
				swinlanes.getCurrentSwimlane(), style, url, colors, stereotype);
		final CommandExecutionResult added = current().add(ins);
		if (added.isOk() == false) {
			return added;
		}
		setNextLinkRendererInternal(LinkRendering.none());
		manageHasUrl(activity);
		if (url != null) {
			hasUrl = true;
		}
		return CommandExecutionResult.ok();

	}

	public void addSpot(String spot, HColor color) {
		final InstructionSpot ins = new InstructionSpot(spot, color, nextLinkRenderer(),
				swinlanes.getCurrentSwimlane());
		current().add(ins);
		setNextLinkRendererInternal(LinkRendering.none());
		manageSwimlaneStrategy();
	}

	public CommandExecutionResult addGoto(String name) {
		final InstructionGoto ins = new InstructionGoto(swinlanes.getCurrentSwimlane(), name);
		current().add(ins);
		setNextLinkRendererInternal(LinkRendering.none());
		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult addLabel(String name) {
		final InstructionLabel ins = new InstructionLabel(swinlanes.getCurrentSwimlane(), name);
		current().add(ins);
		setNextLinkRendererInternal(LinkRendering.none());
		return CommandExecutionResult.ok();
	}

	public void start() {
		manageSwimlaneStrategy();
		current().add(new InstructionStart(swinlanes.getCurrentSwimlane(), nextLinkRenderer()));
		setNextLinkRendererInternal(LinkRendering.none());
	}

	public void stop() {
		manageSwimlaneStrategy();
		final InstructionStop ins = new InstructionStop(swinlanes.getCurrentSwimlane(), nextLinkRenderer());
		if (manageSpecialStopEndAfterEndWhile(ins)) {
			return;
		}
		current().add(ins);
	}

	public void end() {
		manageSwimlaneStrategy();
		final InstructionEnd ins = new InstructionEnd(swinlanes.getCurrentSwimlane(), nextLinkRenderer());
		if (manageSpecialStopEndAfterEndWhile(ins)) {
			return;
		}
		current().add(ins);
	}

	private boolean manageSpecialStopEndAfterEndWhile(Instruction special) {
		if (current() instanceof InstructionList == false) {
			return false;
		}
		final InstructionList current = (InstructionList) current();
		final Instruction last = current.getLast();
		if (last instanceof InstructionWhile == false) {
			return false;
		}
		final InstructionWhile instructionWhile = (InstructionWhile) last;
		if (instructionWhile.containsBreak()) {
			return false;
		}
		instructionWhile.setSpecial(special);
		return true;
	}

	public void breakInstruction() {
		manageSwimlaneStrategy();
		current().add(new InstructionBreak(swinlanes.getCurrentSwimlane(), nextLinkRenderer()));
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("activity3");
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		// BUG42
		// COMPRESSION
		final StringBounder stringBounder = fileFormatOption.getDefaultStringBounder(getSkinParam());
		swinlanes.computeSize(stringBounder);
		TextBlock result = swinlanes;

		result = CompressionXorYBuilder.build(CompressionMode.ON_X, result, stringBounder);
		result = CompressionXorYBuilder.build(CompressionMode.ON_Y, result, stringBounder);

		result = new TextBlockRecentred(result);
		return createImageBuilder(fileFormatOption).drawable(result).write(os);
	}

	public void fork() {
		manageSwimlaneStrategy();
		final InstructionFork instructionFork = new InstructionFork(current(), nextLinkRenderer(), getSkinParam(),
				swinlanes.getCurrentSwimlane());
		current().add(instructionFork);
		setNextLinkRendererInternal(LinkRendering.none());
		setCurrent(instructionFork);
	}

	public CommandExecutionResult forkAgain() {
		if (current() instanceof InstructionFork) {
			final InstructionFork currentFork = (InstructionFork) current();
			currentFork.manageOutRendering(nextLinkRenderer(), false);
			setNextLinkRendererInternal(LinkRendering.none());
			currentFork.forkAgain(swinlanes.getCurrentSwimlane());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find fork");
	}

	public CommandExecutionResult endFork(ForkStyle forkStyle, String label) {
		if (current() instanceof InstructionFork) {
			final InstructionFork currentFork = (InstructionFork) current();
			currentFork.setStyle(forkStyle, label, swinlanes.getCurrentSwimlane());
			currentFork.manageOutRendering(nextLinkRenderer(), true);
			setNextLinkRendererInternal(LinkRendering.none());
			setCurrent(currentFork.getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find fork");
	}

	public void split() {
		final InstructionSplit instructionSplit = new InstructionSplit(current(), nextLinkRenderer(),
				swinlanes.getCurrentSwimlane());
		setNextLinkRendererInternal(LinkRendering.none());
		current().add(instructionSplit);
		setCurrent(instructionSplit);
	}

	public CommandExecutionResult splitAgain() {
		if (current() instanceof InstructionSplit) {
			((InstructionSplit) current()).splitAgain(nextLinkRenderer());
			setNextLinkRendererInternal(LinkRendering.none());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find split");
	}

	public CommandExecutionResult endSplit() {
		if (current() instanceof InstructionSplit) {
			((InstructionSplit) current()).endSplit(nextLinkRenderer(), swinlanes.getCurrentSwimlane());
			setNextLinkRendererInternal(LinkRendering.none());
			setCurrent(((InstructionSplit) current()).getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find split");
	}

	public void startSwitch(Display test, HColor color) {
		manageSwimlaneStrategy();
		final InstructionSwitch instructionSwitch = new InstructionSwitch(swinlanes.getCurrentSwimlane(), current(),
				test, nextLinkRenderer(), color, getSkinParam());
		current().add(instructionSwitch);
		setNextLinkRendererInternal(LinkRendering.none());
		setCurrent(instructionSwitch);
	}

	public CommandExecutionResult switchCase(Display labelCase) {
		if (current() instanceof InstructionSwitch) {
			final boolean ok = ((InstructionSwitch) current()).switchCase(labelCase, nextLinkRenderer());
			if (ok == false) {
				return CommandExecutionResult.error("You cannot put an elseIf here");
			}
			setNextLinkRendererInternal(LinkRendering.none());
			return CommandExecutionResult.ok();

		}
		return CommandExecutionResult.error("Cannot find switch");
	}

	public CommandExecutionResult endSwitch() {
		if (current() instanceof InstructionSwitch) {
			((InstructionSwitch) current()).endSwitch(nextLinkRenderer());
			setNextLinkRendererInternal(LinkRendering.none());
			setCurrent(((InstructionSwitch) current()).getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find switch");
	}

	public void startIf(Display test, Display whenThen, HColor color, Url url) {
		manageSwimlaneStrategy();
		final InstructionIf instructionIf = new InstructionIf(swinlanes.getCurrentSwimlane(), current(), test,
				LinkRendering.none().withDisplay(whenThen), nextLinkRenderer(), color, getSkinParam(), url);
		current().add(instructionIf);
		setNextLinkRendererInternal(LinkRendering.none());
		setCurrent(instructionIf);
	}

	public CommandExecutionResult elseIf(LinkRendering inlabel, Display test, LinkRendering whenThen, HColor color) {
		if (current() instanceof InstructionIf) {
			final boolean ok = ((InstructionIf) current()).elseIf(inlabel, test, whenThen, nextLinkRenderer(), color);
			if (ok == false) {
				return CommandExecutionResult.error("You cannot put an elseIf here");
			}
			setNextLinkRendererInternal(LinkRendering.none());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find if");
	}

	public CommandExecutionResult else2(LinkRendering whenElse) {
		if (current() instanceof InstructionIf) {
			final boolean result = ((InstructionIf) current()).swithToElse2(whenElse, nextLinkRenderer());
			if (result == false) {
				return CommandExecutionResult.error("Cannot find if");
			}
			setNextLinkRendererInternal(LinkRendering.none());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find if");
	}

	public CommandExecutionResult endif() {
		// System.err.println("Activity3::endif");
		if (current() instanceof InstructionIf) {
			((InstructionIf) current()).endif(nextLinkRenderer());
			setNextLinkRendererInternal(LinkRendering.none());
			setCurrent(((InstructionIf) current()).getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find if");
	}

	public void startRepeat(HColor color, Display label, BoxStyle boxStyleIn, Colors colors) {
		manageSwimlaneStrategy();
		final InstructionRepeat instructionRepeat = new InstructionRepeat(swinlanes, current(), nextLinkRenderer(),
				color, label, boxStyleIn, colors);
		current().add(instructionRepeat);
		setCurrent(instructionRepeat);
		setNextLinkRendererInternal(LinkRendering.none());

	}

	public CommandExecutionResult repeatWhile(Display label, Display yes, Display out, Display linkLabel,
			Rainbow linkColor) {
		manageSwimlaneStrategy();
		if (current() instanceof InstructionRepeat) {
			final InstructionRepeat instructionRepeat = (InstructionRepeat) current();
			final LinkRendering back = new LinkRendering(linkColor).withDisplay(linkLabel);
			instructionRepeat.setTest(label, yes, out, nextLinkRenderer(), back, swinlanes.getCurrentSwimlane());
			setCurrent(instructionRepeat.getParent());
			this.setNextLinkRendererInternal(LinkRendering.none());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find repeat");

	}

	public CommandExecutionResult backward(Display label, BoxStyle boxStyle, LinkRendering incoming1,
			LinkRendering incoming2) {
		manageSwimlaneStrategy();
		if (current() instanceof InstructionRepeat) {
			final InstructionRepeat instructionRepeat = (InstructionRepeat) current();
			instructionRepeat.setBackward(label, swinlanes.getCurrentSwimlane(), boxStyle, incoming1, incoming2);
			return CommandExecutionResult.ok();
		}
		if (current() instanceof InstructionWhile) {
			final InstructionWhile instructionWhile = (InstructionWhile) current();
			instructionWhile.setBackward(label, swinlanes.getCurrentSwimlane(), boxStyle, incoming1, incoming2);
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find repeat");

	}

	public void doWhile(Display test, Display yes, HColor color) {
		manageSwimlaneStrategy();
		final InstructionWhile instructionWhile = new InstructionWhile(swinlanes.getCurrentSwimlane(), current(), test,
				nextLinkRenderer(), yes, color, getSkinParam());
		current().add(instructionWhile);
		setCurrent(instructionWhile);
	}

	public CommandExecutionResult endwhile(Display out) {
		if (current() instanceof InstructionWhile) {
			((InstructionWhile) current()).incoming(nextLinkRenderer());
			((InstructionWhile) current()).outDisplay(out);
			setNextLinkRendererInternal(LinkRendering.none());
			setCurrent(((InstructionWhile) current()).getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find while");
	}

	final public CommandExecutionResult kill() {
		if (current().kill() == false) {
			return CommandExecutionResult.error("kill cannot be used here");
		}
		return CommandExecutionResult.ok();
	}

	public void startGroup(Display name, HColor backColor, HColor titleColor, HColor borderColor, USymbol type,
			double roundCorner) {
		manageSwimlaneStrategy();
		final InstructionGroup instructionGroup = new InstructionGroup(current(), name, backColor, titleColor,
				swinlanes.getCurrentSwimlane(), borderColor, nextLinkRenderer(), type, roundCorner);
		current().add(instructionGroup);
		setCurrent(instructionGroup);
	}

	public CommandExecutionResult endGroup() {
		if (current() instanceof InstructionGroup) {
			setCurrent(((InstructionGroup) current()).getParent());
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot find group");
	}

	private void setNextLinkRendererInternal(LinkRendering link) {
		// System.err.println("setNextLinkRendererInternal=" + link);
		swinlanes.setNextLinkRenderer(Objects.requireNonNull(link));
	}

	private void setNextLink(LinkRendering linkRenderer) {
		Objects.requireNonNull(linkRenderer);
		// System.err.println("setNextLink=" + linkRenderer);
		if (current() instanceof InstructionCollection) {
			final Instruction last = ((InstructionCollection) current()).getLast();
			if (last instanceof InstructionWhile) {
				((InstructionWhile) last).outColor(linkRenderer.getRainbow());
			} else if (last instanceof InstructionIf) {
				((InstructionIf) last).outColor(linkRenderer);
			}
		}
		this.setNextLinkRendererInternal(linkRenderer);
	}

	public void setLabelNextArrow(Display label) {
//		if (current() instanceof InstructionRepeat && ((InstructionRepeat) current()).hasBackward()) {
//			final InstructionRepeat instructionRepeat = (InstructionRepeat) current();
//			instructionRepeat.setBackwardArrowLabel(label);
//			return;
//		}
		if (current() instanceof InstructionWhile && ((InstructionWhile) current()).getLast() == null) {
			((InstructionWhile) current()).overwriteYes(label);
			return;
		}

		setNextLinkRendererInternal(nextLinkRenderer().withDisplay(label));
	}

	public void setColorNextArrow(Rainbow color) {
		if (color == null) {
			return;
		}
		final LinkRendering link = new LinkRendering(color);
		setNextLink(link);
	}

	public CommandExecutionResult addNote(Display note, NotePosition position, NoteType type, Colors colors) {
		final boolean ok = current().addNote(note, position, type, colors, swinlanes.getCurrentSwimlane());
		if (ok == false) {
			return CommandExecutionResult.error("Cannot add note here");
		}
		manageHasUrl(note);
		return CommandExecutionResult.ok();
	}

	private boolean hasUrl = false;

	private void manageHasUrl(Display display) {
		if (display.hasUrl()) {
			hasUrl = true;
		}
	}

	@Override
	public boolean hasUrl() {
		return hasUrl;
	}

}
