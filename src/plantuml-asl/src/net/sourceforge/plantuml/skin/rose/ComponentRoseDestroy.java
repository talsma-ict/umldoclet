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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentRoseDestroy extends AbstractComponent {

	private final HtmlColor foregroundColor;

	public ComponentRoseDestroy(Style style, HtmlColor foregroundColor) {
		super(style);
		this.foregroundColor = foregroundColor;
	}

	private final int crossSize = 9;

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		ug = ug.apply(new UStroke(2)).apply(new UChangeColor(foregroundColor));

		ug.draw(new ULine(2 * crossSize, 2 * crossSize));
		ug.apply(new UTranslate(0, 2 * crossSize)).draw(new ULine(2 * crossSize, -2 * crossSize));
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return crossSize * 2;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return crossSize * 2;
	}

}
