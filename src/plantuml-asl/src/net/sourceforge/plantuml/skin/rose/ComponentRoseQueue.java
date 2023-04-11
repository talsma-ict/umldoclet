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

import net.sourceforge.plantuml.decoration.symbol.USymbols;
import net.sourceforge.plantuml.klimt.Fashion;
import net.sourceforge.plantuml.klimt.LineBreakStrategy;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.ISkinSimple;
import net.sourceforge.plantuml.style.Style;

public class ComponentRoseQueue extends AbstractTextualComponent {

	private final TextBlock stickman;
	private final boolean head;

	public ComponentRoseQueue(Style style, Style stereo, Display stringsToDisplay, boolean head,
			ISkinSimple spriteContainer) {
		super(style, stereo, LineBreakStrategy.NONE, 3, 3, 0, spriteContainer, stringsToDisplay, false);

		final Fashion biColor = style.getSymbolContext(getIHtmlColorSet());

		this.head = head;
		this.stickman = USymbols.QUEUE.asSmall(TextBlockUtils.empty(0, 0), getTextBlock(), TextBlockUtils.empty(0, 0),
				biColor, HorizontalAlignment.CENTER);
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimStickman = stickman.calculateDimension(stringBounder);
		final double delta = (getPreferredWidth(stringBounder) - dimStickman.getWidth()) / 2;

		ug = ug.apply(UTranslate.dx(delta));
		stickman.drawU(ug);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		final XDimension2D dimStickman = stickman.calculateDimension(stringBounder);
		return dimStickman.getHeight();
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		final XDimension2D dimStickman = stickman.calculateDimension(stringBounder);
		return dimStickman.getWidth();
	}

}
