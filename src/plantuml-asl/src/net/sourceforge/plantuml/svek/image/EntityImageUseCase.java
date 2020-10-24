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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.BodyEnhanced;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphicHorizontalLine;
import net.sourceforge.plantuml.ugraphic.TextBlockInEllipse;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UHorizontalLine;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageUseCase extends AbstractEntityImage {

	final private TextBlock desc;

	final private Url url;

	public EntityImageUseCase(ILeaf entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, skinParam);
		final Stereotype stereotype = entity.getStereotype();

		final TextBlock tmp = new BodyEnhanced(entity.getDisplay(), FontParam.USECASE, skinParam,
				HorizontalAlignment.CENTER, stereotype, true, false, entity, SName.componentDiagram);

		if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
				|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false) {
			this.desc = tmp;
		} else {
			final TextBlock stereo;
			if (stereotype.getSprite(getSkinParam()) != null) {
				stereo = stereotype.getSprite(getSkinParam());
			} else {
				stereo = Display.getWithNewlines(stereotype.getLabel(getSkinParam().guillemet())).create(
						new FontConfiguration(getSkinParam(), FontParam.USECASE_STEREOTYPE, stereotype),
						HorizontalAlignment.CENTER, skinParam);
			}
			this.desc = TextBlockUtils.mergeTB(stereo, tmp, HorizontalAlignment.CENTER);
		}
		this.url = entity.getUrl99();

	}

	private UStroke getStroke() {
		if (SkinParam.USE_STYLES()) {
			final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			return style.getStroke();
		}
		UStroke stroke = getSkinParam().getThickness(LineParam.usecaseBorder, getStereo());

		if (stroke == null) {
			stroke = new UStroke(1.5);
		}
		return stroke;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new TextBlockInEllipse(desc, stringBounder).calculateDimension(stringBounder);
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final TextBlockInEllipse ellipse = new TextBlockInEllipse(desc, stringBounder);
		if (getSkinParam().shadowing2(getEntity().getStereotype(), SkinParameter.USECASE)) {
			ellipse.setDeltaShadow(3);
		}

		if (url != null) {
			ug.startUrl(url);
		}

		ug = ug.apply(getStroke());
		final HColor linecolor = getLineColor();
		ug = ug.apply(linecolor);
		final HColor backcolor = getBackColor();
		ug = ug.apply(backcolor.bg());
		final UGraphic ug2 = new MyUGraphicEllipse(ug, 0, 0, ellipse.getUEllipse());

		ellipse.drawU(ug2);
		if (getEntity().getLeafType() == LeafType.USECASE_BUSINESS) {
			specialBusiness(ug, ellipse.getUEllipse());
		}

		if (url != null) {
			ug.closeUrl();
		}
	}

	private void specialBusiness(UGraphic ug, UEllipse frontier) {
		final RotatedEllipse rotatedEllipse = new RotatedEllipse(frontier, Math.PI / 4);

		final double theta1 = 20.0 * Math.PI / 180;
		final double theta2 = rotatedEllipse.getOtherTheta(theta1);

		final UEllipse frontier2 = frontier.scale(0.99);
		final Point2D p1 = frontier2.getPointAtAngle(-theta1);
		final Point2D p2 = frontier2.getPointAtAngle(-theta2);
		drawLine(ug, p1, p2);
	}

	private void specialBusiness0(UGraphic ug, UEllipse frontier) {
		final double c = frontier.getWidth() / frontier.getHeight();
		final double ouverture = Math.PI / 2;
		final Point2D p1 = frontier.getPointAtAngle(getTrueAngle(c, Math.PI / 4 - ouverture));
		final Point2D p2 = frontier.getPointAtAngle(getTrueAngle(c, Math.PI / 4 + ouverture));
		drawLine(ug, p1, p2);
	}

	private void drawLine(UGraphic ug, final Point2D p1, final Point2D p2) {
		ug = ug.apply(new UTranslate(p1));
		ug.draw(new ULine(p2.getX() - p1.getX(), p2.getY() - p1.getY()));
	}

	private double getTrueAngle(final double c, final double gamma) {
		return Math.atan2(Math.sin(gamma), Math.cos(gamma) / c);
	}

	private HColor getBackColor() {
		HColor backcolor = getEntity().getColors(getSkinParam()).getColor(ColorType.BACK);
		if (backcolor == null) {
			if (SkinParam.USE_STYLES()) {
				final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
				backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());
			} else {
				backcolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.usecaseBackground);
			}
		}
		return backcolor;
	}

	private StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.componentDiagram, SName.usecase);
	}

	private HColor getLineColor() {
		HColor linecolor = getEntity().getColors(getSkinParam()).getColor(ColorType.LINE);
		if (linecolor == null) {
			if (SkinParam.USE_STYLES()) {
				final Style style = getDefaultStyleDefinition().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
				linecolor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
			} else {
				linecolor = SkinParamUtils.getColor(getSkinParam(), getStereo(), ColorParam.usecaseBorder);
			}
		}
		return linecolor;
	}

	public ShapeType getShapeType() {
		return ShapeType.OVAL;
	}

	static class MyUGraphicEllipse extends AbstractUGraphicHorizontalLine {

		private final double startingX;
		private final double yTheoricalPosition;
		private final UEllipse ellipse;

		@Override
		protected AbstractUGraphicHorizontalLine copy(UGraphic ug) {
			return new MyUGraphicEllipse(ug, startingX, yTheoricalPosition, ellipse);
		}

		MyUGraphicEllipse(UGraphic ug, double startingX, double yTheoricalPosition, UEllipse ellipse) {
			super(ug);
			this.startingX = startingX;
			this.ellipse = ellipse;
			this.yTheoricalPosition = yTheoricalPosition;
		}

		private double getNormalized(double y) {
			if (y < yTheoricalPosition) {
				throw new IllegalArgumentException();
			}
			y = y - yTheoricalPosition;
			if (y > ellipse.getHeight()) {
				throw new IllegalArgumentException();
			}
			return y;
		}

		private double getStartingXInternal(double y) {
			return startingX + ellipse.getStartingX(getNormalized(y));
		}

		private double getEndingXInternal(double y) {
			return startingX + ellipse.getEndingX(getNormalized(y));
		}

		private Stencil getStencil2(UTranslate translate) {
			final double dy = translate.getDy();
			return new Stencil() {

				public double getStartingX(StringBounder stringBounder, double y) {
					return getStartingXInternal(y + dy);
				}

				public double getEndingX(StringBounder stringBounder, double y) {
					return getEndingXInternal(y + dy);
				}
			};
		}

		@Override
		protected void drawHline(UGraphic ug, UHorizontalLine line, UTranslate translate) {
			final UStroke stroke = new UStroke(1.5);
			line.drawLineInternal(ug.apply(translate), getStencil2(translate), 0, stroke);
		}

	}

}
