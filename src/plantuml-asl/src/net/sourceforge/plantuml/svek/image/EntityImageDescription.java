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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.BodyFactory;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.USymbols;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.utils.MathUtils;

public class EntityImageDescription extends AbstractEntityImage {

	private final ShapeType shapeType;

	final private Url url;

	private final TextBlock asSmall;

	private final TextBlock name;
	private final TextBlock desc;

	private TextBlock stereo;

	private final boolean hideText;
	private final Collection<Link> links;
	private final boolean useRankSame;
	private final boolean fixCircleLabelOverlapping;
	private final Bibliotekon bibliotekon;
	private final SymbolContext ctx;

	public EntityImageDescription(ILeaf entity, ISkinParam skinParam2, PortionShower portionShower,
			Collection<Link> links, SName styleName, Bibliotekon bibliotekon) {
		super(entity, entity.getColors().mute(skinParam2));
		this.useRankSame = getSkinParam().useRankSame();
		this.bibliotekon = bibliotekon;
		this.fixCircleLabelOverlapping = getSkinParam().fixCircleLabelOverlapping();

		this.links = links;
		USymbol symbol = getUSymbol(entity);
		if (symbol == USymbols.FOLDER)
			this.shapeType = ShapeType.FOLDER;
		else if (symbol == USymbols.HEXAGON)
			this.shapeType = ShapeType.HEXAGON;
		else if (symbol == USymbols.INTERFACE)
			this.shapeType = getSkinParam().fixCircleLabelOverlapping() ? ShapeType.RECTANGLE_WITH_CIRCLE_INSIDE
					: ShapeType.RECTANGLE;
		else
			this.shapeType = ShapeType.RECTANGLE;

		this.hideText = symbol == USymbols.INTERFACE;

		this.url = entity.getUrl99();

		final Colors colors = entity.getColors();
		HColor backcolor = colors.getColor(ColorType.BACK);

		final StyleSignatureBasic tmp = StyleSignatureBasic.of(SName.root, SName.element, styleName,
				symbol.getSkinParameter().getStyleName());
		final Stereotype stereotype = entity.getStereotype();
		final Style style = tmp.withTOBECHANGED(stereotype).getMergedStyle(getSkinParam().getCurrentStyleBuilder())
				.eventuallyOverride(colors);

		final Style styleStereo = tmp.forStereotypeItself(stereotype)
				.getMergedStyle(getSkinParam().getCurrentStyleBuilder());
		final HColor forecolor = style.value(PName.LineColor).asColor(getSkinParam().getThemeStyle(),
				getSkinParam().getIHtmlColorSet());
		if (backcolor == null)
			backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getThemeStyle(),
					getSkinParam().getIHtmlColorSet());

		final double roundCorner = style.value(PName.RoundCorner).asDouble();
		final double diagonalCorner = style.value(PName.DiagonalCorner).asDouble();
		final double deltaShadow = style.value(PName.Shadowing).asDouble();
		final UStroke stroke = style.getStroke(colors);
		final FontConfiguration fcTitle = style.getFontConfiguration(getSkinParam().getThemeStyle(),
				getSkinParam().getIHtmlColorSet());
		final FontConfiguration fcStereo = styleStereo.getFontConfiguration(getSkinParam().getThemeStyle(),
				getSkinParam().getIHtmlColorSet());
		final HorizontalAlignment defaultAlign = style.getHorizontalAlignment();

		assert getStereo() == stereotype;

		ctx = new SymbolContext(backcolor, forecolor).withStroke(stroke).withShadow(deltaShadow).withCorner(roundCorner,
				diagonalCorner);

		final Display codeDisplay = Display.getWithNewlines(entity.getCodeGetName());
		if ((entity.getDisplay().equals(codeDisplay) && symbol.getSkinParameter() == SkinParameter.PACKAGE)
				|| entity.getDisplay().isWhite()) {
			desc = TextBlockUtils.empty(getSkinParam().minClassWidth(), 0);
		} else {
			final HorizontalAlignment align = getSkinParam().getDefaultTextAlignment(defaultAlign);
			desc = BodyFactory.create3(entity.getDisplay(), symbol.getFontParam(), getSkinParam(), align, fcTitle,
					getSkinParam().wrapWidth(), style);
		}

		stereo = TextBlockUtils.empty(0, 0);

