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
package net.sourceforge.plantuml.style;

public enum SName {
	activity, //
	activityBar, //
	activityDiagram, //
	actor, //
	agent, //
	archimate, //
	arrow, //
	artifact, //
	boundary, //
	box, //
	boxless, //
	caption, //
	card, //
	circle, //
	classDiagram, //
	class_, //
	clickable, //
	cloud, //
	closed, //
	collection, //
	collections, //
	component, //
	componentDiagram, //
	constraintArrow, //
	control, //
	database, //
	delay, //
	destroy, //
	diamond, //
	document, //
	element, //
	entity, //
	end, //
	stop, //
	file, //
	folder, //
	footer, //
	frame, //
	ganttDiagram, //
	group, //
	groupHeader, //
	header, //
	hexagon, //
	highlight, //
	interface_, //
	jsonDiagram, //
	gitDiagram, //
	label, //
	leafNode, //
	legend, //
	lifeLine, //
	map, //
	milestone, //
	mindmapDiagram, //
	network, //
	node, //
	note, //
	nwdiagDiagram, //
	objectDiagram, //
	object, //
	package_, //
	participant, //
	partition, //
	person, //
	queue, //
	rectangle, //
	reference, //
	referenceHeader, //
	root, //
	rootNode, //
	saltDiagram, //
	separator, //
	sequenceDiagram, //
	server, //
	stack, //
	stateDiagram, //
	state, //
	stereotype, //
	storage, //
	swimlane, //
	task, //
	timeline, //
	timingDiagram, //
	title, //
	undone, //
	unstarted, //
	usecase, //
	
	visibilityIcon, //
	private_, //
	protected_, //
	public_, //
	IEMandatory, //
	spot, //
	spotAnnotation, //
	spotInterface, //
	spotEnum, //
	spotEntity, //
	spotClass, //
	spotAbstractClass, //
	
	wbsDiagram, //
	yamlDiagram; //

	public static String depth(int level) {
		return "depth(" + level + ")";
	}
}
