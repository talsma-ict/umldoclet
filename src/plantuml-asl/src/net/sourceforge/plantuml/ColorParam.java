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
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

@HaxeIgnored
public enum ColorParam {
	background(HColorUtils.WHITE, true, ColorType.BACK), 
	hyperlink(HColorUtils.BLUE),

	activityBackground(HColorUtils.MY_YELLOW, true, ColorType.BACK), 
	activityBorder(HColorUtils.MY_RED, ColorType.LINE),

	classBackground(HColorUtils.MY_YELLOW, true, ColorType.BACK),

	classBorder(HColorUtils.MY_RED, ColorType.LINE),

	arrowHead(HColorUtils.MY_RED, null),

	stateBorder(HColorUtils.MY_RED, ColorType.LINE),

	noteBackground(HColorUtils.COL_FBFB77, true, ColorType.BACK), 
	noteBorder(HColorUtils.MY_RED, ColorType.LINE),

	diagramBorder(null, ColorType.LINE),

	actorBackground(HColorUtils.MY_YELLOW, true, ColorType.BACK), 
	actorBorder(HColorUtils.MY_RED, ColorType.LINE),
	sequenceGroupBodyBackground(HColorUtils.RED, true, ColorType.BACK),
	sequenceReferenceHeaderBackground(HColorUtils.COL_EEEEEE, true, ColorType.BACK),
	sequenceReferenceBackground(HColorUtils.WHITE, true, ColorType.BACK),
	sequenceLifeLineBorder(HColorUtils.MY_RED, ColorType.LINE),
	sequenceNewpageSeparator(HColorUtils.BLACK, ColorType.LINE), 
	sequenceBoxBorder(HColorUtils.MY_RED, ColorType.LINE),

	iconPrivate(HColorUtils.COL_C82930), 
	iconPrivateBackground(HColorUtils.COL_F24D5C),
	iconPackage(HColorUtils.COL_1963A0), 
	iconPackageBackground(HColorUtils.COL_4177AF),
	iconProtected(HColorUtils.COL_B38D22), 
	iconProtectedBackground(HColorUtils.COL_FFFF44),
	iconPublic(HColorUtils.COL_038048), 
	iconPublicBackground(HColorUtils.COL_84BE84),
	iconIEMandatory(HColorUtils.BLACK),

	arrowLollipop(HColorUtils.WHITE),

	machineBackground(HColorUtils.WHITE), 
	machineBorder(HColorUtils.BLACK, ColorType.LINE),
	requirementBackground(HColorUtils.WHITE), 
	requirementBorder(HColorUtils.BLACK, ColorType.LINE),
	designedBackground(HColorUtils.WHITE), 
	designedBorder(HColorUtils.BLACK, ColorType.LINE),
	domainBackground(HColorUtils.WHITE), 
	domainBorder(HColorUtils.BLACK, ColorType.LINE),
	lexicalBackground(HColorUtils.WHITE), 
	lexicalBorder(HColorUtils.BLACK, ColorType.LINE),
	biddableBackground(HColorUtils.WHITE), 
	biddableBorder(HColorUtils.BLACK, ColorType.LINE);

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
