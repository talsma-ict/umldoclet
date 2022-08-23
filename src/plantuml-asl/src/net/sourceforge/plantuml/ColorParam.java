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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.annotation.HaxeIgnored;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

@HaxeIgnored
public enum ColorParam {
	background(HColors.WHITE, true, ColorType.BACK), 
	hyperlink(HColors.BLUE),

	activityBackground(HColors.MY_YELLOW, true, ColorType.BACK), 
	activityBorder(HColors.MY_RED, ColorType.LINE),

	classBackground(HColors.MY_YELLOW, true, ColorType.BACK),

	classBorder(HColors.MY_RED, ColorType.LINE),

	arrowHead(HColors.MY_RED, null),

	stateBorder(HColors.MY_RED, ColorType.LINE),

	noteBackground(HColors.COL_FBFB77, true, ColorType.BACK), 
	noteBorder(HColors.MY_RED, ColorType.LINE),

	diagramBorder(null, ColorType.LINE),

	actorBackground(HColors.MY_YELLOW, true, ColorType.BACK), 
	actorBorder(HColors.MY_RED, ColorType.LINE),
	sequenceGroupBodyBackground(HColors.RED, true, ColorType.BACK),
	sequenceReferenceHeaderBackground(HColors.COL_EEEEEE, true, ColorType.BACK),
	sequenceReferenceBackground(HColors.WHITE, true, ColorType.BACK),
	sequenceLifeLineBorder(HColors.MY_RED, ColorType.LINE),
	sequenceNewpageSeparator(HColors.BLACK, ColorType.LINE), 
	sequenceBoxBorder(HColors.MY_RED, ColorType.LINE),

	iconPrivate(HColors.COL_C82930), 
	iconPrivateBackground(HColors.COL_F24D5C),
	iconPackage(HColors.COL_1963A0), 
	iconPackageBackground(HColors.COL_4177AF),
	iconProtected(HColors.COL_B38D22), 
	iconProtectedBackground(HColors.COL_FFFF44),
	iconPublic(HColors.COL_038048), 
	iconPublicBackground(HColors.COL_84BE84),
	iconIEMandatory(HColors.BLACK),

	arrowLollipop(HColors.WHITE),

	machineBackground(HColors.WHITE), 
	machineBorder(HColors.BLACK, ColorType.LINE),
	requirementBackground(HColors.WHITE), 
	requirementBorder(HColors.BLACK, ColorType.LINE),
	designedBackground(HColors.WHITE), 
	designedBorder(HColors.BLACK, ColorType.LINE),
	domainBackground(HColors.WHITE), 
	domainBorder(HColors.BLACK, ColorType.LINE),
	lexicalBackground(HColors.WHITE), 
	lexicalBorder(HColors.BLACK, ColorType.LINE),
	biddableBackground(HColors.WHITE), 
	biddableBorder(HColors.BLACK, ColorType.LINE);

	private final boolean isBackground;
	private final HColor defaultValue;
	private final ColorType colorType;

	private ColorParam(HColor defaultValue, ColorType colorType) {
		this(defaultValue, false, colorType);
	}

	private ColorParam(HColor defaultValue) {
		this(defaultValue, false, null);
	}

	private ColorParam() {
		this(null, false, null);
	}

	private ColorParam(boolean isBackground) {
		this(null, isBackground, null);
	}

	private ColorParam(HColor defaultValue, boolean isBackground, ColorType colorType) {
		this.isBackground = isBackground;
		this.defaultValue = defaultValue;
		this.colorType = colorType;
		if (colorType == ColorType.BACK && isBackground == false) {
			System.err.println(this);
			throw new IllegalStateException();
		}
	}

	protected boolean isBackground() {
		return isBackground;
	}

	public final HColor getDefaultValue() {
		return defaultValue;
	}

	public ColorType getColorType() {
		return colorType;
	}
}
