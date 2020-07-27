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
package net.sourceforge.plantuml.ugraphic;

import net.sourceforge.plantuml.graphic.UGraphicDelegator;

public abstract class AbstractUGraphicHorizontalLine extends UGraphicDelegator {

	private UTranslate translate = new UTranslate();

	public UGraphic apply(UChange change) {
		final AbstractUGraphicHorizontalLine result;
		if (change instanceof UTranslate) {
			result = copy(getUg());
			result.translate = this.translate.compose((UTranslate) change);
		} else if (change instanceof UClip) {
			final UClip clip = ((UClip) change).translate(translate);
			result = copy(getUg().apply(clip));
			result.translate = this.translate;
		} else {
			result = copy(getUg().apply(change));
			result.translate = this.translate;
		}
		return result;
	}

	protected abstract AbstractUGraphicHorizontalLine copy(UGraphic ug);

	protected AbstractUGraphicHorizontalLine(UGraphic ug) {
		super(ug);
	}

	protected abstract void drawHline(UGraphic ug, UHorizontalLine line, UTranslate translate);

	public void draw(UShape shape) {
		if (shape instanceof UHorizontalLine) {
			drawHline(getUg(), (UHorizontalLine) shape, UTranslate.dy(translate.getDy()));
		} else {
			getUg().apply(translate).draw(shape);
		}
	}

}
