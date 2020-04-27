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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.creole.atom.AbstractAtom;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.creole.atom.AtomText;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Fission {

	private final Stripe stripe;
	private final LineBreakStrategy maxWidth;

	public Fission(Stripe stripe, LineBreakStrategy maxWidth) {
		this.stripe = stripe;
		this.maxWidth = maxWidth;
		if (maxWidth == null) {
			throw new IllegalArgumentException();
		}
	}

	static private boolean NEW_MODE = true;

	public List<Stripe> getSplitted2(StringBounder stringBounder) {
		final double valueMaxWidth = maxWidth.getMaxWidth();
		if (valueMaxWidth == 0) {
			return Arrays.asList(stripe);
		}
		final List<Stripe> result = new ArrayList<Stripe>();
		StripeSimple current = new StripeSimple(stripe.getHeader());
		double remainingSpace = valueMaxWidth;
		for (Atom atom : noHeader()) {
			while (true) {
				final List<Atom> splitInTwo = atom.splitInTwo(stringBounder, remainingSpace);
				final Atom part1 = splitInTwo.get(0);
				final double widthPart1 = part1.calculateDimension(stringBounder).getWidth();
				current.addAtom(part1, widthPart1);
				remainingSpace -= widthPart1;
				if (remainingSpace <= 0) {
					result.add(current);
					current = new StripeSimple(blank(stripe.getHeader()));
					remainingSpace = valueMaxWidth;
				}
				if (splitInTwo.size() == 1) {
					break;
				}
				atom = splitInTwo.get(1);
				if (remainingSpace < valueMaxWidth
						&& atom.calculateDimension(stringBounder).getWidth() > remainingSpace) {
					result.add(current);
					current = new StripeSimple(blank(stripe.getHeader()));
					remainingSpace = valueMaxWidth;
				}
			}
		}
		if (remainingSpace < valueMaxWidth) {
			result.add(current);
		}
		return Collections.unmodifiableList(result);
	}

	public List<Stripe> getSplitted(StringBounder stringBounder) {
		if (NEW_MODE)
			return getSplitted2(stringBounder);
		final double valueMaxWidth = maxWidth.getMaxWidth();
		if (valueMaxWidth == 0) {
			return Arrays.asList(stripe);
		}
		final List<Stripe> result = new ArrayList<Stripe>();
		StripeSimple current = new StripeSimple(stripe.getHeader());
		for (Atom atom : noHeader()) {
			for (Atom atomSplitted : getSplitted(stringBounder, atom)) {
				final double width = atomSplitted.calculateDimension(stringBounder).getWidth();
				if (current.totalWidth + width > valueMaxWidth) {
					result.add(current);
					current = new StripeSimple(blank(stripe.getHeader()));
				}
				current.addAtom(atomSplitted, width);
			}
		}
		if (current.totalWidth > 0) {
			result.add(current);
		}
		return Collections.unmodifiableList(result);
	}

	private List<Atom> noHeader() {
		final List<Atom> atoms = stripe.getAtoms();
		if (stripe.getHeader() == null) {
			return atoms;
		}
		return atoms.subList(1, atoms.size());
	}

	private static Atom blank(final Atom header) {
		if (header == null) {
			return null;
		}
		return new AbstractAtom() {

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return header.calculateDimension(stringBounder);
			}

			public double getStartingAltitude(StringBounder stringBounder) {
				return header.getStartingAltitude(stringBounder);
			}

			public void drawU(UGraphic ug) {
			}

		};
	}

	private Collection<? extends Atom> getSplitted(StringBounder stringBounder, Atom atom) {
		if (atom instanceof AtomText) {
			return ((AtomText) atom).getSplitted(stringBounder, maxWidth);
		}
		return Collections.singleton(atom);
	}

	// private List<Stripe> getSplittedSimple() {
	// final StripeSimple result = new StripeSimple();
	// for (Atom atom : stripe.getAtoms1()) {
	// result.addAtom(atom, 0);
	//
	// }
	// return Arrays.asList((Stripe) result);
	// }

	static class StripeSimple implements Stripe {

		private final List<Atom> atoms = new ArrayList<Atom>();
		private double totalWidth;

		private StripeSimple(Atom header) {
			if (header != null) {
				this.atoms.add(header);
			}
		}

		public List<Atom> getAtoms() {
			return Collections.unmodifiableList(atoms);
		}

		private void addAtom(Atom atom, double width) {
			this.atoms.add(atom);
			this.totalWidth += width;
		}

		public Atom getHeader() {
			return null;
		}

	}

}
