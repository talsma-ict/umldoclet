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

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.url.Url;

public class EntityImageLollipopInterface extends AbstractEntityImage {
    // ::remove folder when __HAXE__

	private static final int SIZE = 10;

	private final TextBlock desc;
	private final SName sname;
	private final Url url;

	public StyleSignature getSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname, SName.circle).withTOBECHANGED(getStereo());
	}

	private UStroke getUStroke() {
		return UStroke.withThickness(1.5);
	}

	public EntityImageLollipopInterface(Entity entity, ISkinParam skinParam, SName sname) {
		super(entity, skinParam);
		this.sname = sname;

		final FontConfiguration fc = FontConfiguration.create(getSkinParam(),
				getSignature().getMergedStyle(skinParam.getCurrentStyleBuilder()));

		this.desc = entity.getDisplay().create(fc, HorizontalAlignment.CENTER, skinParam);
		this.url = entity.getUrl99();

	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(SIZE, SIZE);
	}

	final public void drawU(UGraphic ug) {

		final Style style = getSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
		final HColor backgroundColor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());
		final HColor borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
		final double shadow = style.value(PName.Shadowing).asDouble();

		final UEllipse circle;
		if (getEntity().getLeafType() == LeafType.LOLLIPOP_HALF) {
			circle = new UEllipse(SIZE, SIZE, angle - 90, 180);
		} else {
			circle = UEllipse.build(SIZE, SIZE);
			if (getSkinParam().shadowing(getEntity().getStereotype()))
				circle.setDeltaShadow(shadow);
		}

		ug = ug.apply(backgroundColor.bg()).apply(borderColor);
		if (url != null)
			ug.startUrl(url);

		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getName() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getName());
		ug.startGroup(typeIDent);
		ug.apply(getUStroke()).draw(circle);
		ug.closeGroup();

		final XDimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double widthDesc = dimDesc.getWidth();

		final double x = SIZE / 2 - widthDesc / 2;
		final double y = SIZE;
		desc.drawU(ug.apply(new UTranslate(x, y)));
		if (url != null)
			ug.closeUrl();

	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

	private double angle;

	public void addImpact(double angle) {
		this.angle = 180 - (angle * 180 / Math.PI);
	}

}
