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
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmbeddedDiagram;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockWithUrl;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.Ports;
import net.sourceforge.plantuml.svek.WithPorts;
import net.sourceforge.plantuml.ugraphic.PlacementStrategy;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyVisibility;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Center;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Left;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Right;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.utils.CharHidder;

public class MethodsOrFieldsArea extends AbstractTextBlock implements TextBlock, WithPorts {

	public TextBlock asBlockMemberImpl() {
		return new TextBlockLineBefore(TextBlockUtils.withMargin(this, 6, 4));
	}

	private final FontParam fontParam;
	private final ISkinParam skinParam;
	private final Rose rose = new Rose();
	private final Display members;
	private final HorizontalAlignment align;
	private final Stereotype stereotype;
	private final ILeaf leaf;
	private final Style style;

	public MethodsOrFieldsArea(Display members, FontParam fontParam, ISkinParam skinParam, Stereotype stereotype,
			ILeaf leaf, Style style) {
		this(members, fontParam, skinParam, HorizontalAlignment.LEFT, stereotype, leaf, style);
	}

	public MethodsOrFieldsArea(Display members, FontParam fontParam, ISkinParam skinParam, HorizontalAlignment align,
			Stereotype stereotype, ILeaf leaf, Style style) {
		this.style = style;
		this.leaf = leaf;
		this.stereotype = stereotype;
		this.align = align;
		this.skinParam = skinParam;
		this.fontParam = fontParam;
		this.members = members;
	}

	private boolean hasSmallIcon() {
		if (skinParam.classAttributeIconSize() == 0) {
			return false;
		}
		for (CharSequence cs : members) {
			if (cs instanceof Member == false)
				continue;
			final Member m = (Member) cs;
			if (m.getVisibilityModifier() != null)
				return true;

		}
		return false;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double smallIcon = 0;
		if (hasSmallIcon()) {
			smallIcon = skinParam.getCircledCharacterRadius() + 3;
		}
		double x = 0;
		double y = 0;
		for (CharSequence cs : members) {
			final TextBlock bloc = createTextBlock(cs);
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			x = Math.max(dim.getWidth(), x);
			y += dim.getHeight();
		}
		x += smallIcon;
		return new Dimension2DDouble(x, y);
	}

	@Override
	public Ports getPorts(StringBounder stringBounder) {
		final Ports result = new Ports();
		double y = 0;

		for (CharSequence cs : members) {
			final TextBlock bloc = createTextBlock(cs);
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			final Elected port = getElected(leaf.getPortShortNames(), convert(cs));
			if (port != null)
				result.add(port.getShortName(), port.getScore(), y, dim.getHeight());

			y += dim.getHeight();
		}
		return result;
	}

	private String convert(CharSequence cs) {
		if (cs instanceof Member)
			return ((Member) cs).getDisplay(false);
		return cs.toString();
	}

	public Elected getElected(Collection<String> shortNames, String cs) {
		for (String shortName : new HashSet<>(shortNames)) {
			final int score = getScore(shortName, cs);
			if (score > 0)
				return new Elected(shortName, score);
		}
		return null;
	}

	private int getScore(String shortName, String cs) {
		if (cs.matches(".*\\b" + shortName + "\\b.*"))
			return 100;

		if (cs.contains(shortName))
			return 50;

		return 0;
	}

	private TextBlock createTextBlock(CharSequence cs) {

		FontConfiguration config;
		if (style != null)
			config = new FontConfiguration(skinParam, style);
		else
			config = new FontConfiguration(skinParam, fontParam, stereotype);

		if (cs instanceof Member) {
			final Member m = (Member) cs;
			final boolean withVisibilityChar = skinParam.classAttributeIconSize() == 0;
			String s = m.getDisplay(withVisibilityChar);
			if (withVisibilityChar && s.startsWith("#"))
				s = CharHidder.addTileAtBegin(s);

			if (m.isAbstract())
				config = config.italic();

			if (m.isStatic())
				config = config.underline();

			TextBlock bloc = Display.getWithNewlines(s).create8(config, align, skinParam, CreoleMode.SIMPLE_LINE,
					skinParam.wrapWidth());
			bloc = TextBlockUtils.fullInnerPosition(bloc, m.getDisplay(false));
			return new TextBlockTracer(m, bloc);
		}

		if (cs instanceof EmbeddedDiagram) {
			return ((EmbeddedDiagram) cs).asDraw(skinParam);
		}

		return Display.getWithNewlines(cs.toString()).create8(config, align, skinParam, CreoleMode.SIMPLE_LINE,
				skinParam.wrapWidth());

	}

