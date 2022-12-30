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
package net.sourceforge.plantuml.creole;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.creole.atom.AbstractAtom;
import net.sourceforge.plantuml.creole.atom.Atom;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Fission {

	private final Stripe stripe;
	private final LineBreakStrategy maxWidth;

	public Fission(Stripe stripe, LineBreakStrategy maxWidth) {
		this.stripe = stripe;
		this.maxWidth = Objects.requireNonNull(maxWidth);
	}

	public List<Stripe> getSplitted(StringBounder stringBounder) {
		final double valueMaxWidth = Math.abs(maxWidth.getMaxWidth());
		if (valueMaxWidth == 0)
			return Arrays.asList(stripe);

		final List<Stripe> result = new ArrayList<>();
		StripeSimpleInternal line = new StripeSimpleInternal(false, stringBounder, stripe.getLHeader());
		result.add(line);

		final Deque<Neutron> all = new ArrayDeque<>();
		for (Atom atom : noHeader())
			for (Neutron n : atom.getNeutrons())
				all.addLast(n);
		if (all.peekLast().getType() != NeutronType.ZWSP_SEPARATOR)
			all.addLast(Neutron.zwspSeparator());

		while (all.size() > 0) {
			final Neutron current = all.removeFirst();
			if (current.getType() == NeutronType.ZWSP_SEPARATOR && line.getWidth() > valueMaxWidth) {
				all.addFirst(current);
				final List<Neutron> removed = line.slightyShorten();
				for (int i = removed.size() - 1; i >= 0; i--)
					all.addFirst(removed.get(i));

				line = new StripeSimpleInternal(true, stringBounder, blank(stripe.getLHeader()));
				result.add(line);
			} else
				line.addNeutron(current);
		}

		for (Stripe l : result)
			((StripeSimpleInternal) l).removeFinalSpaces();

		while (result.size() > 1 && ((StripeSimpleInternal) result.get(result.size() - 1)).isWhite())
			result.remove(result.size() - 1);

		return Collections.unmodifiableList(result);

	}

	private List<Atom> noHeader() {
		final List<Atom> atoms = stripe.getAtoms();
		if (stripe.getLHeader() == null)
			return atoms;

		return atoms.subList(1, atoms.size());
	}

	static class StripeSimpleInternal implements Stripe {

		private final boolean removeInitialSpaces;
		private final Atom header;
		private final List<Neutron> neutrons = new ArrayList<>();
		private final StringBounder stringBounder;
		private double width;

		private StripeSimpleInternal(boolean removeInitialSpaces, StringBounder stringBounder, Atom header) {
			this.removeInitialSpaces = removeInitialSpaces;
			this.stringBounder = stringBounder;
			this.header = header;
			if (header != null)
				width += header.calculateDimension(stringBounder).getWidth();
		}

		public double getWidth() {
			if (width == -1)
				throw new IllegalStateException();
//			double width = 0;
//			if (header != null)
//				width += header.calculateDimension(stringBounder).getWidth();
//			for (Neutron n : neutrons)
//				width += n.getWidth(stringBounder);
			return width;
		}

		@Override
		public String toString() {
			if (header != null)
				return header.toString() + " " + neutrons;
			return neutrons.toString();
		}

		public List<Neutron> slightyShorten() {
			if (neutrons.size() == 0)
				throw new IllegalStateException();

			final int lastZwsp = lastZwsp();
			if (lastZwsp == -1)
				return Collections.emptyList();

			this.width = -1;
			final List<Neutron> removed = new ArrayList<Neutron>(neutrons.subList(lastZwsp, neutrons.size()));
			while (neutrons.size() > lastZwsp)
				neutrons.remove(neutrons.size() - 1);

			return removed;

		}

		private boolean isWhite() {
			for (Neutron n : neutrons)
				if (n.getType() != NeutronType.ZWSP_SEPARATOR && n.getType() != NeutronType.SPACE)
					return false;
			return true;
		}

		private void removeFinalSpaces() {
			while (neutrons.size() > 0 && neutrons.get(0).getType() == NeutronType.ZWSP_SEPARATOR)
				neutrons.remove(0);
			while (neutrons.size() > 1
					&& (last().getType() == NeutronType.SPACE || last().getType() == NeutronType.ZWSP_SEPARATOR))
				neutrons.remove(neutrons.size() - 1);
		}

		private Neutron last() {
			return neutrons.get(neutrons.size() - 1);
		}

		private int lastZwsp() {
			for (int i = neutrons.size() - 1; i >= 0; i--)
				if (neutrons.get(i).getType() == NeutronType.ZWSP_SEPARATOR)
					return i;
			return -1;
		}

		public List<Atom> getAtoms() {
			final List<Atom> result = new ArrayList<>();
			if (header != null)
				result.add(header);

			for (Neutron n : neutrons)
				if (n.getType() != NeutronType.ZWSP_SEPARATOR) {
					if (removeInitialSpaces && result.size() == 0 && n.getType() == NeutronType.SPACE)
						continue;
					result.add(n.asAtom());
				}
			return Collections.unmodifiableList(result);
		}

		private void addNeutron(Neutron neutron) {
			if (width == -1)
				throw new IllegalStateException();

			if (neutron.getType() == NeutronType.ZWSP_SEPARATOR && this.neutrons.size() == 0)
				return;

			if (neutron.getType() == NeutronType.ZWSP_SEPARATOR && this.neutrons.size() > 0
					&& last().getType() == NeutronType.ZWSP_SEPARATOR)
				return;

			if (removeInitialSpaces && this.neutrons.size() == 0 && neutron.getType() == NeutronType.SPACE)
				return;

			this.neutrons.add(neutron);
			this.width += neutron.getWidth(stringBounder);
		}

		public Atom getLHeader() {
			return null;
		}

	}

	private static Atom blank(final Atom header) {
		if (header == null)
			return null;

		return new AbstractAtom() {

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return header.calculateDimension(stringBounder);
			}

			public double getStartingAltitude(StringBounder stringBounder) {
				return header.getStartingAltitude(stringBounder);
			}

			public void drawU(UGraphic ug) {
			}

		};
	}

}
