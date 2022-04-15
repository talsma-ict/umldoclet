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
package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.PaddingParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.teoz.LivingSpace;
import net.sourceforge.plantuml.sequencediagram.teoz.TileArguments;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.WithStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Doll implements WithStyle {

	final private List<Participant> participants = new ArrayList<>();
	final private ParticipantEnglober englober;
	final private StyleBuilder styleBuilder;
	final private TileArguments tileArguments;

	public static Doll createPuma(ParticipantEnglober englober, Participant first, ISkinParam skinParam, Rose skin,
			StringBounder stringBounder, StyleBuilder styleBuilder) {
		return new Doll(englober, convertFunctionToBeRemoved(skinParam, skin, stringBounder), styleBuilder, first);
	}

	public static Doll createTeoz(ParticipantEnglober englober, TileArguments tileArguments) {
		return new Doll(englober, tileArguments, tileArguments.getSkinParam().getCurrentStyleBuilder(), null);
	}

	private static TileArguments convertFunctionToBeRemoved(ISkinParam skinParam, Rose skin,
			StringBounder stringBounder) {
		return new TileArguments(stringBounder, null, skin, skinParam, null);
	}

	private Doll(ParticipantEnglober englober, TileArguments tileArguments, StyleBuilder styleBuilder,
			Participant first) {
		this.englober = Objects.requireNonNull(englober);
		this.styleBuilder = styleBuilder;
		this.tileArguments = Objects.requireNonNull(tileArguments);

		if (first != null) {
			this.participants.add(first);
		}

	}

	final public StyleSignatureBasic getStyleSignature() {
		return ComponentType.ENGLOBER.getStyleSignature();
	}

	final public Style[] getUsedStyles() {
		Style tmp = getStyleSignature().withTOBECHANGED(englober.getStereotype()).getMergedStyle(styleBuilder);
		final HColor backColor = englober.getBoxColor();
		if (tmp != null)
			tmp = tmp.eventuallyOverride(PName.BackGroundColor, backColor);

		return new Style[] { tmp };
	}

	final public ParticipantEnglober getParticipantEnglober() {
		return englober;
	}

	private Component getComponent() {
		final ParticipantEnglober englober = getParticipantEnglober();
		final ISkinParam s = englober.getBoxColor() == null ? tileArguments.getSkinParam()
				: new SkinParamBackcolored(tileArguments.getSkinParam(), englober.getBoxColor());
		return tileArguments.getSkin().createComponent(getUsedStyles(), ComponentType.ENGLOBER, null, s,
				englober.getTitle());
	}

	public double getTitlePreferredHeight() {
		final Component comp = tileArguments.getSkin().createComponent(getUsedStyles(), ComponentType.ENGLOBER, null,
				tileArguments.getSkinParam(), getParticipantEnglober().getTitle());
		return comp.getPreferredHeight(tileArguments.getStringBounder());
	}

	public final Participant getFirst2TOBEPRIVATE() {
		return participants.get(0);
	}

	public final Participant getLast2TOBEPRIVATE() {
		return participants.get(participants.size() - 1);
	}

	private Real getPosA(StringBounder stringBounder) {
		return getFirstLivingSpace().getPosA(stringBounder);
	}

	private Real getPosB(StringBounder stringBounder) {
		return getFirstLivingSpace().getPosB(stringBounder);
	}

	private Real getPosD(StringBounder stringBounder) {
		return getLastLivingSpace().getPosD(stringBounder);
	}

	private Real getPosE(StringBounder stringBounder) {
		return getLastLivingSpace().getPosE(stringBounder);
	}

	private Real getPosAA(StringBounder stringBounder) {
		final LivingSpace previous = tileArguments.getLivingSpaces().previous(getFirstLivingSpace());
		if (previous == null) {
			return tileArguments.getOrigin();
		}
		return previous.getPosD(stringBounder);
	}

	private LivingSpace getFirstLivingSpace() {
		return tileArguments.getLivingSpace(getFirst2TOBEPRIVATE());
	}

	private LivingSpace getLastLivingSpace() {
		return tileArguments.getLivingSpace(getLast2TOBEPRIVATE());
	}

	public boolean contains(Participant p) {
		return participants.contains(p);
	}

	public void addParticipant(Participant p) {
		participants.add(Objects.requireNonNull(p));
	}

	@Override
	public String toString() {
		return "Doll:" + englober.getTitle().toString() + " " + participants;
	}

	private double getTitleWidth() {
		return getComponent().getPreferredWidth(tileArguments.getStringBounder());
	}

	public void drawMe(UGraphic ug, double height, Context2D context, Doll group) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double x1 = getPosA(stringBounder).getCurrentValue() - 4;
		final double x2 = getPosE(stringBounder).getCurrentValue() + 4;

		if (group != null) {
			final double titlePreferredHeight = group.getTitlePreferredHeight();
			ug = ug.apply(UTranslate.dy(titlePreferredHeight));
			height -= titlePreferredHeight;
		}

		final Dimension2DDouble dim = new Dimension2DDouble(x2 - x1, height);
		getComponent().drawU(ug.apply(new UTranslate(x1, 1)), new Area(dim), context);
	}

	public void addInternalConstraints(StringBounder stringBounder) {
		final double titleWidth = getTitleWidth();
		final double x1 = getPosB(stringBounder).getCurrentValue();
		final double x2 = getPosD(stringBounder).getCurrentValue();
		final double actualWidth = x2 - x1;
		final double marginX = (titleWidth + 10 - actualWidth) / 2;
		if (marginX > 0) {
			getFirstLivingSpace().ensureMarginBefore(marginX);
			getLastLivingSpace().ensureMarginAfter(marginX);
		}
		getPosA(stringBounder).ensureBiggerThan(getPosAA(stringBounder).addFixed(10 + padding()));

	}

	public void addConstraintAfter(StringBounder stringBounder) {
		final LivingSpace next = tileArguments.getLivingSpaces().next(getLastLivingSpace());
		if (next == null)
			return;

		next.getPosA(stringBounder).ensureBiggerThan(getPosE(stringBounder).addFixed(20 + 2 * padding()));
	}

	private double padding() {
		return tileArguments.getSkinParam().getPadding(PaddingParam.BOX);
	}

	public Real getMinX(StringBounder stringBounder) {
		return getPosA(stringBounder);
	}

	public Real getMaxX(StringBounder stringBounder) {
		return getPosE(stringBounder).addFixed(10);
	}

}
