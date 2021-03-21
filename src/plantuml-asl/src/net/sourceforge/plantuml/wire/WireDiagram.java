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
package net.sourceforge.plantuml.wire;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.TikzFontDistortion;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class WireDiagram extends UmlDiagram {

	private final WBlock root = new WBlock("", new UTranslate(), 0, 0, null);
	private final List<Spot> spots = new ArrayList<Spot>();
	private final List<WLinkHorizontal> hlinks = new ArrayList<WLinkHorizontal>();
	private final List<WLinkVertical> vlinks = new ArrayList<WLinkVertical>();

	public DiagramDescription getDescription() {
		return new DiagramDescription("Wire Diagram");
	}

	public WireDiagram() {
		super(UmlDiagramType.WIRE);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final Scale scale = getScale();

		final double dpiFactor = scale == null ? getScaleCoef(fileFormatOption) : scale.getScale(100, 100);
		final ISkinParam skinParam = getSkinParam();

		final int margin1;
		final int margin2;
		if (UseStyle.useBetaStyle()) {
			margin1 = SkinParam.zeroMargin(10);
			margin2 = SkinParam.zeroMargin(10);
		} else {
			margin1 = 10;
			margin2 = 10;
		}
		HColor backcolor = skinParam.getBackgroundColor(false);

		final ClockwiseTopRightBottomLeft margins = ClockwiseTopRightBottomLeft.margin1margin2(margin1, margin2);
		final String metadata = fileFormatOption.isWithMetadata() ? getMetadata() : null;
		final ImageParameter imageParameter = new ImageParameter(skinParam.getColorMapper(), skinParam.handwritten(),
				null, dpiFactor, metadata, "", margins, backcolor);

		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);
		TextBlock result = getTextBlock();

		result = new AnnotatedWorker(this, skinParam, fileFormatOption.getDefaultStringBounder(getSkinParam()))
				.addAdd(result);
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
				// return getDrawingElement().calculateDimension(stringBounder);
				throw new UnsupportedOperationException();

			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HColor getBackcolor() {
				return null;
			}
		};
	}

	private void drawMe(UGraphic ug) {
		root.drawMe(ug);
		for (Spot spot : spots) {
			spot.drawMe(ug);
		}
		for (WLinkHorizontal link : hlinks) {
			link.drawMe(ug);
		}
		for (WLinkVertical link : vlinks) {
			link.drawMe(ug);
		}

	}

	public CommandExecutionResult addComponent(String indent, String name, int width, int height, HColor color) {
		final int level = computeIndentationLevel(indent);
		return this.root.addBlock(level, name, width, height, color);
	}

	public CommandExecutionResult newColumn(String indent) {
		final int level = computeIndentationLevel(indent);
		return this.root.newColumn(level);
	}

	public CommandExecutionResult spot(String name, HColor color, String x, String y) {
		final WBlock block = this.root.getBlock(name);
		if (block == null) {
			return CommandExecutionResult.error("No such element " + name);
		}
		final Spot spot = new Spot(block, color, x, y);
		this.spots.add(spot);
		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult wgoto(String indent, double x, double y) {
		final int level = computeIndentationLevel(indent);
		return this.root.wgoto(level, x, y);
	}

	public CommandExecutionResult wmove(String indent, double x, double y) {
		final int level = computeIndentationLevel(indent);
		return this.root.wmove(level, x, y);
	}

	public CommandExecutionResult print(String indent, String text) {
		final int level = computeIndentationLevel(indent);

		final StringBounder stringBounder = FileFormat.PNG.getDefaultStringBounder(TikzFontDistortion.getDefault());
		return this.root.print(stringBounder, getSkinParam(), level, text);
	}

	private int computeIndentationLevel(String indent) {
		final int level = indent.replace("    ", "\t").length();
		return level;
	}

	public CommandExecutionResult vlink(String name1, String x1, String y1, String name2, WLinkType type,
			WArrowDirection direction, HColor color, Display label) {
		final WBlock block1 = this.root.getBlock(name1);
		if (block1 == null) {
			return CommandExecutionResult.error("No such element " + name1);
		}
		final WBlock block2 = this.root.getBlock(name2);
		if (block2 == null) {
			return CommandExecutionResult.error("No such element " + name2);
		}

		final UTranslate start = block1.getNextOutVertical(x1, y1, type);
		final double destination = block2.getAbsolutePosition("0", "0").getDy();

		this.vlinks.add(new WLinkVertical(getSkinParam(), start, destination, type, direction, color, label));

		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult hlink(String name1, String x1, String y1, String name2, WLinkType type,
			WArrowDirection direction, HColor color, Display label) {
		final WBlock block1 = this.root.getBlock(name1);
		if (block1 == null) {
			return CommandExecutionResult.error("No such element " + name1);
		}
		final WBlock block2 = this.root.getBlock(name2);
		if (block2 == null) {
			return CommandExecutionResult.error("No such element " + name2);
		}

		final UTranslate start = block1.getNextOutHorizontal(x1, y1, type);
		final double destination = block2.getAbsolutePosition("0", "0").getDx();

		this.hlinks.add(new WLinkHorizontal(getSkinParam(), start, destination, type, direction, color, label));

		return CommandExecutionResult.ok();
	}

}
