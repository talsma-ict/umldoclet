/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import static net.sourceforge.plantuml.utils.ObjectUtils.instanceOfAny;

import net.sourceforge.plantuml.klimt.UBackground;
import net.sourceforge.plantuml.klimt.UChange;
import net.sourceforge.plantuml.klimt.UShape;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphicNo;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.shape.URectangle;

public class ZadBuilder extends UGraphicNo {

	@Override
	public UGraphic apply(UChange change) {
		return new ZadBuilder(this, change);
	}

	private final Context context;

	static class Context {
		private final Zad zad = new Zad();
	}

	public ZadBuilder(StringBounder stringBounder) {
		super(stringBounder, UTranslate.none());
		this.context = new Context();
	}

	private ZadBuilder(ZadBuilder other, UChange change) {
		// super(other, change);
		super(other.getStringBounder(), change instanceof UTranslate ? other.getTranslate().compose((UTranslate) change)
				: other.getTranslate());
		if (!instanceOfAny(change, UBackground.class, HColor.class, UStroke.class, UTranslate.class))
			throw new UnsupportedOperationException(change.getClass().toString());

		this.context = other.context;
	}

	public void draw(UShape shape) {
		if (shape instanceof URectangle) {
			drawRectangle((URectangle) shape);
		}
	}

	private void drawRectangle(URectangle shape) {
		final MinMax area = shape.getMinMax().translate(getTranslate());
		// System.err.println("ZadBuilder " + shape + " " + area);
		context.zad.add(area);
	}

	public Zad getZad() {
		return context.zad;
	}

}
