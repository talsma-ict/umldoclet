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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.activitydiagram3.Instruction;
import net.sourceforge.plantuml.activitydiagram3.InstructionList;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAddNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAddUrl;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAssembly;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorCreateGroup;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorCreateParallel;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorIf;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorRepeat;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorSwitch;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorWhile;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.UGraphicInterceptorOneSwimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.VCompactFactory;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleDefinition;
import net.sourceforge.plantuml.style.Styleable;
import net.sourceforge.plantuml.svek.UGraphicForSnake;
import net.sourceforge.plantuml.ugraphic.LimitFinder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.comp.SlotSet;
import net.sourceforge.plantuml.utils.MathUtils;

public class SwimlanesA extends AbstractTextBlock implements TextBlock, Styleable {

	protected final ISkinParam skinParam;;
	private final Pragma pragma;

	protected final List<Swimlane> swimlanes = new ArrayList<Swimlane>();
	private Swimlane currentSwimlane = null;

	private final Instruction root = new InstructionList();
	private Instruction currentInstruction = root;

	private LinkRendering nextLinkRenderer = LinkRendering.none();
	private Style style;

	public StyleDefinition getDefaultStyleDefinition() {
		return StyleDefinition.of(SName.root, SName.element, SName.classDiagram, SName.swimlane);
	}

	public SwimlanesA(ISkinParam skinParam, Pragma pragma) {
		this.skinParam = skinParam;
		this.pragma = pragma;
	}

	protected Style getStyle() {
		if (style == null) {
			this.style = getDefaultStyleDefinition().getMergedStyle(skinParam.getCurrentStyleBuilder());
		}
		return style;
	}

	private FtileFactory getFtileFactory(StringBounder stringBounder) {
		FtileFactory factory = new VCompactFactory(skinParam, stringBounder);
		factory = new FtileFactoryDelegatorAddUrl(factory);
		factory = new FtileFactoryDelegatorAssembly(factory);
		factory = new FtileFactoryDelegatorIf(factory, pragma);
		factory = new FtileFactoryDelegatorSwitch(factory);
		factory = new FtileFactoryDelegatorWhile(factory);
		factory = new FtileFactoryDelegatorRepeat(factory);
		factory = new FtileFactoryDelegatorCreateParallel(factory);
		// factory = new FtileFactoryDelegatorCreateParallelAddingMargin(new
		// FtileFactoryDelegatorCreateParallel1(factory));
		factory = new FtileFactoryDelegatorAddNote(factory);
		factory = new FtileFactoryDelegatorCreateGroup(factory);
		return factory;
	}

	public void swimlane(String name, HtmlColor color, Display label) {
		currentSwimlane = getOrCreate(name);
		if (color != null) {
			currentSwimlane.setSpecificColorTOBEREMOVED(ColorType.BACK, color);
		}
		if (Display.isNull(label) == false) {
			currentSwimlane.setDisplay(label);
		}
	}

	private Swimlane getOrCreate(String name) {
		for (Swimlane s : swimlanes) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		final Swimlane result = new Swimlane(name);
		swimlanes.add(result);
		return result;
	}

	class Cross extends UGraphicDelegator {

		private Cross(UGraphic ug) {
			super(ug);
		}

		@Override
		public void draw(UShape shape) {
			if (shape instanceof Ftile) {
				final Ftile tile = (Ftile) shape;
				tile.drawU(this);
			} else if (shape instanceof Connection) {
				final Connection connection = (Connection) shape;
				final Ftile tile1 = connection.getFtile1();
				final Ftile tile2 = connection.getFtile2();

				if (tile1 == null || tile2 == null) {
					return;
				}
				if (tile1.getSwimlaneOut() != tile2.getSwimlaneIn()) {
					final ConnectionCross connectionCross = new ConnectionCross(connection);
					connectionCross.drawU(getUg());
				}
			}
		}

		public UGraphic apply(UChange change) {
			return new Cross(getUg().apply(change));
		}

	}

	static protected final double separationMargin = 10;

	private TextBlock full;

	public void drawU(UGraphic ug) {
		if (full == null) {
			final FtileFactory factory = getFtileFactory(ug.getStringBounder());
			full = root.createFtile(factory);
			if (swimlanes.size() <= 1) {
				// BUG42
				full = new TextBlockInterceptorUDrawable(full);
			}
		}

		ug = new UGraphicForSnake(ug);
		if (swimlanes.size() <= 1) {
			full.drawU(ug);
			ug.flushUg();
			return;
		}

		drawWhenSwimlanes(ug, full);
	}

