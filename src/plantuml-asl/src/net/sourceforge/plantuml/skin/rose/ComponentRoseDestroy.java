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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.ISkinSimple;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;

public class ComponentRoseDestroy extends AbstractComponent {

	private final HColor foregroundColor;

	public ComponentRoseDestroy(Style style, HColor foregroundColor, ISkinSimple spriteContainer) {
		super(style);
		if (style != null)
			this.foregroundColor = style.value(PName.LineColor).asColor(spriteContainer.getIHtmlColorSet());
		else
			this.foregroundColor = foregroundColor;
	}

	private final int crossSize = 9;

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		ug = ug.apply(UStroke.withThickness(2)).apply(foregroundColor);

		ug.draw(new ULine(2 * crossSize, 2 * crossSize));
		ug.apply(UTranslate.dy(2 * crossSize)).draw(new ULine(2 * crossSize, -2 * crossSize));
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
