/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.mindmap;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class StripeFrontier {

	private final SortedSet<Stripe> stripes = new TreeSet<Stripe>();

	public StripeFrontier() {
		this.stripes.add(new Stripe(-Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE));
	}

	public boolean isEmpty() {
		return stripes.size() == 1;
	}

	@Override
	public String toString() {
		return stripes.toString();
	}

	public boolean contains(double x, double y) {
		for (Stripe stripe : stripes) {
			if (stripe.contains(x)) {
				return y <= stripe.getValue();
			}
		}
		throw new UnsupportedOperationException();
	}

	public double getContact(double x1, double x2) {
		final SortedSet<Stripe> collisions = collisionning(x1, x2);
		double result = -Double.MAX_VALUE;
		for (Stripe strip : collisions) {
			result = Math.max(result, strip.getValue());
		}
		return result;

	}

	public void addSegment(double x1, double x2, double value) {
		if (x2 <= x1) {
			System.err.println("x1=" + x1);
			System.err.println("x2=" + x2);
			throw new IllegalArgumentException();
		}
		final SortedSet<Stripe> collisions = collisionning(x1, x2);
		if (collisions.size() > 1) {
			final Iterator<Stripe> it = collisions.iterator();
			it.next();
			double x = x1;
			while (it.hasNext()) {
				final Stripe tmp = it.next();
				addSegment(x, tmp.getStart(), value);
				x = tmp.getStart();
			}
			addSegment(x, x2, value);
			// System.err.println("x1=" + x1);
			// System.err.println("x2=" + x2);
			// System.err.println("All=" + stripes);
			// System.err.println("collisions" + collisions);
			// throw new UnsupportedOperationException();
		} else {
			final Stripe touch = collisions.iterator().next();
			addSingleInternal(x1, x2, value, touch);
		}
	}

	private void addSingleInternal(double x1, double x2, double value, final Stripe touch) {
		if (value <= touch.getValue()) {
			return;
		}
		final boolean ok = this.stripes.remove(touch);
		assert ok;
		if (touch.getStart() != x1) {
			this.stripes.add(new Stripe(touch.getStart(), x1, touch.getValue()));
		}
		this.stripes.add(new Stripe(x1, x2, value));
		if (x2 != touch.getEnd()) {
			this.stripes.add(new Stripe(x2, touch.getEnd(), touch.getValue()));
		}
		assert checkConsistent();
	}

	private boolean checkConsistent() {
		Stripe last = null;
		for (Stripe stripe : stripes) {
			if (last == null && stripe.getStart() != -Double.MAX_VALUE) {
				return false;
			}
			if (last != null && last.getEnd() != stripe.getStart()) {
				return false;
			}
			last = stripe;
		}
		if (last.getEnd() != Double.MAX_VALUE) {
			return false;
		}
		return true;
	}

	private SortedSet<Stripe> collisionning(double x1, double x2) {
		final SortedSet<Stripe> result = new TreeSet<Stripe>();
		for (Iterator<Stripe> it = stripes.iterator(); it.hasNext();) {
			Stripe stripe = it.next();
			if (x1 >= stripe.getEnd()) {
				continue;
			}
			result.add(stripe);
			if (x2 <= stripe.getEnd()) {
				return result;
			}
		}
		throw new UnsupportedOperationException();
	}
}
