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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Pragma;
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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.Styleable;
import net.sourceforge.plantuml.svek.UGraphicForSnake;
import net.sourceforge.plantuml.ugraphic.LimitFinder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.comp.CompressionMode;
import net.sourceforge.plantuml.ugraphic.comp.SlotFinder;
import net.sourceforge.plantuml.ugraphic.comp.SlotSet;
import net.sourceforge.plantuml.utils.MathUtils;

public class SwimlanesA extends AbstractTextBlock implements ISwimlanesA, TextBlock, Styleable {

	protected final ISkinParam skinParam;;
	private final Pragma pragma;

	protected final List<Swimlane> swimlanes = new ArrayList<Swimlane>();
	private Swimlane currentSwimlane = null;

	private final Instruction root = new InstructionList();
	private Instruction currentInstruction = root;

	private LinkRendering nextLinkRenderer = LinkRendering.none();
	private Style style;

	public StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.classDiagram, SName.swimlane);
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

	public void swimlane(String name, HColor color, Display label) {
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

	protected double separationMargin() {
		return 10;
	}

	// private TextBlock full;

	public final void computeSize(StringBounder stringBounder) {
		final SlotFinder ug = new SlotFinder(CompressionMode.ON_Y, stringBounder);
		if (swimlanes.size() > 1) {
			TextBlock full = root.createFtile(getFtileFactory(stringBounder));
			computeSizeInternal(ug, full);
		}

	}

	public final void drawU(UGraphic ug) {
		TextBlock full = root.createFtile(getFtileFactory(ug.getStringBounder()));

		ug = new UGraphicForSnake(ug);
		if (swimlanes.size() > 1) {
			drawWhenSwimlanes(ug, full);
		} else {
			// BUG42
			full = new TextBlockInterceptorUDrawable(full);
			full.drawU(ug);
			ug.flushUg();
		}
	}

	static private void printDebug(UGraphic ug, SlotSet slot, HColor col, TextBlock full) {
		slot.drawDebugX(ug.apply(col).apply(col.bg()), full.calculateDimension(ug.getStringBounder()).getHeight());

	}

	protected void drawWhenSwimlanes(final UGraphic ug, TextBlock full) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionFull = full.calculateDimension(stringBounder);

		double x2 = 0;
		for (Swimlane swimlane : swimlanes) {
			final HColor back = swimlane.getColors(skinParam).getColor(ColorType.BACK);
			if (back != null) {
				UGraphic background = ug.apply(back.bg()).apply(back);
				background = background.apply(UTranslate.dx(x2));
				drawBackColor(background, swimlane, dimensionFull);
			}

			full.drawU(new UGraphicInterceptorOneSwimlane(ug, swimlane, swimlanes).apply(swimlane.getTranslate())
					.apply(getTitleHeightTranslate(stringBounder)));
			x2 += swimlane.getActualWidth();

		}
		final Cross cross = new Cross(ug.apply(getTitleHeightTranslate(stringBounder)));
		full.drawU(cross);
		cross.flushUg();
	}

	protected void drawBackColor(UGraphic ug, Swimlane swimlane, Dimension2D dimensionFull) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double height = dimensionFull.getHeight() + getTitleHeightTranslate(stringBounder).getDy();
		final URectangle rectangle = new URectangle(swimlane.getActualWidth(), height).ignoreForCompressionOnX()
				.ignoreForCompressionOnY();
		ug.draw(rectangle);
	}

	protected UTranslate getTitleHeightTranslate(final StringBounder stringBounder) {
		return new UTranslate();
	}

	private void computeDrawingWidths(UGraphic ug, TextBlock full) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (Swimlane swimlane : swimlanes) {
			final LimitFinder limitFinder = new LimitFinder(stringBounder, false);
			final UGraphicInterceptorOneSwimlane interceptor = new UGraphicInterceptorOneSwimlane(
					new UGraphicForSnake(limitFinder), swimlane, swimlanes);
			full.drawU(interceptor);
			interceptor.flushUg();
			final MinMax minMax = limitFinder.getMinMax();
			swimlane.setMinMax(minMax);
		}
	}

	private void computeSizeInternal(UGraphic ug, TextBlock full) {
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

			final UTranslate translate = UTranslate.dx(x1 - swimlane.getMinMax().getMinX() + separationMargin()
					+ (swimlaneActualWidth - rawDrawingWidth(swimlane)) / 2.0);
			swimlane.setTranslate(translate);
			swimlane.setWidth(swimlaneActualWidth);

			x1 += swimlaneActualWidth;
		}
	}

	protected double swimlaneActualWidth(StringBounder stringBounder, double swimlaneWidth, Swimlane swimlane) {
		return MathUtils.max(swimlaneWidth, rawDrawingWidth(swimlane));
	}

	private double rawDrawingWidth(Swimlane swimlane) {
		return swimlane.getMinMax().getWidth() + 2 * separationMargin();
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
