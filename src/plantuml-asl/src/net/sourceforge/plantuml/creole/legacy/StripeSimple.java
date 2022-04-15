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
package net.sourceforge.plantuml.creole.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.CreoleContext;
import net.sourceforge.plantuml.creole.CreoleHorizontalLine;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Stripe;
import net.sourceforge.plantuml.creole.StripeStyle;
import net.sourceforge.plantuml.creole.StripeStyleType;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.creole.atom.AtomEmoji;
import net.sourceforge.plantuml.creole.atom.AtomImg;
import net.sourceforge.plantuml.creole.atom.AtomMath;
import net.sourceforge.plantuml.creole.atom.AtomOpenIcon;
import net.sourceforge.plantuml.creole.atom.AtomSpace;
import net.sourceforge.plantuml.creole.atom.AtomSprite;
import net.sourceforge.plantuml.creole.command.Command;
import net.sourceforge.plantuml.creole.command.CommandCreoleColorAndSizeChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleColorChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleEmoji;
import net.sourceforge.plantuml.creole.command.CommandCreoleExposantChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleFontFamilyChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleImg;
import net.sourceforge.plantuml.creole.command.CommandCreoleLatex;
import net.sourceforge.plantuml.creole.command.CommandCreoleMath;
import net.sourceforge.plantuml.creole.command.CommandCreoleMonospaced;
import net.sourceforge.plantuml.creole.command.CommandCreoleOpenIcon;
import net.sourceforge.plantuml.creole.command.CommandCreoleQrcode;
import net.sourceforge.plantuml.creole.command.CommandCreoleSizeChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleSpace;
import net.sourceforge.plantuml.creole.command.CommandCreoleSprite;
import net.sourceforge.plantuml.creole.command.CommandCreoleStyle;
import net.sourceforge.plantuml.creole.command.CommandCreoleSvgAttributeChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleUrl;
import net.sourceforge.plantuml.emoji.Emoji;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.FontPosition;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.ImgValign;
import net.sourceforge.plantuml.math.ScientificEquationSafe;
import net.sourceforge.plantuml.openiconic.OpenIcon;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.CharHidder;

public class StripeSimple implements Stripe {

	final private Atom header;

	final private List<Atom> atoms = new ArrayList<>();

	final private Map<Character, List<Command>> commands = new HashMap<>();

	private HorizontalAlignment align = HorizontalAlignment.LEFT;

	public void setCellAlignment(HorizontalAlignment align) {
		this.align = align;
	}

	public HorizontalAlignment getCellAlignment() {
		return align;
	}

	private FontConfiguration fontConfiguration;

	final private StripeStyle style;
	final private ISkinSimple skinParam;

	@Override
	public String toString() {
		return super.toString() + " " + atoms.toString();
	}

	public Atom getLHeader() {
		return header;
	}

