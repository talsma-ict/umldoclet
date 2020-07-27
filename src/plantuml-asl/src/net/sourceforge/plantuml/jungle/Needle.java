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
package net.sourceforge.plantuml.jungle;

import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class Needle implements UDrawable {

	private final double length;
	private final Display display;
	private final double degreePosition;
	private final double degreeOperture;

	private Needle(Display display, double length, double degreePosition, double degreeOperture) {
		this.display = display;
		this.degreePosition = degreePosition;
		this.degreeOperture = degreeOperture;
		this.length = length;
	}

	public void drawU(UGraphic ug) {
		GTileNode.getTextBlock(display);
		ug.draw(getLine());

		ug = ug.apply(getTranslate(length));
		GTileNode.getTextBlock(display).drawU(ug);
	}

	private ULine getLine() {
		final UTranslate translate = getTranslate(length);
		return new ULine(translate.getDx(), translate.getDy());
	}

	public UTranslate getTranslate(double dist) {
		final double angle = degreePosition * Math.PI / 180.0;
		final double dx = dist * Math.cos(angle);
		final double dy = dist * Math.sin(angle);
		return new UTranslate(dx, dy);
	}

	public UDrawable addChildren(final List<GNode> children) {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				Needle.this.drawU(ug);
				if (children.size() == 0) {
					return;
				}
				ug = ug.apply(getTranslate(length / 2));
				final UDrawable child1 = getNeedle(children.get(0), length / 2, degreePosition + degreeOperture,
						degreeOperture / 2);
				child1.drawU(ug);
				if (children.size() == 1) {
					return;
				}
				final UDrawable child2 = getNeedle(children.get(1), length / 2, degreePosition - degreeOperture,
						degreeOperture / 2);
				child2.drawU(ug);

			}
		};
	}

	public static UDrawable getNeedle(GNode root, double length, double degree, double degreeOperture) {
		final Needle needle0 = new Needle(root.getDisplay(), length, degree, degreeOperture);
		final UDrawable n1 = needle0.addChildren(root.getChildren());
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				ug = ug.apply(HColorUtils.BLACK);
				n1.drawU(ug);
			}
		};
	}

}
