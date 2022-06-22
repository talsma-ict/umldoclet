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

import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.MergeStrategy;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.style.WithStyle;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Participant implements SpecificBackcolorable, WithStyle {

	private final String code;
	private Display display;
	private final ParticipantType type;

	private int initialLife = 0;

	private Stereotype stereotype;
	private boolean stereotypePositionTop;
	private final Set<EntityPortion> hiddenPortions;
	private final int order;
	private final StyleBuilder styleBuilder;

	// private Style style;

	public StyleSignatureBasic getStyleSignature() {
		return type.getStyleSignature().addClickable(getUrl());
	}

	public Style[] getUsedStyles() {

		final StyleSignature signature = getStyleSignature().withTOBECHANGED(stereotype);
		Style tmp = signature.getMergedStyle(styleBuilder);
		tmp = tmp.eventuallyOverride(getColors());
		Style stereo = getStyleSignature().forStereotypeItself(stereotype).getMergedStyle(styleBuilder);
		if (tmp != null)
			stereo = tmp.mergeWith(stereo, MergeStrategy.OVERWRITE_EXISTING_VALUE);

		return new Style[] { tmp, stereo };
	}

	public Participant(ParticipantType type, String code, Display display, Set<EntityPortion> hiddenPortions, int order,
			StyleBuilder styleBuilder) {
		this.hiddenPortions = hiddenPortions;
		this.styleBuilder = styleBuilder;
		this.order = order;
		this.code = Objects.requireNonNull(code);
		if (code.length() == 0)
			throw new IllegalArgumentException();

		if (Display.isNull(display) || display.size() == 0)
			throw new IllegalArgumentException();

		this.type = Objects.requireNonNull(type);
		this.display = display;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return getCode();
	}

	public Display getDisplay(boolean underlined) {
		Display result = underlined ? display.underlined() : display;
		if (stereotype != null && hiddenPortions.contains(EntityPortion.STEREOTYPE) == false) {
			if (stereotypePositionTop)
				result = result.addFirst(stereotype);
			else
				result = result.add(stereotype);

		}
		return result;
	}

	public ParticipantType getType() {
		return type;
	}

	public final void setStereotype(Stereotype stereotype, boolean stereotypePositionTop) {
		if (this.stereotype != null)
			throw new IllegalStateException();

		this.stereotype = Objects.requireNonNull(stereotype);
		this.stereotypePositionTop = stereotypePositionTop;
	}

	public final int getInitialLife() {
		return initialLife;
	}

	private SymbolContext liveBackcolors;

	public final void incInitialLife(SymbolContext colors) {
		initialLife++;
		this.liveBackcolors = colors;
	}

	public SymbolContext getLiveSpecificBackColors() {
		return liveBackcolors;
	}

	public Colors getColors() {
		return colors;
	}

	public void setSpecificColorTOBEREMOVED(ColorType type, HColor color) {
		if (color != null)
			this.colors = colors.add(type, color);

	}

	private Colors colors = Colors.empty();

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	private Url url;

	public final Url getUrl() {
		return url;
	}

	public final void setUrl(Url url) {
		this.url = url;
	}

	public final Stereotype getStereotype() {
		return stereotype;
	}

	public int getOrder() {
		return order;
	}

}
