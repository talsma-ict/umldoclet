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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;

public class EntityImageCircleStart extends AbstractEntityImage {

	private static final int SIZE = 20;

	public StyleSignatureBasic getDefaultStyleDefinitionCircle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.circle, SName.start);
	}

	public EntityImageCircleStart(Entity entity, ISkinParam skinParam) {
		super(entity, skinParam);
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(SIZE, SIZE);
	}

	final public void drawU(UGraphic ug) {
		final UEllipse circle = UEllipse.build(SIZE, SIZE);

		final Style style = getDefaultStyleDefinitionCircle().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
		HColor color = getEntity().getColors().getColor(ColorType.BACK);
		if (color == null)
			color = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());

		final double shadowing = style.value(PName.Shadowing).asDouble();

		circle.setDeltaShadow(shadowing);
		ug.apply(color.bg()).apply(HColors.none()).draw(circle);
	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

}
