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
package net.sourceforge.plantuml.mindmap;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class MindMapDiagram extends UmlDiagram {

	private Branch left = new Branch();
	private Branch right = new Branch();

	private Direction defaultDirection = Direction.RIGHT;

	public final void setDefaultDirection(Direction defaultDirection) {
		this.defaultDirection = defaultDirection;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("MindMap");
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.MINDMAP;
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final Scale scale = getScale();

		final double dpiFactor = scale == null ? getScaleCoef(fileFormatOption) : scale.getScale(100, 100);
		final ISkinParam skinParam = getSkinParam();
		final ImageBuilder imageBuilder = new ImageBuilder(skinParam.getColorMapper(), dpiFactor,
				skinParam.getBackgroundColor(), fileFormatOption.isWithMetadata() ? getMetadata() : null, "", 10, 10,
				null, skinParam.handwritten());
		TextBlock result = getTextBlock();

		result = new AnnotatedWorker(this, skinParam, fileFormatOption.getDefaultStringBounder()).addAdd(result);
		imageBuilder.setUDrawable(result);

		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, seed(), os);
	}

	private TextBlockBackcolored getTextBlock() {
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				drawMe(ug);
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				computeFinger();
				final double y1 = right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2;
				final double y2 = left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2;
				final double y = Math.max(y1, y2);

				final double x = left.finger == null ? 0 : left.finger.getFullElongation(stringBounder);

				final double width = right.finger == null ? x : x + right.finger.getFullElongation(stringBounder);
				final double height = y
						+ Math.max(left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2,
								right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2);
				return new Dimension2DDouble(width, height);

			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HtmlColor getBackcolor() {
				return null;
			}
		};
	}

	private void drawMe(UGraphic ug) {
		computeFinger();

		final StringBounder stringBounder = ug.getStringBounder();
		final double y1 = right.finger == null ? 0 : right.finger.getFullThickness(stringBounder) / 2;
		final double y2 = left.finger == null ? 0 : left.finger.getFullThickness(stringBounder) / 2;
		final double y = Math.max(y1, y2);

		final double x = left.finger == null ? 0 : left.finger.getFullElongation(stringBounder)
				+ ((FingerImpl2) left.finger).getX12();
		if (right.finger != null) {
			right.finger.drawU(ug.apply(new UTranslate(x, y)));
		}
		if (left.finger != null) {
			left.finger.drawU(ug.apply(new UTranslate(x, y)));
		}
	}

	private void computeFinger() {
		if (left.finger == null && right.finger == null) {
			if (left.root.hasChildren()) {
				left.finger = FingerImpl2.build(left.root, getSkinParam(), Direction.LEFT);
			}
			if (left.finger == null || right.root.hasChildren()) {
				right.finger = FingerImpl2.build(right.root, getSkinParam(), Direction.RIGHT);
			}
			if (left.finger != null && right.finger != null) {
				left.finger.doNotDrawFirstPhalanx();
			}
		}
	}

	public CommandExecutionResult addIdea(HtmlColor backColor, int level, String label, IdeaShape shape) {
		return addIdea(backColor, level, label, shape, defaultDirection);
	}

	public CommandExecutionResult addIdea(HtmlColor backColor, int level, String label, IdeaShape shape,
			Direction direction) {
		if (level == 0) {
			if (this.right.root != null) {
				return CommandExecutionResult
						.error("I don't know how to draw multi-root diagram. You should suggest an image so that the PlantUML team implements it :-)");
			}
			right.initRoot(label, shape);
			left.initRoot(label, shape);
			return CommandExecutionResult.ok();
		}
		if (direction == Direction.LEFT) {
			return left.add(backColor, level, label, shape);
		}
		return right.add(backColor, level, label, shape);
	}

	static class Branch {
		private Idea root;
		private Idea last;
		private Finger finger;

		private void initRoot(String label, IdeaShape shape) {
			root = new Idea(Display.getWithNewlines(label), shape);
			last = root;
		}

		private Idea getParentOfLast(int nb) {
			Idea result = last;
			for (int i = 0; i < nb; i++) {
				result = result.getParent();
			}
			return result;
		}

		private CommandExecutionResult add(HtmlColor backColor, int level, String label, IdeaShape shape) {
			if (level == last.getLevel() + 1) {
				final Idea newIdea = last.createIdea(backColor, level, Display.getWithNewlines(label), shape);
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			if (level <= last.getLevel()) {
				final int diff = last.getLevel() - level + 1;
				final Idea newIdea = getParentOfLast(diff).createIdea(backColor, level, Display.getWithNewlines(label), shape);
				last = newIdea;
				return CommandExecutionResult.ok();
			}
			return CommandExecutionResult.error("error42L");
		}

	}

}
