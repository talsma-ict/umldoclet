/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.sequencediagram.graphic.GroupingGraphicalElementElse;
import net.sourceforge.plantuml.sequencediagram.graphic.LivingParticipantBox;
import net.sourceforge.plantuml.sequencediagram.graphic.MessageExoArrow;
import net.sourceforge.plantuml.sequencediagram.graphic.ParticipantBox;

public class InGroupableList implements InGroupable {

	private static final int MARGIN5 = 5;
	public static final int MARGIN10 = 10;

	private final Grouping grouping;
	private final Set<InGroupable> inGroupables = new HashSet<>();

	private double minWidth;

	public List<InGroupableList> getInnerList() {
		final List<InGroupableList> result = new ArrayList<>();
		for (InGroupable i : inGroupables)
			if (i instanceof InGroupableList)
				result.add((InGroupableList) i);

		return result;
	}

	private final ParticipantBox veryfirst;

	public InGroupableList(ParticipantBox veryfirst, Grouping grouping, double startingY) {
		this.grouping = grouping;
		this.veryfirst = veryfirst;
	}

	public void addInGroupable(InGroupable in) {
		// Thread.dumpStack();
		this.inGroupables.add(in);
		cacheMin = null;
		cacheMax = null;
	}

	public boolean isEmpty() {
		return inGroupables.isEmpty();
	}

	@Override
	public String toString() {
		return "GS " + grouping + " " + inGroupables.toString();
	}

	public String toString(StringBounder stringBounder) {
		final StringBuilder sb = new StringBuilder("GS " + grouping + " ");
		for (InGroupable in : inGroupables) {
			sb.append(in.toString(stringBounder));
			sb.append(' ');
		}
		return sb.toString();
	}

	private InGroupable getMinSlow(StringBounder stringBounder) {
		InGroupable result = null;
		for (InGroupable in : inGroupables) {
			if (in instanceof GroupingGraphicalElementElse)
				continue;

			if (result == null || in.getMinX(stringBounder) < result.getMinX(stringBounder))
				result = in;

		}
		return result;
	}

	private InGroupable getMaxSlow(StringBounder stringBounder) {
		InGroupable result = null;
		for (InGroupable in : inGroupables)
			if (result == null || in.getMaxX(stringBounder) > result.getMaxX(stringBounder))
				result = in;

		return result;
	}

	private InGroupable cacheMin = null;
	private InGroupable cacheMax = null;

	private InGroupable getMin(StringBounder stringBounder) {
		if (cacheMin == null)
			cacheMin = getMinSlow(stringBounder);

		// Since // MODIF42 // the assert does not work...
		// System.err.println("cacheMin1="+cacheMin+"
		// cacheMin2="+getMinSlow(stringBounder));
		// assert cacheMin == getMinSlow(stringBounder);
		return cacheMin;
	}

	private InGroupable getMax(StringBounder stringBounder) {
		if (cacheMax == null)
			cacheMax = getMaxSlow(stringBounder);

		assert cacheMax == getMaxSlow(stringBounder);
		return cacheMax;
	}

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	public ParticipantBox getFirstParticipantBox() {
		ParticipantBox first = null;
		for (InGroupable in : inGroupables)
			if (in instanceof LivingParticipantBox) {
				final ParticipantBox participantBox = ((LivingParticipantBox) in).getParticipantBox();
				if (first == null || participantBox.getStartingX() < first.getStartingX())
					first = participantBox;

			}

		return first;
	}

	public ParticipantBox getLastParticipantBox() {
		ParticipantBox last = null;
		for (InGroupable in : inGroupables)
			if (in instanceof LivingParticipantBox) {
				final ParticipantBox participantBox = ((LivingParticipantBox) in).getParticipantBox();
				if (last == null || participantBox.getStartingX() > last.getStartingX())
					last = participantBox;
			}
		return last;
	}

	public double getMinX(StringBounder stringBounder) {
		final InGroupable min = getMin(stringBounder);
		if (min == null)
			return MARGIN10 + MARGIN5 + (veryfirst == null ? 0 : veryfirst.getStartingX());

		double m = min.getMinX(stringBounder);
		if (min instanceof MessageExoArrow && (((MessageExoArrow) min).getType() == MessageExoType.FROM_LEFT
				|| ((MessageExoArrow) min).getType() == MessageExoType.TO_LEFT))
			m += 3;
		else if (min instanceof InGroupableList)
			m -= MARGIN10;
		else
			m -= MARGIN5;

		return m;
	}

	public double getMaxX(StringBounder stringBounder) {
		final double min = getMinX(stringBounder);
		final double max = getMaxXInternal(stringBounder);
		assert max - min >= 0;
		if (max - min < minWidth)
			return min + minWidth + hack2;

		return max + hack2;
	}

	private final double getMaxXInternal(StringBounder stringBounder) {
		final InGroupable max = getMax(stringBounder);
		if (max == null)
			return MARGIN10 + MARGIN5 + minWidth;

		double m = max.getMaxX(stringBounder);
		if (max instanceof MessageExoArrow && (((MessageExoArrow) max).getType() == MessageExoType.FROM_RIGHT
				|| ((MessageExoArrow) max).getType() == MessageExoType.TO_RIGHT))
			m -= 3;
		else if (max instanceof InGroupableList)
			m += MARGIN10;
		else
			m += MARGIN5;

		return m;
	}

	private double hack2;

	public void changeHack2(double hack2) {
		this.hack2 += hack2;
	}

	public double getHack2() {
		return hack2;
	}

}