		if (stereotype != null && stereotype.getSprite(getSkinParam()) != null)
			stereo = stereotype.getSprite(getSkinParam());
		else if (stereotype != null && stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) != null
				&& portionShower.showPortion(EntityPortion.STEREOTYPE, entity))
			stereo = Display.getWithNewlines(stereotype.getLabel(getSkinParam().guillemet())).create(fcStereo,
					HorizontalAlignment.CENTER, getSkinParam());

		name = BodyFactory.create2(getSkinParam().getDefaultTextAlignment(HorizontalAlignment.CENTER), codeDisplay,
				symbol.getFontParam(), getSkinParam(), stereotype, entity, style);

		if (hideText)
			asSmall = symbol.asSmall(TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0),
					ctx, getSkinParam().getStereotypeAlignment());
		else
			asSmall = symbol.asSmall(name, desc, stereo, ctx, getSkinParam().getStereotypeAlignment());

	}

	private USymbol getUSymbol(ILeaf entity) {
		final USymbol result = entity.getUSymbol() == null ? getSkinParam().componentStyle().toUSymbol()
				: entity.getUSymbol();
		return Objects.requireNonNull(result);
	}

	public Dimension2D getNameDimension(StringBounder stringBounder) {
		if (hideText)
			return new Dimension2DDouble(0, 0);

		return name.calculateDimension(stringBounder);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return asSmall.calculateDimension(stringBounder);
	}

	@Override
	public Margins getShield(StringBounder stringBounder) {
		if (hideText == false)
			return Margins.NONE;

		if (isThereADoubleLink((ILeaf) getEntity(), links))
			return Margins.NONE;

		if (fixCircleLabelOverlapping == false && hasSomeHorizontalLinkVisible((ILeaf) getEntity(), links))
			return Margins.NONE;

		if (hasSomeHorizontalLinkDoubleDecorated((ILeaf) getEntity(), links))
			return Margins.NONE;

		final Dimension2D dimStereo = stereo.calculateDimension(stringBounder);
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
		final Dimension2D dimSmall = asSmall.calculateDimension(stringBounder);
		final double x = Math.max(dimStereo.getWidth(), dimDesc.getWidth());
		double suppX = x - dimSmall.getWidth();
		if (suppX < 1)
			suppX = 1;

		final double y = MathUtils.max(1, dimDesc.getHeight(), dimStereo.getHeight());
		return new Margins(suppX / 2, suppX / 2, y, y);
	}

	private boolean hasSomeHorizontalLinkVisible(ILeaf leaf, Collection<Link> links) {
		for (Link link : links)
			if (link.getLength() == 1 && link.contains(leaf) && link.isInvis() == false)
				return true;

		return false;
	}

	private boolean isThereADoubleLink(ILeaf leaf, Collection<Link> links) {
		final Set<IEntity> others = new HashSet<>();
		for (Link link : links) {
			if (link.contains(leaf)) {
				final IEntity other = link.getOther(leaf);
				final boolean changed = others.add(other);
				if (changed == false)
					return true;

			}
		}
		return false;
	}

	private boolean hasSomeHorizontalLinkDoubleDecorated(ILeaf leaf, Collection<Link> links) {
		for (Link link : links)
			if (link.getLength() == 1 && link.contains(leaf) && link.getType().isDoubleDecorated())
				return true;

		return false;
	}

	final public void drawU(UGraphic ug) {
		ug.draw(new UComment("entity " + getEntity().getCodeGetName()));
		final Map<UGroupType, String> typeIDent = new EnumMap<>(UGroupType.class);
		typeIDent.put(UGroupType.CLASS, "elem " + getEntity().getCode() + " selected");
		typeIDent.put(UGroupType.ID, "elem_" + getEntity().getCode());
		ug.startGroup(typeIDent);

		if (url != null)
			ug.startUrl(url);

		if (shapeType == ShapeType.HEXAGON)
			drawHexagon(ctx.apply(ug));

		asSmall.drawU(ug);

		if (hideText) {
			final double space = 8;
			final Dimension2D dimSmall = asSmall.calculateDimension(ug.getStringBounder());
			final Dimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
			final double posx1 = (dimSmall.getWidth() - dimDesc.getWidth()) / 2;

			UGraphic ugDesc = ug.apply(new UTranslate(posx1, space + dimSmall.getHeight()));
			ugDesc = UGraphicStencil.create(ugDesc, dimDesc);
			desc.drawU(ugDesc);

			final Dimension2D dimStereo = stereo.calculateDimension(ug.getStringBounder());
			final double posx2 = (dimSmall.getWidth() - dimStereo.getWidth()) / 2;
			stereo.drawU(ug.apply(new UTranslate(posx2, -space - dimStereo.getHeight())));
		}

		if (url != null)
			ug.closeUrl();

		ug.closeGroup();
	}

	private void drawHexagon(UGraphic ug) {
		if (bibliotekon == null)
			throw new IllegalStateException();

		final SvekNode node = bibliotekon.getNode(getEntity());
		final Shadowable hexagon = node.getPolygon();
		if (hexagon != null) {
			hexagon.setDeltaShadow(ctx.getDeltaShadow());
			ug.draw(hexagon);
		}
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	@Override
	public double getOverscanX(StringBounder stringBounder) {
		if (hideText) {
			final Dimension2D dimSmall = asSmall.calculateDimension(stringBounder);
			final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
			final Dimension2D dimStereo = stereo.calculateDimension(stringBounder);
			final double posx1 = (dimSmall.getWidth() - dimDesc.getWidth()) / 2;
			final double posx2 = (dimSmall.getWidth() - dimStereo.getWidth()) / 2;
			return MathUtils.max(-posx1, -posx2, 0);
		}
		return 0;
	}
}
