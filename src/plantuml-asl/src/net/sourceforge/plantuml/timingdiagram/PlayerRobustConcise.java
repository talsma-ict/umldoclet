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
package net.sourceforge.plantuml.timingdiagram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.timingdiagram.graphic.Histogram;
import net.sourceforge.plantuml.timingdiagram.graphic.IntricatedPoint;
import net.sourceforge.plantuml.timingdiagram.graphic.PDrawing;
import net.sourceforge.plantuml.timingdiagram.graphic.PlayerFrame;
import net.sourceforge.plantuml.timingdiagram.graphic.Ribbon;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public final class PlayerRobustConcise extends Player {

	private final Set<ChangeState> changes = new TreeSet<>();
	private final List<TimeConstraint> constraints = new ArrayList<>();
	private final List<TimingNote> notes = new ArrayList<>();
	private final Map<String, String> statesLabel = new LinkedHashMap<String, String>();
	private final TimingStyle type;

	private String initialState;
	private PDrawing cached;
	private Colors initialColors;

	public PlayerRobustConcise(TimingStyle type, String full, ISkinParam skinParam, TimingRuler ruler, boolean compact,
			Stereotype stereotype) {
		super(full, skinParam, ruler, compact, stereotype);
		this.type = type;
		this.suggestedHeight = 0;
	}

	@Override
	protected StyleSignature getStyleSignature() {
		if (type == TimingStyle.CONCISE)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.concise).withTOBECHANGED(stereotype);
		if (type == TimingStyle.ROBUST)
			return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.robust).withTOBECHANGED(stereotype);
		throw new IllegalStateException();
	}

	private PDrawing buildPDrawing() {
		final Style style = getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());
		if (type == TimingStyle.CONCISE)
			return new Ribbon(ruler, skinParam, notes, isCompact(), getTitle(), suggestedHeight, style);

		if (type == TimingStyle.ROBUST) {
			final Style style0 = StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram)
					.getMergedStyle(skinParam.getCurrentStyleBuilder());
			return new Histogram(ruler, skinParam, statesLabel.values(), isCompact(), getTitle(), suggestedHeight,
					style, style0);
		}

		throw new IllegalStateException();
	}

	public final TextBlock getPart1(final double fullAvailableWidth, final double specialVSpace) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				if (isCompact() == false)
					new PlayerFrame(getTitle(), skinParam).drawFrameTitle(ug);

				ug = ug.apply(getTranslateForTimeDrawing(ug.getStringBounder())).apply(UTranslate.dy(specialVSpace));
				getTimeDrawing().getPart1(fullAvailableWidth).drawU(ug);
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return getTimeDrawing().getPart1(fullAvailableWidth).calculateDimension(stringBounder);
			}
		};
	}

	public UDrawable getPart2() {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				ug = ug.apply(getTranslateForTimeDrawing(ug.getStringBounder()));
				getTimeDrawing().getPart2().drawU(ug);
			}
		};
	}

	private UTranslate getTranslateForTimeDrawing(StringBounder stringBounder) {
		return UTranslate.dy(getTitleHeight(stringBounder));
	}

	public final double getFullHeight(StringBounder stringBounder) {
		return getTitleHeight(stringBounder) + getZoneHeight(stringBounder);
	}

	private double getTitleHeight(StringBounder stringBounder) {
		if (isCompact())
			return 6;

		return getTitle().calculateDimension(stringBounder).getHeight() + 6;
	}

	private PDrawing getTimeDrawing() {
		if (cached == null)
			cached = computeTimeDrawing();

		return cached;
	}

	private PDrawing computeTimeDrawing() {
		final PDrawing result = buildPDrawing();
		result.setInitialState(initialState, initialColors);
		for (ChangeState change : changes)
			result.addChange(change);

		for (TimeConstraint constraint : constraints)
			result.addConstraint(constraint);

		return result;
	}

	private double getZoneHeight(StringBounder stringBounder) {
		return getTimeDrawing().getFullHeight(stringBounder);
	}

	public final void setState(TimeTick now, String comment, Colors color, String... states) {
		for (int i = 0; i < states.length; i++)
			states[i] = decodeState(states[i]);

		if (now == null) {
			this.initialState = states[0];
			this.initialColors = color;
		} else {
			this.changes.add(new ChangeState(now, comment, color, states));
		}

	}

	private String decodeState(String code) {
		final String label = statesLabel.get(code);
		if (label == null)
			return code;

		return label;
	}

	public final IntricatedPoint getTimeProjection(StringBounder stringBounder, TimeTick tick) {
		final IntricatedPoint point = getTimeDrawing().getTimeProjection(stringBounder, tick);
		if (point == null)
			return null;

		final UTranslate translation = getTranslateForTimeDrawing(stringBounder);
		return point.translated(translation);
	}

	public final void createConstraint(TimeTick tick1, TimeTick tick2, String message) {
		this.constraints.add(new TimeConstraint(tick1, tick2, message, skinParam));
	}

	public final void addNote(TimeTick now, Display note, Position position) {
		Style style = null;
		if (UseStyle.useBetaStyle()) {
			final StyleSignatureBasic signature = StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram,
					SName.note);
			style = signature.getMergedStyle(skinParam.getCurrentStyleBuilder());
		}

		this.notes.add(new TimingNote(now, this, note, position, skinParam, style));
	}

	public final void defineState(String stateCode, String label) {
		statesLabel.put(stateCode, label);
	}

}
