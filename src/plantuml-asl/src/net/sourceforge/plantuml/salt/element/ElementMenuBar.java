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
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class ElementMenuBar extends AbstractElement {

	private final Collection<ElementMenuEntry> entries = new ArrayList<ElementMenuEntry>();
	private final Map<ElementMenuEntry, ElementMenuPopup> popups = new HashMap<ElementMenuEntry, ElementMenuPopup>();
	private final UFont font;
	private final ISkinSimple spriteContainer;

	public ElementMenuBar(UFont font, ISkinSimple spriteContainer) {
		this.font = font;
		this.spriteContainer = spriteContainer;
	}

	public void addEntry(String s) {
		entries.add(new ElementMenuEntry(s, font, spriteContainer));
	}

	public void addSubEntry(String s, String sub) {
		final ElementMenuPopup popup = getPopup(getElementMenuEntry(s));
		popup.addEntry(sub);
	}

	private ElementMenuPopup getPopup(ElementMenuEntry s) {
		ElementMenuPopup popup = popups.get(s);
		if (popup == null) {
			popup = new ElementMenuPopup(font, spriteContainer);
			popups.put(s, popup);
		}
		return popup;
	}

	private ElementMenuEntry getElementMenuEntry(String n) {
		for (ElementMenuEntry entry : entries) {
			if (entry.getText().equals(n)) {
				return entry;
			}
		}
		throw new IllegalArgumentException();
	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		double w = 0;
		double h = 0;
		for (ElementMenuEntry entry : entries) {
			final Dimension2D dim = entry.getPreferredDimension(stringBounder, x, y);
			w += dim.getWidth() + 10;
			h = Math.max(h, dim.getHeight());
		}
		return new Dimension2DDouble(w, h);
	}

	public void drawU(UGraphic ug, int zIndex, Dimension2D dimToUse) {
		final Dimension2D preferred = getPreferredDimension(ug.getStringBounder(), 0, 0);

		double x1 = 0;
		if (zIndex == 0) {
			ug.apply(HColorSet.instance().getColorIfValid("#DDDDDD").bg()).draw(
					new URectangle(dimToUse.getWidth(), dimToUse.getHeight()));
			for (ElementMenuEntry entry : entries) {
				entry.drawU(ug.apply(UTranslate.dx(x1)), zIndex, dimToUse);
				final double w = entry.getPreferredDimension(ug.getStringBounder(), x1, 0).getWidth();
				entry.setX(x1);
				x1 += w + 10;
			}
			return;
		}

		if (zIndex == 1) {
			for (ElementMenuEntry entry : popups.keySet()) {
				entry.setBackground(HColorSet.instance().getColorIfValid("#BBBBBB"));
			}

			final double y1 = preferred.getHeight();
			for (Map.Entry<ElementMenuEntry, ElementMenuPopup> ent : popups.entrySet()) {
				final ElementMenuPopup p = ent.getValue();
				final double xpopup = ent.getKey().getX();
				p.drawU(ug.apply(new UTranslate(xpopup, y1)), zIndex,
						p.getPreferredDimension(ug.getStringBounder(), xpopup, y1));
			}
		}
	}
}
