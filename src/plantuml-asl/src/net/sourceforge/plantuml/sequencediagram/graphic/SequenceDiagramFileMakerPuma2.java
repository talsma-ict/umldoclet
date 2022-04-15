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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.png.PngTitler;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Newpage;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class SequenceDiagramFileMakerPuma2 implements FileMaker {

	private final SequenceDiagram diagram;
	private final DrawableSet drawableSet;
	private final Dimension2D fullDimension;
	private final List<Page> pages;
	private final FileFormatOption fileFormatOption;
	private final StringBounder stringBounder;

	public SequenceDiagramFileMakerPuma2(SequenceDiagram diagram, Rose skin, FileFormatOption fileFormatOption) {
		this.diagram = diagram;
		this.stringBounder = fileFormatOption.getDefaultStringBounder(diagram.getSkinParam());
		this.fileFormatOption = fileFormatOption;
		final DrawableSetInitializer initializer = new DrawableSetInitializer(skin, diagram.getSkinParam(),
				diagram.isShowFootbox(), diagram.getAutonewpage());

		for (Participant p : diagram.participants()) {
			initializer.addParticipant(p, diagram.getEnglober(p));
		}

		for (Event ev : diagram.events()) {
			initializer.addEvent(ev);
			// if (ev instanceof Message) {
			// // TODO mieux faire
			// final Message m = (Message) ev;
			// for (LifeEvent lifeEvent : m.getLiveEvents()) {
			// if (lifeEvent.getType() == LifeEventType.DESTROY
			// /*
			// * || lifeEvent.getType() == LifeEventType.CREATE
			// */) {
			// initializer.addEvent(lifeEvent);
			// }
			// }
			// }
		}
		drawableSet = initializer.createDrawableSet(stringBounder);
		final List<Newpage> newpages = new ArrayList<>();
		for (Event ev : drawableSet.getAllEvents()) {
			if (ev instanceof Newpage) {
				newpages.add((Newpage) ev);
			}
		}
		fullDimension = drawableSet.getDimension();
		final Map<Newpage, Double> positions = new LinkedHashMap<Newpage, Double>();
		for (Newpage n : newpages) {
			positions.put(n, initializer.getYposition(stringBounder, n));
		}
		pages = create(drawableSet, positions, diagram.isShowFootbox(), diagram.getTitle().getDisplay()).getPages();
	}

	public int getNbPages() {
		return pages.size();
	}

	private PageSplitter create(DrawableSet drawableSet, Map<Newpage, Double> positions, boolean showFootbox,
			Display title) {

		final double headerHeight = drawableSet.getHeadHeight(stringBounder);
		final double tailHeight = drawableSet.getTailHeight(stringBounder, showFootbox);
		final double signatureHeight = 0;
		final double newpageHeight = drawableSet.getSkin().createComponentNewPage(drawableSet.getSkinParam())
				.getPreferredHeight(stringBounder);

		return new PageSplitter(fullDimension.getHeight(), headerHeight, positions, tailHeight, signatureHeight,
				newpageHeight, title);
	}

	public ImageData createOne(OutputStream os, final int index, boolean isWithMetadata) throws IOException {

		final Page page = pages.get(index);
		final SequenceDiagramArea area = new SequenceDiagramArea(fullDimension.getWidth(), page.getHeight());

		final TextBlock compTitle;
		final AnnotatedWorker annotatedWorker = new AnnotatedWorker(diagram, diagram.getSkinParam(), stringBounder);
		final TextBlock caption = annotatedWorker.getCaption();
		area.setCaptionArea(caption.calculateDimension(stringBounder));

		if (Display.isNull(page.getTitle())) {
			compTitle = null;
		} else {
			if (UseStyle.useBetaStyle()) {
				final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.title)
						.getMergedStyle(diagram.getSkinParam().getCurrentStyleBuilder());
				compTitle = style.createTextBlockBordered(page.getTitle(), diagram.getSkinParam().getIHtmlColorSet(),
						diagram.getSkinParam());
			} else {
				compTitle = TextBlockUtils.withMargin(TextBlockUtils.title(
						FontConfiguration.create(drawableSet.getSkinParam(), FontParam.TITLE, null), page.getTitle(),
						drawableSet.getSkinParam()), 7, 7);
			}
			final Dimension2D dimTitle = compTitle.calculateDimension(stringBounder);
			area.setTitleArea(dimTitle.getWidth(), dimTitle.getHeight());
		}
		area.initFooter(getPngTitler(FontParam.FOOTER, index), stringBounder);
		area.initHeader(getPngTitler(FontParam.HEADER, index), stringBounder);

		final TextBlock legendBlock;
		if (diagram.getLegend().isNull()) {
			legendBlock = TextBlockUtils.empty(0, 0);
		} else {
			if (UseStyle.useBetaStyle()) {
				final Style style = StyleSignatureBasic.of(SName.root, SName.document, SName.legend)
						.getMergedStyle(diagram.getSkinParam().getCurrentStyleBuilder());
				legendBlock = style.createTextBlockBordered(diagram.getLegend().getDisplay(),
						diagram.getSkinParam().getIHtmlColorSet(), diagram.getSkinParam());
			} else {
				legendBlock = EntityImageLegend.create(diagram.getLegend().getDisplay(), diagram.getSkinParam());
			}
		}
		final Dimension2D dimLegend = legendBlock.calculateDimension(stringBounder);
		area.setLegend(dimLegend, isLegendTop(), diagram.getLegend().getHorizontalAlignment());

		final UDrawable drawable = new UDrawable() {
			public void drawU(UGraphic ug) {

				double delta = 0;
				if (index > 0) {
					delta = page.getNewpage1() - page.getHeaderHeight();
				}
				if (delta < 0) {
					delta = 0;
				}

				if (compTitle != null) {
					final HColor back = diagram.calculateBackColor();
					compTitle.drawU(ug.apply(back.bg()).apply(new UTranslate(area.getTitleX(), area.getTitleY())));
				}
				caption.drawU(ug.apply(new UTranslate(area.getCaptionX(), area.getCaptionY())));

				final double delta1 = Math.max(0, area.getLegendWidth() - area.getWidth());

				final UTranslate forCore = new UTranslate(area.getSequenceAreaX() + delta1 / 2,
						area.getSequenceAreaY());
				TextBlock core = drawableSet.asTextBlock(delta, fullDimension.getWidth(), page,
						diagram.isShowFootbox());
				core = annotatedWorker.addFrame(core);
				core.drawU(ug.apply(forCore));

				drawHeader(area, ug, index);
				drawFooter(area, ug, index);

				if (area.hasLegend()) {
					legendBlock.drawU(ug.apply(new UTranslate(area.getLegendX(), area.getLegendY())));
				}
			}

		};
		return diagram.createImageBuilder(fileFormatOption).drawable(drawable).write(os);
	}

	private void drawFooter(SequenceDiagramArea area, UGraphic ug, int page) {
		final PngTitler pngTitler = getPngTitler(FontParam.FOOTER, page);
		final TextBlock text = pngTitler.getRibbonBlock();
		if (text == null) {
			return;
		}
		text.drawU(ug.apply(
				new UTranslate(area.getFooterX(diagram.getFooter().getHorizontalAlignment()), area.getFooterY())));
	}

	private void drawHeader(SequenceDiagramArea area, UGraphic ug, int page) {
		final PngTitler pngTitler = getPngTitler(FontParam.HEADER, page);
		final TextBlock text = pngTitler.getRibbonBlock();
		if (text == null) {
			return;
		}
		text.drawU(ug.apply(
				new UTranslate(area.getHeaderX(diagram.getHeader().getHorizontalAlignment()), area.getHeaderY())));
	}

	private PngTitler getPngTitler(final FontParam fontParam, int page) {
		final ISkinParam skinParam = diagram.getSkinParam();
		final DisplaySection display = diagram.getFooterOrHeaderTeoz(fontParam).withPage(page + 1, pages.size());
		final StyleSignatureBasic def = fontParam.getStyleDefinition(null);
		final Style style = def.getMergedStyle(skinParam.getCurrentStyleBuilder());

		return new PngTitler(display, style, skinParam.getIHtmlColorSet(), skinParam);
	}

	private boolean isLegendTop() {
		return diagram.getLegend().isNull() == false
				&& diagram.getLegend().getVerticalAlignment() == VerticalAlignment.TOP;
	}

}