	static class TextBlockTracer extends AbstractTextBlock implements TextBlock {

		private final TextBlock bloc;
		private final Url url;

		public TextBlockTracer(Member m, TextBlock bloc) {
			this.bloc = bloc;
			this.url = m.getUrl();
		}

		public void drawU(UGraphic ug) {
			if (url != null)
				ug.startUrl(url);

			bloc.drawU(ug);
			if (url != null)
				ug.closeUrl();

		}

		public Dimension2D calculateDimension(StringBounder stringBounder) {
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			return dim;
		}

		@Override
		public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
			return bloc.getInnerPosition(member, stringBounder, strategy);
		}

	}

	private TextBlock getUBlock(final VisibilityModifier modifier, Url url) {
		if (modifier == null) {
			return new AbstractTextBlock() {

				public void drawU(UGraphic ug) {
				}

				@Override
				public Rectangle2D getInnerPosition(String member, StringBounder stringBounder,
						InnerStrategy strategy) {
					return null;
				}

				public Dimension2D calculateDimension(StringBounder stringBounder) {
					return new Dimension2DDouble(1, 1);
				}
			};
		}
		final HColor backColor;
		final HColor borderColor;
		if (UseStyle.useBetaStyle()) {
			final Style style = modifier.getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());
			borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
			final boolean isField = modifier.isField();
			backColor = isField ? null
					: style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
							skinParam.getIHtmlColorSet());
		} else {
			borderColor = rose.getHtmlColor(skinParam, modifier.getForeground());
			backColor = modifier.getBackground() == null ? null
					: rose.getHtmlColor(skinParam, modifier.getBackground());
		}

		final TextBlock uBlock = modifier.getUBlock(skinParam.classAttributeIconSize(), borderColor, backColor,
				url != null);
		return TextBlockWithUrl.withUrl(uBlock, url);
	}

	public boolean contains(String member) {
		for (CharSequence cs : members) {
			final Member att = (Member) cs;
			if (att.getDisplay(false).startsWith(member))
				return true;

		}
		return false;
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
		final ULayoutGroup group = getLayout(stringBounder);
		final Dimension2D dim = calculateDimension(stringBounder);
		return group.getInnerPosition(member, dim.getWidth(), dim.getHeight(), stringBounder);
	}

	private ULayoutGroup getLayout(final StringBounder stringBounder) {
		final ULayoutGroup group;
		if (hasSmallIcon()) {
			group = new ULayoutGroup(
					new PlacementStrategyVisibility(stringBounder, skinParam.getCircledCharacterRadius() + 3));
			for (CharSequence cs : members) {
				final TextBlock bloc = createTextBlock(cs);
				if (cs instanceof EmbeddedDiagram) {
					group.add(getUBlock(null, null));
				} else {
					final Member att = (Member) cs;
					final VisibilityModifier modifier = att.getVisibilityModifier();
					group.add(getUBlock(modifier, att.getUrl()));
				}
				group.add(bloc);
			}
		} else {
			final PlacementStrategy placementStrategy;
			if (align == HorizontalAlignment.LEFT)
				placementStrategy = new PlacementStrategyY1Y2Left(stringBounder);
			else if (align == HorizontalAlignment.CENTER)
				placementStrategy = new PlacementStrategyY1Y2Center(stringBounder);
			else
				placementStrategy = new PlacementStrategyY1Y2Right(stringBounder);

			group = new ULayoutGroup(placementStrategy);
			for (CharSequence cs : members) {
				final TextBlock bloc = createTextBlock(cs);
				group.add(bloc);
			}
		}
		return group;
	}

	public void drawU(UGraphic ug) {
		final ULayoutGroup group = getLayout(ug.getStringBounder());
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		group.drawU(ug, dim.getWidth(), dim.getHeight());
	}

}
