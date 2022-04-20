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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class UGraphicNewpages extends UGraphicDelegator {

	private final double ymin;
	private final double ymax;
	private final double dy;

	private UGraphicNewpages create(UGraphic ug, double ymin, double ymax) {
		return new UGraphicNewpages(ug, ymin, ymax, 0);
	}

	private UGraphicNewpages(UGraphic ug, double ymin, double ymax, double dy) {
		super(ug);
		this.ymin = ymin;
		this.ymax = ymax;
		this.dy = dy;
	}

	public void draw(UShape shape) {
		System.err.println("UGraphicNewpages " + shape.getClass());
		if (dy >= ymin && dy < ymax) {
			getUg().draw(shape);
		} else {
			System.err.println("Removing " + shape);
		}

	}

	public UGraphic apply(UChange change) {
		double newdy = dy;
		if (change instanceof UTranslate) {
			newdy += ((UTranslate) change).getDy();
		}
		return new UGraphicNewpages(getUg().apply(change), ymin, ymax, newdy);
	}

}
