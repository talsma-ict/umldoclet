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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CommunicationTileNoteTop extends CommunicationTileNoteBottomTopAbstract {

	public CommunicationTileNoteTop(Tile tile, AbstractMessage message, Rose skin, ISkinParam skinParam,
			Note noteOnMessage) {
		super(tile, message, skin, skinParam, noteOnMessage);
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Component comp = getComponent(stringBounder);
		final XDimension2D dim = comp.getPreferredDimension(stringBounder);
		final Area area = Area.create(dim.getWidth(), dim.getHeight());

		((UDrawable) tile).drawU(ug.apply(UTranslate.dy(dim.getHeight() + spacey)));

		final double middleMsg = (tile.getMinX().getCurrentValue() + tile.getMaxX().getCurrentValue()) / 2;

		final double xNote = getNotePosition(stringBounder).getCurrentValue();

		comp.drawU(ug.apply(UTranslate.dx(xNote)), area, (Context2D) ug);

		drawLine(ug, middleMsg, tile.getContactPointRelative() + dim.getHeight() + spacey, xNote + dim.getWidth() / 2,
				dim.getHeight() - 2 * Rose.paddingY);

	}

}
