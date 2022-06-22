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

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class EntityImageClass extends AbstractEntityImage implements Stencil, WithPorts {

	final private TextBlock body;
	final private Margins shield;
	final private EntityImageClassHeader header;
	final private Url url;
	final private double roundCorner;
	final private LeafType leafType;

	final private LineConfigurable lineConfig;

	public EntityImageClass(GraphvizVersion version, ILeaf entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, entity.getColors().mute(skinParam));
		this.leafType = entity.getLeafType();
		this.lineConfig = entity;

		this.roundCorner = getStyle().value(PName.RoundCorner).asDouble();

		this.shield = version != null && version.useShield() && entity.hasNearDecoration() ? Margins.uniform(16)
				: Margins.NONE;
		final boolean showMethods = portionShower.showPortion(EntityPortion.METHOD, entity);
		final boolean showFields = portionShower.showPortion(EntityPortion.FIELD, entity);
		this.body = entity.getBodier().getBody(FontParam.CLASS_ATTRIBUTE, getSkinParam(), showMethods, showFields,
				entity.getStereotype(), getStyle(), null);

		this.header = new EntityImageClassHeader(entity, getSkinParam(), portionShower);
		this.url = entity.getUrl99();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimHeader = header.calculateDimension(stringBounder);
		final Dimension2D dimBody = body == null ? new Dimension2DDouble(0, 0) : body.calculateDimension(stringBounder);
		double width = Math.max(dimBody.getWidth(), dimHeader.getWidth());
		if (width < getSkinParam().minClassWidth())
			width = getSkinParam().minClassWidth();

		final double height = dimBody.getHeight() + dimHeader.getHeight();
		return new Dimension2DDouble(width, height);
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final Rectangle2D result = body.getInnerPosition(member, stringBounder, strategy);
		if (result == null)
			return result;

		final Dimension2D dimHeader = header.calculateDimension(stringBounder);
		final UTranslate translate = UTranslate.dy(dimHeader.getHeight());
		return translate.apply(result);
	}

	final public void drawU(UGraphic ug) {
		ug.draw(new UComment("class " + getEntity().getCodeGetName()));
		if (url != null)
			ug.startUrl(url);

		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getCode() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getCode());
		ug.startGroup(typeIDent);
		drawInternal(ug);
		ug.closeGroup();

		if (url != null)
			ug.closeUrl();

	}

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.classDiagram, SName.class_) //
				.withTOBECHANGED(getEntity().getStereotype()) //
				.with(getEntity().getStereostyles()) //
				.getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private Style getStyleHeader() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.classDiagram, SName.class_, SName.header) //
				.withTOBECHANGED(getEntity().getStereotype()) //
				.with(getEntity().getStereostyles()) //
				.getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	private void drawInternal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimHeader = header.calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal).rounded(roundCorner)
				.withCommentAndCodeLine(getEntity().getCodeGetName(), getEntity().getCodeLine());

		double shadow = 0;

		HColor classBorder = lineConfig.getColors().getColor(ColorType.LINE);
		HColor headerBackcolor = getEntity().getColors().getColor(ColorType.HEADER);
		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);

		shadow = getStyle().value(PName.Shadowing).asDouble();

		if (classBorder == null)
			classBorder = getStyle().value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());

		if (headerBackcolor == null)
			headerBackcolor = backcolor == null ? getStyleHeader().value(PName.BackGroundColor)
					.asColor(getSkinParam().getThemeStyle(), getSkinParam().getIHtmlColorSet()) : backcolor;

		if (backcolor == null)
			backcolor = getStyle().value(PName.BackGroundColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());

		rect.setDeltaShadow(shadow);

		ug = ug.apply(classBorder);
		ug = ug.apply(backcolor.bg());

		final UStroke stroke = getStyle().getStroke(lineConfig.getColors());

		UGraphic ugHeader = ug;
		if (roundCorner == 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			ug.apply(stroke).draw(rect);
			final Shadowable rect2 = new URectangle(widthTotal, dimHeader.getHeight());
			rect2.setDeltaShadow(0);
			ugHeader = ugHeader.apply(headerBackcolor.bg());
			ugHeader.apply(stroke).draw(rect2);
		} else if (roundCorner != 0 && headerBackcolor != null && backcolor.equals(headerBackcolor) == false) {
			ug.apply(stroke).draw(rect);
			final Shadowable rect2 = new URectangle(widthTotal, dimHeader.getHeight()).rounded(roundCorner);
			final URectangle rect3 = new URectangle(widthTotal, roundCorner / 2);
			rect2.setDeltaShadow(0);
			rect3.setDeltaShadow(0);
			ugHeader = ugHeader.apply(headerBackcolor.bg()).apply(headerBackcolor);
			ugHeader.apply(stroke).draw(rect2);
			ugHeader.apply(stroke).apply(UTranslate.dy(dimHeader.getHeight() - rect3.getHeight())).draw(rect3);
			rect.setDeltaShadow(0);
			ug.apply(stroke).apply(new HColorNone().bg()).draw(rect);
		} else {
			ug.apply(stroke).draw(rect);
		}
		header.drawU(ugHeader, dimTotal.getWidth(), dimHeader.getHeight());

		if (body != null) {
			final UGraphic ug2 = UGraphicStencil.create(ug, this, stroke);
			final UTranslate translate = UTranslate.dy(dimHeader.getHeight());
			body.drawU(ug2.apply(translate));
		}
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final Dimension2D dimHeader = header.calculateDimension(stringBounder);
		if (body instanceof WithPorts)
			return ((WithPorts) body).getPorts(stringBounder).translateY(dimHeader.getHeight());
		return new Ports();
	}

	public ShapeType getShapeType() {
		if (((ILeaf) getEntity()).getPortShortNames().size() > 0)
			return ShapeType.RECTANGLE_HTML_FOR_PORTS;

		return ShapeType.RECTANGLE;
	}

	@Override
	public Margins getShield(StringBounder stringBounder) {
		return shield;
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

}
