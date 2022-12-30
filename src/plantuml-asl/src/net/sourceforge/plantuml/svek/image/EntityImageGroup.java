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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.baraye.ILeaf;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class EntityImageGroup extends AbstractEntityImage {

	// final private TextBlock desc;
	// final private static int MARGIN = 10;

	public EntityImageGroup(ILeaf entity, ISkinParam skinParam) {
		super(entity, skinParam);
		// this.desc = Display.create(StringUtils.getWithNewlines(entity.getDisplay()),
		// FontConfiguration.create(
		// getFont(FontParam.ACTIVITY), HtmlColorUtils.BLACK),
		// HorizontalAlignment.CENTER);
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(30, 30);
	}

	final public void drawU(UGraphic ug) {
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
