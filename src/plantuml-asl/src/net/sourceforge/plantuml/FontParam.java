/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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

import java.awt.Font;

import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignature;

interface FontParamConstant {
	String FAMILY = "SansSerif";
	String COLOR = "black";
}

public enum FontParam {
	TIMING(12, Font.PLAIN), //
	ACTIVITY(12, Font.PLAIN), //
	ACTIVITY_DIAMOND(11, Font.PLAIN), //
	// ACTIVITY_ARROW(11, Font.PLAIN), //
	// GENERIC_ARROW(13, Font.PLAIN), //
	ARROW(13, Font.PLAIN), //
	CIRCLED_CHARACTER(17, Font.BOLD, FontParamConstant.COLOR, "Monospaced"), //
	OBJECT_ATTRIBUTE(10, Font.PLAIN), //
	OBJECT(12, Font.PLAIN), //
	OBJECT_STEREOTYPE(12, Font.ITALIC), //
	CLASS_ATTRIBUTE(10, Font.PLAIN), //
	CLASS(12, Font.PLAIN), //
	CLASS_STEREOTYPE(12, Font.ITALIC), //
	COMPONENT(14, Font.PLAIN), //
	INTERFACE(14, Font.PLAIN), //
	INTERFACE_STEREOTYPE(14, Font.ITALIC), //
	COMPONENT_STEREOTYPE(14, Font.ITALIC), //
	NOTE(13, Font.PLAIN), //
	PACKAGE(14, Font.PLAIN), //
	PACKAGE_STEREOTYPE(14, Font.ITALIC), //
	ACTOR(14, Font.PLAIN), //
	ARTIFACT(14, Font.PLAIN), //
	CLOUD(14, Font.PLAIN), //
	FOLDER(14, Font.PLAIN), //
	FILE(14, Font.PLAIN), //
	FRAME(14, Font.PLAIN), //
	STORAGE(14, Font.PLAIN), //
	BOUNDARY(14, Font.PLAIN), //
	CONTROL(14, Font.PLAIN), //
	ENTITY(14, Font.PLAIN), //
	AGENT(14, Font.PLAIN), //
	RECTANGLE(14, Font.PLAIN), //
	LABEL(14, Font.PLAIN), //
	HEXAGON(14, Font.PLAIN), //
	PERSON(14, Font.PLAIN), //
	ARCHIMATE(14, Font.PLAIN), //
	CARD(14, Font.PLAIN), //
	NODE(14, Font.PLAIN), //
	DATABASE(14, Font.PLAIN), //
	QUEUE(14, Font.PLAIN), //
	STACK(14, Font.PLAIN), //
	// SEQUENCE_ARROW(13, Font.PLAIN), //
	SEQUENCE_BOX(13, Font.BOLD), //
	SEQUENCE_DIVIDER(13, Font.BOLD), //
	SEQUENCE_REFERENCE(13, Font.PLAIN), //
	SEQUENCE_DELAY(11, Font.PLAIN), //
	SEQUENCE_GROUP(11, Font.BOLD), //
	SEQUENCE_GROUP_HEADER(13, Font.BOLD), //
	PARTICIPANT(14, Font.PLAIN), //
	PARTICIPANT_STEREOTYPE(14, Font.ITALIC), //
	STATE(14, Font.PLAIN), //
	STATE_ATTRIBUTE(12, Font.PLAIN), //
	LEGEND(14, Font.PLAIN), //
	TITLE(18, Font.PLAIN), //
	// SEQUENCE_TITLE(14, Font.BOLD), //
	CAPTION(14, Font.PLAIN), //
	SWIMLANE_TITLE(18, Font.PLAIN), //
	FOOTER(10, Font.PLAIN, "#888888", FontParamConstant.FAMILY), //
	HEADER(10, Font.PLAIN, "#888888", FontParamConstant.FAMILY), //
	USECASE(14, Font.PLAIN), //
	USECASE_STEREOTYPE(14, Font.ITALIC), //
	ARTIFACT_STEREOTYPE(14, Font.ITALIC), //
	CLOUD_STEREOTYPE(14, Font.ITALIC), //
	STORAGE_STEREOTYPE(14, Font.ITALIC), //
	BOUNDARY_STEREOTYPE(14, Font.ITALIC), //
	CONTROL_STEREOTYPE(14, Font.ITALIC), //
	ENTITY_STEREOTYPE(14, Font.ITALIC), //
	AGENT_STEREOTYPE(14, Font.ITALIC), //
	RECTANGLE_STEREOTYPE(14, Font.ITALIC), //
	LABEL_STEREOTYPE(14, Font.ITALIC), //
	PERSON_STEREOTYPE(14, Font.ITALIC), //
	HEXAGON_STEREOTYPE(14, Font.ITALIC), //
	ARCHIMATE_STEREOTYPE(14, Font.ITALIC), //
	CARD_STEREOTYPE(14, Font.ITALIC), //
	NODE_STEREOTYPE(14, Font.ITALIC), //
	FOLDER_STEREOTYPE(14, Font.ITALIC), //
	FILE_STEREOTYPE(14, Font.ITALIC), //
	FRAME_STEREOTYPE(14, Font.ITALIC), //
	DATABASE_STEREOTYPE(14, Font.ITALIC), //
	QUEUE_STEREOTYPE(14, Font.ITALIC), //
	STACK_STEREOTYPE(14, Font.ITALIC), //
	ACTOR_STEREOTYPE(14, Font.ITALIC), //
	SEQUENCE_STEREOTYPE(14, Font.ITALIC), //
	PARTITION(14, Font.PLAIN), DESIGNED_DOMAIN(12, Font.PLAIN), //
	DESIGNED_DOMAIN_STEREOTYPE(12, Font.ITALIC), //
	DOMAIN(12, Font.PLAIN), //
	DOMAIN_STEREOTYPE(12, Font.ITALIC), //
	MACHINE(12, Font.PLAIN), //
	MACHINE_STEREOTYPE(12, Font.ITALIC), //
	REQUIREMENT(12, Font.PLAIN), //
	REQUIREMENT_STEREOTYPE(12, Font.ITALIC); //

