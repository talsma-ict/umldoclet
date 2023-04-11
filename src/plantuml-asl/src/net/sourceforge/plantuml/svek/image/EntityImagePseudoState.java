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
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;

public class EntityImagePseudoState extends AbstractEntityImage {

	private static final int SIZE = 22;
	private final TextBlock desc;
	private final SName sname;

	public EntityImagePseudoState(Entity entity, ISkinParam skinParam, SName sname) {
		this(entity, skinParam, "H", sname);
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname, SName.diamond);
	}

	public EntityImagePseudoState(Entity entity, ISkinParam skinParam, String historyText, SName sname) {
		super(entity, skinParam);
		this.sname = sname;
		final Stereotype stereotype = entity.getStereotype();

		final FontConfiguration fontConfiguration = FontConfiguration.create(getSkinParam(), FontParam.STATE,
				stereotype);

		this.desc = Display.create(historyText).create(fontConfiguration, HorizontalAlignment.CENTER, skinParam);
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(SIZE, SIZE);
	}

	final public void drawU(UGraphic ug) {
		final UEllipse circle = UEllipse.build(SIZE, SIZE);

		final Style style = getStyle();
		final HColor borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
		final HColor backgroundColor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());
		final double shadow = style.value(PName.Shadowing).asDouble();
		final UStroke stroke = style.getStroke();

		circle.setDeltaShadow(shadow);
		ug = ug.apply(stroke);
		ug = ug.apply(backgroundColor.bg()).apply(borderColor);
		ug.draw(circle);
		// ug = ug.apply(UStroke.simple());

		final XDimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double widthDesc = dimDesc.getWidth();
		final double heightDesc = dimDesc.getHeight();

		final double x = (SIZE - widthDesc) / 2;
		final double y = (SIZE - heightDesc) / 2;
		desc.drawU(ug.apply(new UTranslate(x, y)));
	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

}
