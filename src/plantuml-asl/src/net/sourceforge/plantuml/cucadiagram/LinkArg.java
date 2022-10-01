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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.skin.VisibilityModifier;

public class LinkArg {

	private final Display label;
	private final String qualifier1;
	private final String qualifier2;
	private final String labeldistance;
	private final String labelangle;

	private final String kal1;
	private final String kal2;

	private VisibilityModifier visibilityModifier;
	private int length;

	public static LinkArg build(final Display label, int length) {
		return build(label, length, true);
	}

	public static LinkArg noDisplay(int length) {
		return build(Display.NULL, length, true);
	}

	public static LinkArg build(final Display label, int length, boolean manageVisibilityModifier) {
		VisibilityModifier visibilityModifier = null;
		final Display newLabel;
		if (Display.isNull(label)) {
			newLabel = Display.NULL;
		} else {
			newLabel = label.manageGuillemet(manageVisibilityModifier);
			if (manageVisibilityModifier && VisibilityModifier.isVisibilityCharacter(label.get(0)))
				visibilityModifier = VisibilityModifier.getVisibilityModifier(label.get(0), false);
		}
		return new LinkArg(newLabel, length, null, null, null, null, visibilityModifier, null, null);
	}

	public LinkArg withQualifier(String qualifier1, String qualifier2) {
		return new LinkArg(label, length, qualifier1, qualifier2, labeldistance, labelangle, visibilityModifier, kal1,
				kal2);
	}

	public LinkArg withKal(String kal1, String kal2) {
		return new LinkArg(label, length, qualifier1, qualifier2, labeldistance, labelangle, visibilityModifier, kal1,
				kal2);
	}

	public LinkArg withDistanceAngle(String labeldistance, String labelangle) {
		return new LinkArg(label, length, qualifier1, qualifier2, labeldistance, labelangle, visibilityModifier, kal1,
				kal2);
	}

	private LinkArg(Display label, int length, String qualifier1, String qualifier2, String labeldistance,
			String labelangle, VisibilityModifier visibilityModifier, String kal1, String kal2) {

		this.label = label;
		this.visibilityModifier = visibilityModifier;
		this.length = length;
		this.qualifier1 = qualifier1;
		this.qualifier2 = qualifier2;
		this.labeldistance = labeldistance;
		this.labelangle = labelangle;
		this.kal1 = kal1;
		this.kal2 = kal2;
	}

	public LinkArg getInv() {
		return new LinkArg(label, length, qualifier2, qualifier1, labeldistance, labelangle, visibilityModifier, kal1,
				kal2);
	}

	public final Display getLabel() {
		return label;
	}

	public final int getLength() {
		return length;
	}

	public final String getQualifier1() {
		return qualifier1;
	}

	public final String getQualifier2() {
		return qualifier2;
	}

	public final String getLabeldistance() {
		return labeldistance;
	}

	public final String getLabelangle() {
		return labelangle;
	}

	public final VisibilityModifier getVisibilityModifier() {
		return visibilityModifier;
	}

	public final void setVisibilityModifier(VisibilityModifier visibilityModifier) {
		this.visibilityModifier = visibilityModifier;
	}

	public final void setLength(int length) {
		this.length = length;
	}

	public final String getKal1() {
		return kal1;
	}

	public final String getKal2() {
		return kal2;
	}

}