	private final int defaultSize;
	private final int fontStyle;
	private final String defaultColor;
	private final String defaultFamily;

	private FontParam(int defaultSize, int fontStyle, String defaultColor, String defaultFamily) {
		this.defaultSize = defaultSize;
		this.fontStyle = fontStyle;
		this.defaultColor = defaultColor;
		this.defaultFamily = defaultFamily;
	}

	private FontParam(int defaultSize, int fontStyle) {
		this(defaultSize, fontStyle, FontParamConstant.COLOR, FontParamConstant.FAMILY);
	}

	public final int getDefaultSize(ISkinParam skinParam) {
		if (this == ARROW && skinParam.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			return 11;
		}
		if (this == CLASS_ATTRIBUTE) {
			return 11;
		}
		return defaultSize;
	}

	public final int getDefaultFontStyle(ISkinParam skinParam, boolean inPackageTitle) {
		if (this == STATE) {
			return fontStyle;
		}
		if (inPackageTitle || this == PACKAGE) {
			return Font.BOLD;
		}
		return fontStyle;
	}

	public final String getDefaultColor() {
		return defaultColor;
	}

	public String getDefaultFamily() {
		return defaultFamily;
	}

	public FontConfiguration getFontConfiguration(ISkinParam skinParam) {
		return new FontConfiguration(skinParam, this, null);
	}

	public StyleSignature getStyleDefinition(SName diagramType) {
		if (this == FOOTER) {
			return StyleSignature.of(SName.root, SName.document, SName.footer);
		}
		if (this == HEADER) {
			return StyleSignature.of(SName.root, SName.document, SName.header);
		}
		if (this == TITLE) {
			return StyleSignature.of(SName.root, SName.document, SName.title);
		}
		if (this == CLASS_ATTRIBUTE) {
			return StyleSignature.of(SName.root, SName.element, SName.classDiagram, SName.class_);
		}
		if (this == RECTANGLE || this == NODE) {
			return StyleSignature.of(SName.root, SName.element, SName.componentDiagram, SName.component);
		}
		return StyleSignature.of(SName.root, SName.element, diagramType, SName.component);
//		System.err.println("Warning " + this);
//		throw new UnsupportedOperationException();
	}

}
