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

import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.baraye.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.SvekLine;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class EntityImageLollipopInterfaceEye1 extends AbstractEntityImage {

	private static final int SIZE = 24;
	private final TextBlock desc;
	private final Bibliotekon bibliotekon;
	final private Url url;

	public EntityImageLollipopInterfaceEye1(ILeaf entity, ISkinParam skinParam, Bibliotekon bibliotekon) {
		super(entity, skinParam);
		this.bibliotekon = bibliotekon;
		final Stereotype stereotype = entity.getStereotype();
		this.desc = entity.getDisplay().create(FontConfiguration.create(getSkinParam(), FontParam.CLASS, stereotype),
				HorizontalAlignment.CENTER, skinParam);
		this.url = entity.getUrl99();

	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		return new XDimension2D(SIZE, SIZE);
	}

	final public void drawU(UGraphic ug) {
		ug = ug.apply(SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.classBorder));
		ug = ug.apply(SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.classBackground).bg());
		if (url != null) {
			ug.startUrl(url);
		}
		final double sizeSmall = 14;
		final double diff = (SIZE - sizeSmall) / 2;
		final UEllipse circle1 = new UEllipse(sizeSmall, sizeSmall);
		if (getSkinParam().shadowing(getEntity().getStereotype())) {
			// circle.setDeltaShadow(4);
		}
		ug.apply(new UStroke(1.5)).apply(new UTranslate(diff, diff)).draw(circle1);
		ug = ug.apply(HColors.none().bg());

		XPoint2D pos = bibliotekon.getNode(getEntity()).getPosition();

		final List<SvekLine> lines = bibliotekon.getAllLineConnectedTo(getEntity());
		final UTranslate reverse = new UTranslate(pos).reverse();
		final ConnectedCircle connectedCircle = new ConnectedCircle(SIZE / 2);
		for (SvekLine line : lines) {
			XPoint2D pt = line.getMyPoint(getEntity());
			pt = reverse.getTranslated(pt);
			connectedCircle.addSecondaryConnection(pt);

		}
		// connectedCircle.drawU(ug.apply(new UStroke(1.5)));
		connectedCircle.drawU(ug);

		//
		// final Dimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		// final double widthDesc = dimDesc.getWidth();
		// // final double totalWidth = Math.max(widthDesc, SIZE);
		//
		// final double x = SIZE / 2 - widthDesc / 2;
		// final double y = SIZE;
		// desc.drawU(ug.apply(new UTranslate(x, y)));
		if (url != null) {
			ug.closeUrl();
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

}
