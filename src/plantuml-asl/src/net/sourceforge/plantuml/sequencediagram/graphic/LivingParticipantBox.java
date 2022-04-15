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

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.sequencediagram.InGroupable;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class LivingParticipantBox implements InGroupable {

	private final ParticipantBox participantBox;
	private final LifeLine lifeLine;

	public LivingParticipantBox(ParticipantBox participantBox, LifeLine lifeLine) {
		this.participantBox = participantBox;
		this.lifeLine = lifeLine;
	}

	/**
	 * @deprecated to be deleted
	 */
	@Deprecated
	public ParticipantBox getParticipantBox() {
		return participantBox;
	}

	/**
	 * @deprecated to be deleted
	 */
	@Deprecated
	public LifeLine getLifeLine() {
		return lifeLine;
	}

	public SegmentColored getLiveThicknessAt(StringBounder stringBounder, double y) {
		final double left = lifeLine.getLeftShift(y);
		assert left >= 0;
		final double right = lifeLine.getRightShift(y);
		assert right >= 0 : "right=" + right;
		final double centerX = participantBox.getCenterX(stringBounder);
		// Log.println("AZERTY " + y + " centerX=" + centerX + " left=" + left + " right=" + right);
		// Log.println("Attention, null for segment");
		final SymbolContext colors = lifeLine.getColors();
		return SegmentColored.create(centerX - left, centerX + right, colors, lifeLine.shadowing());
	}

	public void drawLineU22(UGraphic ug, double startingY, double endingY, boolean showTail, double myDelta) {
		if (endingY <= startingY) {
			return;
		}
		final double destroy = lifeLine.getDestroy();
		if (destroy != 0 && destroy > startingY && destroy < endingY) {
			endingY = destroy;
		}
		participantBox.drawLineU22(ug, startingY, endingY, showTail, myDelta);
	}

	public double magicMargin(StringBounder stringBounder) {
		return participantBox.magicMargin(stringBounder);
	}

	public void create(double ypos) {
		lifeLine.setCreate(ypos);
	}

	public double getCreate() {
		return lifeLine.getCreate();
	}

	public double getMaxX(StringBounder stringBounder) {
		return participantBox.getMaxX(stringBounder);
	}

	public double getMinX(StringBounder stringBounder) {
		return participantBox.getStartingX();
	}

	public String toString(StringBounder stringBounder) {
		return toString();
	}

}
