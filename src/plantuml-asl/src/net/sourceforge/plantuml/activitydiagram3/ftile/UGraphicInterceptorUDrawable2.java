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

import java.awt.geom.Point2D;
import java.util.Map;

import net.sourceforge.plantuml.activitydiagram3.gtile.GConnection;
import net.sourceforge.plantuml.activitydiagram3.gtile.Gtile;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.svek.UGraphicForSnake;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class UGraphicInterceptorUDrawable2 extends UGraphicDelegator {

	private final Map<String, UTranslate> positions;

	public UGraphicInterceptorUDrawable2(UGraphic ug, Map<String, UTranslate> positions) {
		super(ug);
		this.positions = positions;
	}

	public void draw(UShape shape) {
		if (shape instanceof Gtile) {
			final Gtile gtile = (Gtile) shape;
			// System.err.println("gtile=" + gtile);
			gtile.drawU(this);
		} else if (shape instanceof Ftile) {
			final Ftile ftile = (Ftile) shape;
			// System.err.println("ftile=" + ftile);
			ftile.drawU(this);
			if (ftile instanceof FtileLabel) {
				positions.put(((FtileLabel) ftile).getName(), getPosition());
				// System.err.println("ug=" + getUg().getClass());
			}
			if (ftile instanceof FtileGoto) {
				// System.err.println("positions=" + positions);
				drawGoto((FtileGoto) ftile);
			}
		} else if (shape instanceof UDrawable) {
			final UDrawable drawable = (UDrawable) shape;
			drawable.drawU(this);
		} else {
			getUg().draw(shape);
		}

	}

	private UTranslate getPosition() {
		return ((UGraphicForSnake) getUg()).getTranslation();
	}

	private void drawGoto(FtileGoto ftile) {
		final HColor gotoColor = HColorUtils.MY_RED;

		final FtileGeometry geom = ftile.calculateDimension(getStringBounder());
		final Point2D pt = geom.getPointIn();
		UGraphic ugGoto = getUg().apply(gotoColor).apply(gotoColor.bg());
		ugGoto = ugGoto.apply(new UTranslate(pt));
		final UTranslate posNow = getPosition();
		final UTranslate dest = positions.get(ftile.getName());
		final double dx = dest.getDx() - posNow.getDx();
		final double dy = dest.getDy() - posNow.getDy();
		ugGoto.draw(new UEllipse(3, 3));
		ugGoto.apply(new UTranslate(dx, dy)).draw(new UEllipse(3, 3));
		ugGoto.draw(ULine.hline(dx));
		ugGoto.apply(UTranslate.dx(dx)).draw(ULine.vline(dy));
		// ugGoto.draw(new ULine(dx, dy));
	}

	public UGraphic apply(UChange change) {
		return new UGraphicInterceptorUDrawable2(getUg().apply(change), positions);
	}

}