	public StripeSimple(FontConfiguration fontConfiguration, StripeStyle style, CreoleContext context,
			ISkinSimple skinParam, CreoleMode modeSimpleLine) {
		this.fontConfiguration = fontConfiguration;
		this.style = style;
		this.skinParam = skinParam;

		addCommand(CommandCreoleStyle.createCreole(FontStyle.BOLD));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.BOLD));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.BOLD));

		addCommand(CommandCreoleStyle.createCreole(FontStyle.ITALIC));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.ITALIC));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.ITALIC));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.PLAIN));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.PLAIN));
		if (modeSimpleLine == CreoleMode.FULL)
			addCommand(CommandCreoleStyle.createCreole(FontStyle.UNDERLINE));

		addCommand(CommandCreoleStyle.createLegacy(FontStyle.UNDERLINE));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.UNDERLINE));
		addCommand(CommandCreoleStyle.createCreole(FontStyle.STRIKE));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.STRIKE));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.STRIKE));
		addCommand(CommandCreoleStyle.createCreole(FontStyle.WAVE));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.WAVE));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.WAVE));
		addCommand(CommandCreoleStyle.createLegacy(FontStyle.BACKCOLOR));
		addCommand(CommandCreoleStyle.createLegacyEol(FontStyle.BACKCOLOR));
		addCommand(CommandCreoleSizeChange.create());
		addCommand(CommandCreoleSizeChange.createEol());
		addCommand(CommandCreoleColorChange.create());
		addCommand(CommandCreoleColorChange.createEol());
		addCommand(CommandCreoleColorAndSizeChange.create());
		addCommand(CommandCreoleColorAndSizeChange.createEol());
		addCommand(CommandCreoleExposantChange.create(FontPosition.EXPOSANT));
		addCommand(CommandCreoleExposantChange.create(FontPosition.INDICE));
		addCommand(CommandCreoleImg.create());
		addCommand(CommandCreoleQrcode.create());
		addCommand(CommandCreoleOpenIcon.create());
		addCommand(CommandCreoleEmoji.create());
		addCommand(CommandCreoleMath.create());
		addCommand(CommandCreoleLatex.create());
		addCommand(CommandCreoleSprite.create());
		addCommand(CommandCreoleSpace.create());
		addCommand(CommandCreoleFontFamilyChange.create());
		addCommand(CommandCreoleFontFamilyChange.createEol());
		addCommand(CommandCreoleMonospaced.create());
		addCommand(CommandCreoleUrl.create());
		if (SecurityUtils.allowSvgText())
			addCommand(CommandCreoleSvgAttributeChange.create());

		this.header = style.getHeader(fontConfiguration, context);

		if (this.header != null)
			this.atoms.add(this.header);

	}

	private void addCommand(Command cmd) {
		final String starters = cmd.startingChars();
		for (int i = 0; i < starters.length(); i++) {
			final char ch = starters.charAt(i);
			List<Command> localList = commands.get(ch);
			if (localList == null) {
				localList = new ArrayList<Command>();
				commands.put(ch, localList);
			}
			localList.add(cmd);
		}
	}

	public List<Atom> getAtoms() {
		if (atoms.size() == 0)
			atoms.add(AtomTextUtils.createLegacy(" ", fontConfiguration));

		return Collections.unmodifiableList(atoms);
	}

	public FontConfiguration getActualFontConfiguration() {
		return fontConfiguration;
	}

	public void setActualFontConfiguration(FontConfiguration fontConfiguration) {
		this.fontConfiguration = fontConfiguration;
	}

	public void analyzeAndAdd(String line) {
		if (Objects.requireNonNull(line).contains("" + BackSlash.hiddenNewLine()))
			throw new IllegalArgumentException(line);

		line = CharHidder.hide(line);
		if (style.getType() == StripeStyleType.HEADING) {
			fontConfiguration = fontConfigurationForHeading(fontConfiguration, style.getOrder());
			modifyStripe(line);
		} else if (style.getType() == StripeStyleType.HORIZONTAL_LINE) {
			atoms.add(CreoleHorizontalLine.create(fontConfiguration, line, style.getStyle(), skinParam));
		} else {
			modifyStripe(line);
		}
	}

	private static FontConfiguration fontConfigurationForHeading(FontConfiguration fontConfiguration, int order) {
		switch (order) {
		case 0:
			return fontConfiguration.bigger(4).bold();
		case 1:
			return fontConfiguration.bigger(2).bold();
		case 2:
			return fontConfiguration.bigger(1).bold();
		default:
			return fontConfiguration.italic();
		}
	}

	public void addImage(String src, double scale) {
		atoms.add(AtomImg.create(src, ImgValign.TOP, 0, scale, null));
	}

	public void addQrcode(String src, double scale) {
		atoms.add(AtomImg.createQrcode(src, scale));
	}

	public void addSpace(int size) {
		atoms.add(AtomSpace.create(size));
	}

	public void addUrl(Url url) {
		atoms.add(AtomTextUtils.createUrl(url, fontConfiguration, skinParam));
	}

	public void addSprite(String src, double scale, HColor color) {
		final Sprite sprite = skinParam.getSprite(src);
		if (sprite != null)
			atoms.add(new AtomSprite(color, scale, fontConfiguration, sprite, null, skinParam.getColorMapper()));
	}

	public void addOpenIcon(String src, double scale, HColor color) {
		final OpenIcon openIcon = OpenIcon.retrieve(src);
		if (openIcon != null)
			atoms.add(new AtomOpenIcon(color, scale, openIcon, fontConfiguration, null));
	}

	public void addEmoji(String emojiName, String forcedColor) {
		final Emoji emoji = Emoji.retrieve(emojiName);
		if (emoji == null)
			return;

		HColor col = null;
		if (forcedColor == null)
			col = null;
		else if (forcedColor.equals("#0") || forcedColor.equals("#000") || forcedColor.equals("#black"))
			col = fontConfiguration.getColor();
		else
			try {
				col = skinParam.getIHtmlColorSet().getColor(skinParam.getThemeStyle(), forcedColor);
			} catch (NoSuchColorException e) {
				col = null;
			}

		atoms.add(new AtomEmoji(emoji, 1, fontConfiguration.getSize2D(), col));
	}

	public void addMath(ScientificEquationSafe math) {
		atoms.add(new AtomMath(math, fontConfiguration.getColor(), fontConfiguration.getExtendedColor(),
				skinParam.getColorMapper()));
	}

	private void modifyStripe(String line) {
		final StringBuilder pending = new StringBuilder();

		while (line.length() > 0) {
			final Command cmd = searchCommand(line);
			if (cmd == null) {
				pending.append(line.charAt(0));
				line = line.substring(1);
			} else {
				addPending(pending);
				line = cmd.executeAndGetRemaining(line, this);
			}
		}
		addPending(pending);
	}

	private void addPending(StringBuilder pending) {
		if (pending.length() == 0)
			return;

		atoms.add(AtomTextUtils.createLegacy(pending.toString(), fontConfiguration));
		pending.setLength(0);
	}

	private Command searchCommand(String line) {
		final List<Command> localList = commands.get(line.charAt(0));
		if (localList != null)
			for (Command cmd : localList)
				if (cmd.matchingSize(line) != 0)
					return cmd;

		return null;
	}

	public ISkinSimple getSkinParam() {
		return skinParam;
	}

}