	static private void printDebug(UGraphic ug, SlotSet slot, HtmlColor col, TextBlock full) {
		slot.drawDebugX(ug.apply(new UChangeColor(col)).apply(new UChangeBackColor(col)),
				full.calculateDimension(ug.getStringBounder()).getHeight());

	}

	protected void drawWhenSwimlanes(final UGraphic ug, TextBlock full) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionFull = full.calculateDimension(stringBounder);

		computeSize(ug, full);
		final UTranslate titleHeightTranslate = getTitleHeightTranslate(stringBounder);

		double x2 = 0;
		for (Swimlane swimlane : swimlanes) {
			final HtmlColor back = swimlane.getColors(skinParam).getColor(ColorType.BACK);
			if (back != null) {
				final UGraphic background = ug.apply(new UChangeBackColor(back)).apply(new UChangeColor(back))
						.apply(new UTranslate(x2, 0));
				background.draw(new URectangle(swimlane.getActualWidth(), dimensionFull.getHeight()
						+ titleHeightTranslate.getDy()));
			}

			full.drawU(new UGraphicInterceptorOneSwimlane(ug, swimlane).apply(swimlane.getTranslate()).apply(
					titleHeightTranslate));
			x2 += swimlane.getActualWidth();

		}
		final Cross cross = new Cross(ug.apply(titleHeightTranslate));
		full.drawU(cross);
		cross.flushUg();
	}

	protected UTranslate getTitleHeightTranslate(final StringBounder stringBounder) {
		return new UTranslate();
	}

	private void computeDrawingWidths(UGraphic ug, TextBlock full) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (Swimlane swimlane : swimlanes) {
			final LimitFinder limitFinder = new LimitFinder(stringBounder, false);
			final UGraphicInterceptorOneSwimlane interceptor = new UGraphicInterceptorOneSwimlane(new UGraphicForSnake(
					limitFinder), swimlane);
			full.drawU(interceptor);
			interceptor.flushUg();
			final MinMax minMax = limitFinder.getMinMax();
			swimlane.setMinMax(minMax);
		}
	}

	private void computeSize(UGraphic ug, TextBlock full) {
		computeDrawingWidths(ug, full);
		double x1 = 0;

		double swimlaneWidth = skinParam.swimlaneWidth();

		if (swimlaneWidth == ISkinParam.SWIMLANE_WIDTH_SAME) {
			for (Swimlane swimlane : swimlanes) {
				swimlaneWidth = Math.max(swimlaneWidth, rawDrawingWidth(swimlane));
			}

		}
		for (Swimlane swimlane : swimlanes) {
			final double swimlaneActualWidth = swimlaneActualWidth(ug.getStringBounder(), swimlaneWidth, swimlane);

			final UTranslate translate = new UTranslate(x1 - swimlane.getMinMax().getMinX() + separationMargin
					+ (swimlaneActualWidth - rawDrawingWidth(swimlane)) / 2.0, 0);
			swimlane.setTranslateAndWidth(translate, swimlaneActualWidth);

			x1 += swimlaneActualWidth;
		}
	}

	protected double swimlaneActualWidth(StringBounder stringBounder, double swimlaneWidth, Swimlane swimlane) {
		return MathUtils.max(swimlaneWidth, rawDrawingWidth(swimlane));
	}

	private double rawDrawingWidth(Swimlane swimlane) {
		return swimlane.getMinMax().getWidth() + 2 * separationMargin;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getMinMax(stringBounder).getDimension();
	}

	public Instruction getCurrent() {
		return currentInstruction;
	}

	public void setCurrent(Instruction current) {
		this.currentInstruction = current;
	}

	public LinkRendering nextLinkRenderer() {
		return nextLinkRenderer;
	}

	public void setNextLinkRenderer(LinkRendering link) {
		if (link == null) {
			throw new IllegalArgumentException();
		}
		this.nextLinkRenderer = link;
	}

	public Swimlane getCurrentSwimlane() {
		return currentSwimlane;
	}

	private MinMax cachedMinMax;

	@Override
	public MinMax getMinMax(StringBounder stringBounder) {
		if (cachedMinMax == null) {
			cachedMinMax = TextBlockUtils.getMinMax(this, stringBounder);
		}
		return cachedMinMax;
	}

}
