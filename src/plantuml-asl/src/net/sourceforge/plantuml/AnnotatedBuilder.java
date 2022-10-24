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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositioned;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.BigFrame;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.DecorateEntityImage;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class AnnotatedBuilder {

	private final Annotated annotated;
	private final ISkinParam skinParam;
	private final StringBounder stringBounder;

	public AnnotatedBuilder(Annotated annotated, ISkinParam skinParam, StringBounder stringBounder) {
		this.annotated = annotated;
		this.skinParam = skinParam;
		this.stringBounder = stringBounder;
	}

	public boolean hasMainFrame() {
		return annotated.getMainFrame() != null;
	}

	public double mainFrameSuppHeight() {
		final Display mainFrame = annotated.getMainFrame();
		final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.mainframe)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final FontConfiguration fontConfiguration = FontConfiguration.create(getSkinParam(), style);
		final TextBlock title = mainFrame.create(fontConfiguration, HorizontalAlignment.CENTER, getSkinParam());
		final XDimension2D dimTitle = title.calculateDimension(stringBounder);

		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final ClockwiseTopRightBottomLeft padding = style.getPadding().incTop(dimTitle.getHeight() + 10);

		return margin.getBottom() + margin.getTop() + padding.getTop() + padding.getBottom() + 10;
	}

	public TextBlock decoreWithFrame(final TextBlock original) {
		final Display mainFrame = annotated.getMainFrame();
		if (mainFrame == null)
			return original;

//		final double x1 = 5;
//		final double x2 = 7;
//		final double y1 = 10;
//		final double y2 = 10;

		final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.mainframe)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final FontConfiguration fontConfiguration = FontConfiguration.create(getSkinParam(), style);
		final TextBlock title = mainFrame.create(fontConfiguration, HorizontalAlignment.CENTER, getSkinParam());
		final XDimension2D dimTitle = title.calculateDimension(stringBounder);

		final SymbolContext symbolContext = style.getSymbolContext(skinParam.getIHtmlColorSet());
		final ClockwiseTopRightBottomLeft margin = style.getMargin();
		final ClockwiseTopRightBottomLeft padding = style.getPadding().incTop(dimTitle.getHeight() + 10);

		final MinMax originalMinMax = TextBlockUtils.getMinMax(original, stringBounder, false);

		final double ww = originalMinMax.getMinX() >= 0 ? originalMinMax.getMaxX() : originalMinMax.getWidth();
		final double hh = originalMinMax.getMinY() >= 0 ? originalMinMax.getMaxY() : originalMinMax.getHeight();
		final double dx = originalMinMax.getMinX() < 0 ? -originalMinMax.getMinX() : 0;
		final double dy = originalMinMax.getMinY() < 0 ? -originalMinMax.getMinY() : 0;
		final UTranslate delta = new UTranslate(dx, dy);

		final double width = padding.getLeft() + Math.max(ww + 12, dimTitle.getWidth() + 10) + padding.getRight();
		final double height = padding.getTop() + dimTitle.getHeight() + hh + padding.getBottom();

		final TextBlock frame = new BigFrame(title, width, height, symbolContext);

		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				frame.drawU(ug.apply(margin.getTranslate()));
				original.drawU(ug.apply(margin.getTranslate().compose(padding.getTranslate().compose(delta))));
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public XRectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				final XRectangle2D rect = original.getInnerPosition(member, stringBounder, strategy);
				return new XRectangle2D(dx + rect.getX() + margin.getLeft() + padding.getLeft(),
						dy + rect.getY() + margin.getTop() + padding.getTop() + dimTitle.getHeight(), rect.getWidth(),
						rect.getHeight());
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final XDimension2D dim1 = original.calculateDimension(stringBounder);
				final XDimension2D dim2 = padding.apply(dim1);
				final XDimension2D dim3 = margin.apply(dim2);
				return dim3;
			}

			public HColor getBackcolor() {
				return symbolContext.getBackColor();
			}
		};
	}

	private ISkinParam getSkinParam() {
		return skinParam;
	}

	public TextBlock getLegend() {
		final DisplayPositioned legend = annotated.getLegend();
		return EntityImageLegend.create(legend.getDisplay(), getSkinParam());
	}

	public TextBlock getTitle() {
		final DisplayPositioned title = (DisplayPositioned) annotated.getTitle();
		final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.title)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final TextBlock block = style.createTextBlockBordered(title.getDisplay(), skinParam.getIHtmlColorSet(),
				skinParam, Style.ID_TITLE);
		return block;
	}

	public TextBlock getCaption() {
		final DisplayPositioned caption = annotated.getCaption();
		if (caption.isNull())
			return TextBlockUtils.empty(0, 0);

		final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.caption)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		return style.createTextBlockBordered(caption.getDisplay(), skinParam.getIHtmlColorSet(), skinParam,
				Style.ID_CAPTION);

	}

	public TextBlock addHeaderAndFooter(TextBlock original) {
		final DisplaySection footer = annotated.getFooter();
		final DisplaySection header = annotated.getHeader();
		if (footer.isNull() && header.isNull())
			return original;

		TextBlock textFooter = null;
		if (footer.isNull() == false) {
			final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.footer)
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			textFooter = footer.createRibbon(FontConfiguration.create(getSkinParam(), FontParam.FOOTER, null),
					getSkinParam(), style);
		}
		TextBlock textHeader = null;
		if (header.isNull() == false) {
			final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.header)
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			textHeader = header.createRibbon(FontConfiguration.create(getSkinParam(), FontParam.HEADER, null),
					getSkinParam(), style);
		}

		return DecorateEntityImage.addTopAndBottom(original, textHeader, header.getHorizontalAlignment(), textFooter,
				footer.getHorizontalAlignment());
	}

}
