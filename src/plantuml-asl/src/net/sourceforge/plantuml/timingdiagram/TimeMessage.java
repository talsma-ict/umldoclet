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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.WithLinkType;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TimeMessage extends WithLinkType {

	private final TickInPlayer tickInPlayer1;
	private final TickInPlayer tickInPlayer2;
	private final Display label;
	private final ISkinParam skinParam;
	private final StyleBuilder styleBuilder;

	public TimeMessage(TickInPlayer tickInPlayer1, TickInPlayer tickInPlayer2, String label, ISkinParam skinParam) {
		this.skinParam = skinParam;
		this.styleBuilder = skinParam.getCurrentStyleBuilder();
		this.tickInPlayer1 = tickInPlayer1;
		this.tickInPlayer2 = tickInPlayer2;
		this.label = Display.getWithNewlines(label);
		this.setSpecificColor(getColor());
		this.type = new LinkType(LinkDecor.NONE, LinkDecor.NONE);
	}

	@Override
	public UStroke getUStroke() {
		if (styleBuilder == null) {
			return new UStroke(1.5);
		}
		return getStyle().getStroke();
	}

	private HColor getColor() {
//		return HColorUtils.BLUE;
		if (styleBuilder == null) {
			return HColorUtils.MY_RED;
		}
		return getStyle().value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(styleBuilder);
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.arrow);
	}

	public final Player getPlayer1() {
		return tickInPlayer1.getPlayer();
	}

	public final Player getPlayer2() {
		return tickInPlayer2.getPlayer();
	}

	public final TimeTick getTick1() {
		return tickInPlayer1.getTick();
	}

	public final TimeTick getTick2() {
		return tickInPlayer2.getTick();
	}

	public final Display getLabel() {
		return label;
	}

	@Override
	public void goNorank() {
		// Nothing to do
	}

}
