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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.EmbeddedDiagram;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.creole.CreoleContext;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBuilder;
import net.sourceforge.plantuml.creole.Stripe;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorRuntimeException;

public class CreoleParser implements SheetBuilder {

	private final FontConfiguration fontConfiguration;
	private final ISkinSimple skinParam;
	private final HorizontalAlignment horizontalAlignment;
	private final CreoleMode creoleMode;
	private final FontConfiguration stereotype;

	public CreoleParser(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple skinParam, CreoleMode creoleMode, FontConfiguration stereotype) {
		this.stereotype = stereotype;
		this.creoleMode = creoleMode;
		this.fontConfiguration = fontConfiguration;
		this.skinParam = Objects.requireNonNull(skinParam);
		this.horizontalAlignment = horizontalAlignment;
	}

	private Stripe createStripe(String line, CreoleContext context, Stripe lastStripe,
			FontConfiguration fontConfiguration) {
		if (lastStripe instanceof StripeCode) {
			final StripeCode code = (StripeCode) lastStripe;
			if (code.isTerminated()) {
				lastStripe = null;
			} else {
				final boolean terminated = code.addAndCheckTermination(line);
				return null;
			}
		}

		if (lastStripe instanceof StripeTable && isTableLine(line)) {
			final StripeTable table = (StripeTable) lastStripe;
			table.analyzeAndAddLine(line);
			return null;
		} else if (lastStripe instanceof StripeTree && Parser.isTreeStart(StringUtils.trinNoTrace(line))) {
			final StripeTree tree = (StripeTree) lastStripe;
			tree.analyzeAndAdd(line);
			return null;
		} else if (isTableLine(line)) {
			return new StripeTable(fontConfiguration, skinParam, line);
		} else if (Parser.isTreeStart(line)) {
			return new StripeTree(fontConfiguration, skinParam, line);
		} else if (Parser.isCodeStart(line)) {
			return new StripeCode(fontConfiguration.changeFamily(Parser.MONOSPACED), skinParam, line);
		}
		return new CreoleStripeSimpleParser(line, context, fontConfiguration, skinParam, creoleMode)
				.createStripe(context);
	}

	public static boolean isTableLine(String line) {
		return line.matches("^(\\<#\\w+(,#?\\w+)?\\>)?\\|(\\=)?.*\\|$");
	}

	public static boolean doesStartByColor(String line) {
		return line.matches("^\\=?\\s*(\\<#\\w+(,#?\\w+)?\\>).*");
	}

	public Sheet createSheet(Display display) {
		final Sheet sheet = new Sheet(horizontalAlignment);
		if (Display.isNull(display) == false) {
			final CreoleContext context = new CreoleContext();
			for (CharSequence cs : display) {
				final Stripe stripe;
				if (cs instanceof EmbeddedDiagram) {
					final Atom atom = ((EmbeddedDiagram) cs).asDraw(skinParam);
					stripe = new Stripe() {
						public Atom getLHeader() {
							return null;
						}

						public List<Atom> getAtoms() {
							return Arrays.asList(atom);
						}
					};
				} else if (cs instanceof Stereotype) {
					for (String st : ((Stereotype) cs).getLabels(skinParam.guillemet())) {
						sheet.add(createStripe(st, context, sheet.getLastStripe(), stereotype));
					}
					continue;
				} else {
					stripe = createStripe(cs.toString(), context, sheet.getLastStripe(), fontConfiguration);
				}
				if (stripe != null) {
					sheet.add(stripe);
				}
			}
		}
		return sheet;
	}

	public static void checkColor(Display result) throws NoSuchColorException {
		FontConfiguration fc = FontConfiguration.blackBlueTrue(UFont.byDefault(10));
		try {
			new CreoleParser(fc, HorizontalAlignment.LEFT, new SpriteContainerEmpty(), CreoleMode.FULL, fc)
					.createSheet(result);
		} catch (NoSuchColorRuntimeException e) {
			throw new NoSuchColorException();
		}
	}
}
