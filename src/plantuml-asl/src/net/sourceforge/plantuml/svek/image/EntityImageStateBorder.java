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
 * Contribution :  Hisashi Miyashita
 */
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageStateBorder extends AbstractEntityImageBorder {

	private final SName sname;

	public EntityImageStateBorder(ILeaf leaf, ISkinParam skinParam, Cluster stateParent, final Bibliotekon bibliotekon,
			SName sname) {
		super(leaf, skinParam, stateParent, bibliotekon, FontParam.STATE);
		this.sname = sname;
	}

	private StyleSignatureBasic getSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname);
	}

	private boolean upPosition() {
		if (parent == null)
			return false;

		final Point2D clusterCenter = parent.getClusterPosition().getPointCenter();
		final SvekNode node = bibliotekon.getNode(getEntity());
		return node.getMinY() < clusterCenter.getY();
	}

	final public void drawU(UGraphic ug) {
		double y = 0;
		final Dimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double x = 0 - (dimDesc.getWidth() - 2 * EntityPosition.RADIUS) / 2;
		if (upPosition())
			y -= 2 * EntityPosition.RADIUS + dimDesc.getHeight();
		else
			y += 2 * EntityPosition.RADIUS;

		desc.drawU(ug.apply(new UTranslate(x, y)));

		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		final HColor borderColor;

		if (UseStyle.useBetaStyle()) {
			final Style style = getSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			borderColor = style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());
			if (backcolor == null)
				backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getThemeStyle(),
						getSkinParam().getIHtmlColorSet());
		} else {
			borderColor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBorder);
			if (backcolor == null)
				backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.stateBackground);
		}

		ug = ug.apply(getUStroke()).apply(borderColor);
		ug = ug.apply(backcolor.bg());

		entityPosition.drawSymbol(ug, rankdir);
	}

	private UStroke getUStroke() {
		return new UStroke(1.5);
	}

}
