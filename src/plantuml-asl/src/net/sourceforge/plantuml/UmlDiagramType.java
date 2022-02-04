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

import net.sourceforge.plantuml.style.SName;

public enum UmlDiagramType {
	SEQUENCE, STATE, CLASS, OBJECT, ACTIVITY, DESCRIPTION, COMPOSITE, FLOW, TIMING, BPM, NWDIAG, MINDMAP, WBS, WIRE,
	HELP, GANTT, SALT, JSON, GIT, BOARD, YAML, HCL;

	public SName getStyleName() {
		if (this == SEQUENCE) {
			return SName.sequenceDiagram;
		}
		if (this == STATE) {
			return SName.stateDiagram;
		}
		if (this == CLASS) {
			return SName.classDiagram;
		}
		if (this == OBJECT) {
			return SName.objectDiagram;
		}
		if (this == ACTIVITY) {
			return SName.activityDiagram;
		}
		if (this == DESCRIPTION) {
			return SName.componentDiagram;
		}
		if (this == COMPOSITE) {
			return SName.componentDiagram;
		}
		if (this == MINDMAP) {
			return SName.mindmapDiagram;
		}
		if (this == WBS) {
			return SName.wbsDiagram;
		}
		if (this == GANTT) {
			return SName.ganttDiagram;
		}
		if (this == SALT) {
			return SName.saltDiagram;
		}
		if (this == YAML) {
			return SName.yamlDiagram;
		}
		if (this == HCL) {
			return SName.yamlDiagram;
		}
		if (this == JSON) {
			return SName.jsonDiagram;
		}
		if (this == TIMING) {
			return SName.timingDiagram;
		}
		if (this == NWDIAG) {
			return SName.nwdiagDiagram;
		}
		return SName.activityDiagram;
	}
}
