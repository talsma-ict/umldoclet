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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.plantuml.graphic.StringBounder;

public class ConstraintSet {

	private final ParticipantBoxSimple firstBorder;

	private final ParticipantBoxSimple lastborder;

	final private List<Pushable> participantList = new ArrayList<>();
	final private Map<List<Pushable>, Constraint> constraints = new HashMap<List<Pushable>, Constraint>();

	public ConstraintSet(Collection<? extends Pushable> all, double freeX) {
		this.participantList.add(firstBorder = new ParticipantBoxSimple(0, "LEFT"));
		this.participantList.addAll(all);
		this.participantList.add(lastborder = new ParticipantBoxSimple(freeX, "RIGHT"));
	}

	@Override
	public String toString() {
		return constraints.values().toString();
	}

	public double getMaxX() {
		return lastborder.getCenterX(null);
	}

	public Constraint getConstraint(Pushable p1, Pushable p2) {
		if (p1 == null || p2 == null || p1 == p2) {
			throw new IllegalArgumentException();
		}
		final int i1 = participantList.indexOf(p1);
		final int i2 = participantList.indexOf(p2);
		if (i1 == -1 || i2 == -1) {
			throw new IllegalArgumentException();
		}
		if (i1 > i2) {
			return getConstraint(p2, p1);
		}
		final List<Pushable> key = Arrays.asList(p1, p2);
		Constraint result = constraints.get(key);
		if (result == null) {
			result = new Constraint(p1, p2);
			constraints.put(key, result);
		}
		return result;
	}

	public Constraint getConstraintAfter(Pushable p1) {
		return getConstraint(Objects.requireNonNull(p1), getNext(p1));
	}

	public Constraint getConstraintBefore(Pushable p1) {
		return getConstraint(Objects.requireNonNull(p1), getPrevious(p1));
	}

	public Pushable getPrevious(Pushable p) {
		return getOtherParticipant(p, -1);
	}

	public Pushable getNext(Pushable p) {
		return getOtherParticipant(p, 1);
	}

	private Pushable getOtherParticipant(Pushable p, int delta) {
		final int i = participantList.indexOf(p);
		if (i == -1) {
			throw new IllegalArgumentException();
		}
		return participantList.get(i + delta);
	}

	public void takeConstraintIntoAccount(StringBounder stringBounder) {
		for (int dist = 1; dist < participantList.size(); dist++) {
			pushEverybody(stringBounder, dist);
		}
	}

	private void pushEverybody(StringBounder stringBounder, int dist) {
		for (int i = 0; i < participantList.size() - dist; i++) {
			final Pushable p1 = participantList.get(i);
			final Pushable p2 = participantList.get(i + dist);
			final Constraint c = getConstraint(p1, p2);
			ensureSpaceAfter(stringBounder, p1, p2, c.getValue());
		}
	}

	public void pushToLeftParticipantBox(double deltaX, Pushable firstToChange, boolean including) {
		if (deltaX <= 0) {
			throw new IllegalArgumentException();
		}
		Objects.requireNonNull(firstToChange);
		// freeX += deltaX;
		boolean founded = false;
		for (Pushable box : participantList) {
			if (box.equals(firstToChange)) {
				founded = true;
				if (including == false) {
					continue;
				}
			}
			if (founded) {
				box.pushToLeft(deltaX);
			}
		}
	}

	public void pushToLeft(double delta) {
		pushToLeftParticipantBox(delta, firstBorder, true);
	}

	private void ensureSpaceAfter(StringBounder stringBounder, Pushable p1, Pushable p2, double space) {
		if (p1.equals(p2)) {
			throw new IllegalArgumentException();
		}
		if (p1.getCenterX(stringBounder) > p2.getCenterX(stringBounder)) {
			ensureSpaceAfter(stringBounder, p2, p1, space);
			return;
		}
		assert p1.getCenterX(stringBounder) < p2.getCenterX(stringBounder);
		final double existingSpace = p2.getCenterX(stringBounder) - p1.getCenterX(stringBounder);
		if (existingSpace < space) {
			pushToLeftParticipantBox(space - existingSpace, p2, true);
		}

	}

	public final Pushable getFirstBorder() {
		return firstBorder;
	}

	public final Pushable getLastborder() {
		return lastborder;
	}

}
