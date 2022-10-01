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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GtileSplit extends GtileColumns {

	private final HColor lineColor;

	public GtileSplit(List<Gtile> gtiles, Swimlane singleSwimlane, HColor lineColor) {
		super(gtiles, singleSwimlane, 20);
		this.lineColor = lineColor;

	}

	final public StyleSignatureBasic getDefaultStyleDefinitionActivity() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.activity);
	}

	final static public StyleSignatureBasic getDefaultStyleDefinitionDiamond() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.activity, SName.diamond);
	}

	final public StyleSignatureBasic getDefaultStyleDefinitionArrow() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		super.drawUInternal(ug);

		final double x0 = gtiles.get(0).getCoord(GPoint.NORTH_HOOK).compose(getPosition(0)).getDx();

		final int last = gtiles.size() - 1;
		final double xLast = gtiles.get(last).getCoord(GPoint.NORTH_HOOK).compose(getPosition(last)).getDx();
		final ULine hline = ULine.hline(xLast - x0);

		ug = ug.apply(lineColor).apply(new UStroke(1.5));
		ug.apply(UTranslate.dx(x0)).draw(hline);

		final double y = getCoord(GPoint.SOUTH_BORDER).getDy();
		ug.apply(new UTranslate(x0, y)).draw(hline);

	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return XDimension2D.delta(super.calculateDimension(stringBounder), 0, 0);
	}

//	@Override
//	public Collection<GConnection> getInnerConnections() {
//
//		final GConnection arrow1 = new GConnectionVerticalDown(getPos1(), tile1.getGPoint(GPoint.SOUTH_HOOK), getPos2(),
//				tile2.getGPoint(GPoint.NORTH_HOOK), TextBlockUtils.EMPTY_TEXT_BLOCK);
//		final GConnection arrow2 = new GConnectionVerticalDown(getPos2(), tile2.getGPoint(GPoint.SOUTH_HOOK), getPos3(),
//				tile3.getGPoint(GPoint.NORTH_HOOK), TextBlockUtils.EMPTY_TEXT_BLOCK);
//
//		final double xright = calculateDimension(stringBounder).getWidth();
//
//		final GConnection arrow3 = new GConnectionLeftThenVerticalThenRight(getPos1(), tile1.getGPoint(GPoint.EAST_HOOK),
//				getPos3(), tile3.getGPoint(GPoint.EAST_HOOK), xright, TextBlockUtils.EMPTY_TEXT_BLOCK);
//
//		return Arrays.asList(arrow1, arrow2, arrow3);
//	}

}
