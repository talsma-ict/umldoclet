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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.AffineTransformType;
import net.sourceforge.plantuml.ugraphic.PixelImage;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class GraphicStrings extends AbstractTextBlock implements IEntityImage {

	private final double margin = 5;

	private final HColor background;

	private final static HColor hyperlinkColor = HColorUtils.BLUE;

	private final static boolean useUnderlineForHyperlink = true;

	private final List<String> strings;

	private final BufferedImage image;
	private final double imagePadding = 30;

	private final GraphicPosition position;

	private final FontConfiguration fontConfiguration;

	public static IEntityImage createForError(List<String> strings, boolean useRed) {
		return new GraphicStrings(strings, sansSerif14(getForeColor(useRed)).bold(), getBackColor(useRed), null, null,
				CreoleMode.NO_CREOLE);
	}

	private static HColor getForeColor(boolean useRed) {
		if (useRed) {
			return HColorUtils.BLACK;
		}
		return HColorUtils.MY_GREEN;
	}

	private static HColor getBackColor(boolean useRed) {
		if (useRed) {
			return HColorUtils.RED_LIGHT;
		}
		return HColorUtils.BLACK;
	}

	public static TextBlockBackcolored createGreenOnBlackMonospaced(List<String> strings) {
		return new GraphicStrings(strings, monospaced14(HColorUtils.GREEN), HColorUtils.BLACK, null, null,
				CreoleMode.SIMPLE_LINE);
	}

	public static TextBlockBackcolored createBlackOnWhite(List<String> strings) {
		return new GraphicStrings(strings, sansSerif12(HColorUtils.BLACK), HColorUtils.WHITE, null, null,
				CreoleMode.FULL);
	}

	public static TextBlockBackcolored createBlackOnWhiteMonospaced(List<String> strings) {
		return new GraphicStrings(strings, monospaced14(HColorUtils.BLACK), HColorUtils.WHITE, null, null,
				CreoleMode.FULL);
	}

	public static TextBlockBackcolored createBlackOnWhite(List<String> strings, BufferedImage image,
			GraphicPosition position) {
		return new GraphicStrings(strings, sansSerif12(HColorUtils.BLACK), HColorUtils.WHITE, image, position,
				CreoleMode.FULL);
	}

	public static FontConfiguration sansSerif12(HColor color) {
		return FontConfiguration.create(UFont.sansSerif(12), color, hyperlinkColor, useUnderlineForHyperlink);
	}

	public static FontConfiguration sansSerif14(HColor color) {
		return FontConfiguration.create(UFont.sansSerif(14), color, hyperlinkColor, useUnderlineForHyperlink);
	}

	private static FontConfiguration monospaced14(HColor color) {
		return FontConfiguration.create(UFont.monospaced(14), color, hyperlinkColor, useUnderlineForHyperlink);
	}

	private final CreoleMode mode;

	private GraphicStrings(List<String> strings, FontConfiguration fontConfiguration, HColor background,
			BufferedImage image, GraphicPosition position, CreoleMode mode) {
		this.strings = strings;
		this.background = background;
		this.image = image;
		this.position = position;
		this.mode = mode;
		this.fontConfiguration = fontConfiguration;

	}

	private TextBlock getTextBlock() {
		final Display display = Display.create(strings);
		if (mode == CreoleMode.NO_CREOLE) {
			return new TextBlockRaw(strings, fontConfiguration);

		} else {
			return display.create7(fontConfiguration, HorizontalAlignment.LEFT, new SpriteContainerEmpty(), mode);
		}
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(new UTranslate(margin, margin));
		final Dimension2D size = calculateDimensionInternal(ug.getStringBounder());
		getTextBlock().drawU(ug.apply(fontConfiguration.getColor()));

		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				ug.apply(new UTranslate((size.getWidth() - image.getWidth()) / 2, size.getHeight() - image.getHeight()))
						.draw(new UImage(new PixelImage(image, AffineTransformType.TYPE_BILINEAR)));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth(), size.getHeight() - image.getHeight()))
						.draw(new UImage(new PixelImage(image, AffineTransformType.TYPE_BILINEAR)));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth() - 1, 1))
						.draw(new UImage(new PixelImage(image, AffineTransformType.TYPE_BILINEAR)));
			}
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return Dimension2DDouble.delta(calculateDimensionInternal(stringBounder), 2 * margin);
	}

	private Dimension2D calculateDimensionInternal(StringBounder stringBounder) {
		Dimension2D dim = getTextBlock().calculateDimension(stringBounder);
		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				dim = new Dimension2DDouble(dim.getWidth(), dim.getHeight() + image.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + imagePadding + image.getWidth(), dim.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + imagePadding + image.getWidth(), dim.getHeight());
			}
		}
		return dim;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public HColor getBackcolor() {
		return background;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return false;
	}

	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
