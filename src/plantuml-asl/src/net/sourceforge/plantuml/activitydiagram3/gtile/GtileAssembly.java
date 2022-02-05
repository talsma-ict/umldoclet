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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.awt.geom.Dimension2D;
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GtileAssembly extends GtileTopDown {

	private final TextBlock textBlock;

	public GtileAssembly(Gtile tile1, Gtile tile2, LinkRendering linkRendering) {
		super(tile1, tile2);
		this.textBlock = getTextBlock(linkRendering.getDisplay());
		// See FtileFactoryDelegatorAssembly
	}

	@Override
	protected UTranslate supplementaryMove() {
		final double height = 30 + textBlock.calculateDimension(stringBounder).getHeight();
		return new UTranslate(0, height);
	}

	final public StyleSignature getDefaultStyleDefinitionArrow() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D raw = super.calculateDimension(stringBounder);
		final double textBlockWidth = textBlock.calculateDimension(stringBounder).getWidth();
		final double pos1 = tile1.getCoord(GPoint.SOUTH_HOOK).compose(getPos1()).getDx();
		return Dimension2DDouble.atLeast(raw, pos1 + textBlockWidth, 0);
	}

	protected final TextBlock getTextBlock(Display display) {
		// DUP3945
		if (Display.isNull(display)) {
			return TextBlockUtils.EMPTY_TEXT_BLOCK;
		}
		final FontConfiguration fontConfiguration;
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam().getCurrentStyleBuilder());
			fontConfiguration = style.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
		} else {
			fontConfiguration = new FontConfiguration(skinParam(), FontParam.ARROW, null);
		}
		return display.create7(fontConfiguration, HorizontalAlignment.LEFT, skinParam(), CreoleMode.SIMPLE_LINE);
	}

	@Override
	public Collection<GConnection> getInnerConnections() {
		final GConnection arrow = new GConnectionVerticalDown(getPos1(), tile1.getGPoint(GPoint.SOUTH_HOOK), getPos2(),
				tile2.getGPoint(GPoint.NORTH_HOOK), textBlock);
		return Collections.singletonList(arrow);
	}

}
