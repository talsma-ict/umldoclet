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
package net.sourceforge.plantuml.creole.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import net.sourceforge.plantuml.creole.atom.AtomImg;
import net.sourceforge.plantuml.creole.atom.AtomMath;
import net.sourceforge.plantuml.creole.atom.AtomOpenIcon;
import net.sourceforge.plantuml.creole.atom.AtomSpace;
import net.sourceforge.plantuml.creole.atom.AtomSprite;
import net.sourceforge.plantuml.creole.command.Command;
import net.sourceforge.plantuml.creole.command.CommandCreoleColorAndSizeChange;
import net.sourceforge.plantuml.creole.command.CommandCreoleColorChange;
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
import net.sourceforge.plantuml.utils.CharHidder;

public class StripeSimple implements Stripe {

	final private Atom header;

	final private List<Atom> atoms = new ArrayList<Atom>();
	final private List<Command> commands = new ArrayList<Command>();
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

		// class Splitter
		this.commands.add(CommandCreoleStyle.createCreole(FontStyle.BOLD));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.BOLD));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.BOLD));
		this.commands.add(CommandCreoleStyle.createCreole(FontStyle.ITALIC));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.ITALIC));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.ITALIC));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.PLAIN));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.PLAIN));
		if (modeSimpleLine == CreoleMode.FULL) {
			this.commands.add(CommandCreoleStyle.createCreole(FontStyle.UNDERLINE));
		}
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.UNDERLINE));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.UNDERLINE));
		this.commands.add(CommandCreoleStyle.createCreole(FontStyle.STRIKE));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.STRIKE));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.STRIKE));
		this.commands.add(CommandCreoleStyle.createCreole(FontStyle.WAVE));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.WAVE));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.WAVE));
		this.commands.add(CommandCreoleStyle.createLegacy(FontStyle.BACKCOLOR));
		this.commands.add(CommandCreoleStyle.createLegacyEol(FontStyle.BACKCOLOR));
		this.commands.add(CommandCreoleSizeChange.create());
		this.commands.add(CommandCreoleSizeChange.createEol());
		this.commands.add(CommandCreoleColorChange.create());
		this.commands.add(CommandCreoleColorChange.createEol());
		this.commands.add(CommandCreoleColorAndSizeChange.create());
		this.commands.add(CommandCreoleColorAndSizeChange.createEol());
		this.commands.add(CommandCreoleExposantChange.create(FontPosition.EXPOSANT));
		this.commands.add(CommandCreoleExposantChange.create(FontPosition.INDICE));
		this.commands.add(CommandCreoleImg.create());
		this.commands.add(CommandCreoleQrcode.create());
		this.commands.add(CommandCreoleOpenIcon.create(skinParam.getIHtmlColorSet()));
		this.commands.add(CommandCreoleMath.create());
		this.commands.add(CommandCreoleLatex.create());
		this.commands.add(CommandCreoleSprite.create(skinParam.getIHtmlColorSet()));
		this.commands.add(CommandCreoleSpace.create());
		this.commands.add(CommandCreoleFontFamilyChange.create());
		this.commands.add(CommandCreoleFontFamilyChange.createEol());
		this.commands.add(CommandCreoleMonospaced.create(skinParam.getMonospacedFamily()));
		this.commands.add(CommandCreoleUrl.create(skinParam));
		if (SecurityUtils.allowSvgText()) {
			this.commands.add(CommandCreoleSvgAttributeChange.create());
		}

		this.header = style.getHeader(fontConfiguration, context);

		if (this.header != null) {
			this.atoms.add(this.header);
		}
	}

	public List<Atom> getAtoms() {
		if (atoms.size() == 0) {
			atoms.add(AtomText.create(" ", fontConfiguration));
		}
		return Collections.unmodifiableList(atoms);
	}

	public FontConfiguration getActualFontConfiguration() {
		return fontConfiguration;
	}

	public void setActualFontConfiguration(FontConfiguration fontConfiguration) {
		this.fontConfiguration = fontConfiguration;
	}

	public void analyzeAndAdd(String line) {
		if (line == null) {
			throw new IllegalArgumentException();
		}
		if (line.contains("" + BackSlash.hiddenNewLine())) {
			throw new IllegalArgumentException(line);
		}
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
		if (order == 0) {
			fontConfiguration = fontConfiguration.bigger(4).bold();
		} else if (order == 1) {
			fontConfiguration = fontConfiguration.bigger(2).bold();
		} else if (order == 2) {
			fontConfiguration = fontConfiguration.bigger(1).bold();
		} else {
			fontConfiguration = fontConfiguration.italic();
		}
		return fontConfiguration;
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
		atoms.add(AtomText.createUrl(url, fontConfiguration, skinParam));
	}

	public void addSprite(String src, double scale, HColor color) {
		final Sprite sprite = skinParam.getSprite(src);
		if (sprite != null) {
			atoms.add(new AtomSprite(color, scale, fontConfiguration, sprite, null));
		}
	}

	public void addOpenIcon(String src, double scale, HColor color) {
		final OpenIcon openIcon = OpenIcon.retrieve(src);
		if (openIcon != null) {
			atoms.add(new AtomOpenIcon(color, scale, openIcon, fontConfiguration, null));
		}
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
		if (pending.length() == 0) {
			return;
		}
		atoms.add(AtomText.create(pending.toString(), fontConfiguration));
		pending.setLength(0);
	}

	private Command searchCommand(String line) {
		for (Command cmd : commands) {
			final int i = cmd.matchingSize(line);
			if (i != 0) {
				return cmd;
			}
		}
		return null;
	}

}
