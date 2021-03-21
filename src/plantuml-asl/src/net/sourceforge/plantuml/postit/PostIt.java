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
package net.sourceforge.plantuml.postit;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.skin.rose.ComponentRoseNote;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PostIt {

	private final String id;
	private final Display text;

	private Dimension2D minimumDimension;

	public PostIt(String id, Display text) {
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public Display getText() {
		return text;
	}

	public Dimension2D getMinimumDimension() {
		return minimumDimension;
	}

	public void setMinimumDimension(Dimension2D minimumDimension) {
		this.minimumDimension = minimumDimension;
	}

	public Dimension2D getDimension(StringBounder stringBounder) {
		double width = getComponent().getPreferredWidth(stringBounder);
		double height = getComponent().getPreferredHeight(stringBounder);

		if (minimumDimension != null && width < minimumDimension.getWidth()) {
			width = minimumDimension.getWidth();
		}
		if (minimumDimension != null && height < minimumDimension.getHeight()) {
			height = minimumDimension.getHeight();
		}

		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug) {

		final Component note = getComponent();
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionToUse = getDimension(stringBounder);

		note.drawU(ug, new Area(dimensionToUse), new SimpleContext2D(false));

	}

	private Component getComponent() {
		final HColor noteBackgroundColor = HColorSet.instance().getColorOrWhite("#FBFB77");
		final HColor borderColor = HColorUtils.MY_RED;

		final SkinParam param = SkinParam.noShadowing(null);
		final UFont fontNote = param.getFont(null, false, FontParam.NOTE);
		final FontConfiguration font2 = fontNote.toFont2(HColorUtils.BLACK, true, HColorUtils.BLUE, 8);
		final ComponentRoseNote note = new ComponentRoseNote(null,
				new SymbolContext(noteBackgroundColor, borderColor).withStroke(new UStroke()), font2, text, 0, 0,
				new SpriteContainerEmpty(), 0, HorizontalAlignment.LEFT);
		return note;
	}
}
