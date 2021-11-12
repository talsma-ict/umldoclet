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

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Stripe;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class StripeCode implements Stripe, Atom {

	final private FontConfiguration fontConfiguration;
	private final List<String> raw = new ArrayList<>();

	private boolean terminated;

	public StripeCode(FontConfiguration fontConfiguration, ISkinSimple skinParam, String line) {
//		this.skinParam = skinParam;
		this.fontConfiguration = fontConfiguration;
	}

	public List<Atom> getAtoms() {
		return Collections.<Atom>singletonList(this);
	}

	public Atom getLHeader() {
		return null;
	}

	public boolean addAndCheckTermination(String line) {
		if (Parser.isCodeEnd(line)) {
			this.terminated = true;
			return true;
		}
		this.raw.add(line);
		return false;
	}

	public final boolean isTerminated() {
		return terminated;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double width = 0;
		double height = 0;
		for (String s : raw) {
			final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), s);
			width = Math.max(width, dim.getWidth());
			height += dim.getHeight();
		}
		return new Dimension2DDouble(width, height);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		double y = 0;
		for (String s : raw) {
			final UText shape = new UText(s, fontConfiguration);
			final StringBounder stringBounder = ug.getStringBounder();
			final Dimension2D dim = stringBounder.calculateDimension(fontConfiguration.getFont(), s);
			y += dim.getHeight();
			ug.apply(UTranslate.dy(y - shape.getDescent(stringBounder))).draw(shape);
		}
	}

	public List<Atom> splitInTwo(StringBounder stringBounder, double width) {
		return Arrays.asList((Atom) this);
	}

}
