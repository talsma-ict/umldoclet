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
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.EmbeddedDiagram;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.legacy.CreoleParser;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockVertical2;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;

public class BodyEnhanced1 extends BodyEnhancedAbstract implements TextBlock, WithPorts {

	private final Display rawBody2;

	private final ISkinParam skinParam;
	private final boolean lineFirst;
	private final List<Url> urls = new ArrayList<>();

	private final ILeaf entity;
	private final boolean inEllipse;
	private final Style style;

	BodyEnhanced1(HorizontalAlignment align, List<CharSequence> rawBody, ISkinParam skinParam, ILeaf entity,
			Style style) {
		super(align,
				style.getFontConfiguration(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet(), entity.getColors()),
				style);
		this.style = style;
		this.rawBody2 = Display.create(rawBody);

		this.skinParam = skinParam;

		this.lineFirst = true;

		this.entity = entity;
		this.inEllipse = false;
	}

	BodyEnhanced1(HorizontalAlignment align, Display display, ISkinParam skinParam, ILeaf entity, Style style) {
		super(align,
				style.getFontConfiguration(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet(), entity.getColors()),
				style);

		this.style = style;
		this.entity = entity;

		this.skinParam = skinParam;

		this.lineFirst = false;

		final LeafType leafType = entity.getLeafType();
		this.inEllipse = leafType == LeafType.USECASE || leafType == LeafType.USECASE_BUSINESS;

		if (inEllipse && display.size() > 0 && isBlockSeparator(display.get(0).toString()))
			display = display.add("");

		this.rawBody2 = display;

	}

	@Override
	protected double getMarginX() {
		return 6;
	}

	private static boolean isTreeOrTable(String s) {
		s = StringUtils.trinNoTrace(s);
		return Parser.isTreeStart(s) || CreoleParser.isTableLine(s);
	}

	@Override
	final protected TextBlock getArea(StringBounder stringBounder) {
		if (area != null)
			return area;

		urls.clear();
		final List<TextBlock> blocks = new ArrayList<>();

		char separator = lineFirst ? '_' : 0;
		TextBlock title = null;
		Display display = null;
		for (ListIterator<CharSequence> it = rawBody2.iterator(); it.hasNext();) {
			final CharSequence cs = it.next();
			if (cs instanceof EmbeddedDiagram) {
				if (display == null)
					display = Display.empty();
				if (display.size() > 0 || separator != 0) {
					blocks.add(decorate(stringBounder,
							new MethodsOrFieldsArea(display, skinParam, align, entity, style), separator, title));
					separator = 0;
					title = null;
					display = null;
				}
				blocks.add(TextBlockUtils.withMargin(((EmbeddedDiagram) cs).asDraw(skinParam), 2, 2));
			} else {
				final String s = cs.toString();
				if (isBlockSeparator(s)) {
					if (display == null)
						display = Display.empty();
					blocks.add(decorate(stringBounder,
							new MethodsOrFieldsArea(display, skinParam, align, entity, style), separator, title));
					separator = s.charAt(0);
					title = getTitle(s, skinParam);
					display = null;
				} else if (isTreeOrTable(s)) {
					final boolean isTable = CreoleParser.isTableLine(s);
					if (display == null)
						display = Display.empty();
					blocks.add(decorate(stringBounder,
							new MethodsOrFieldsArea(display, skinParam, align, entity, style), separator, title));

					separator = 0;
					title = null;
					display = null;
					final List<CharSequence> allTree = buildTreeOrTable(s, it);
					final FontConfiguration fontConfiguration = style.getFontConfiguration(skinParam.getThemeStyle(),
							skinParam.getIHtmlColorSet());
					TextBlock bloc = Display.create(allTree).create7(fontConfiguration, align, skinParam,
							CreoleMode.FULL);
					if (isTable)
						bloc = TextBlockUtils.withMargin(bloc, 10, 10, 0, 5);

					blocks.add(bloc);
				} else {
					if (display == null)
						display = Display.empty();
					display = display.add(cs);
					if (cs instanceof Member && ((Member) cs).getUrl() != null)
						urls.add(((Member) cs).getUrl());

				}
			}
		}

		if (display == null)
			display = Display.empty();
		if (inEllipse && display.size() == 0)
			display = display.add("");

		blocks.add(decorate(stringBounder, new MethodsOrFieldsArea(display, skinParam, align, entity, style), separator,
				title));

		if (blocks.size() == 1)
			this.area = blocks.get(0);
		else
			this.area = new TextBlockVertical2(blocks, align);

		if (skinParam.minClassWidth() > 0)
			this.area = TextBlockUtils.withMinWidth(this.area, skinParam.minClassWidth(), align);

		return area;
	}

	private static List<CharSequence> buildTreeOrTable(String init, ListIterator<CharSequence> it) {
		final List<CharSequence> result = new ArrayList<>();
		final Pattern p = Pattern.compile("^(\\s+)");
		final Matcher m = p.matcher(init);
		String start = "";
		if (m.find())
			start = m.group(1);

		result.add(purge(init, start));
		while (it.hasNext()) {
			String s = it.next().toString();
			if (isTreeOrTable(s)) {
				result.add(purge(s, start));
			} else {
				it.previous();
				return result;
			}

		}
		return result;
	}

	private static String purge(String s, String start) {
		if (s.startsWith(start))
			return s.substring(start.length());

		return s;
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final TextBlock area = getArea(stringBounder);
		if (area instanceof WithPorts)
			return ((WithPorts) area).getPorts(stringBounder);

		return new Ports();
	}

	public List<Url> getUrls() {
		return Collections.unmodifiableList(urls);
	}

	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		return getArea(stringBounder).getInnerPosition(member, stringBounder, strategy);
	}

}
