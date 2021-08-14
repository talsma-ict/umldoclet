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
package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.ugraphic.color.HColor;

final public class GroupingLeaf extends Grouping implements EventWithDeactivate {

	private final GroupingStart start;
	private final HColor backColorGeneral;

	public GroupingLeaf(String title, String comment, GroupingType type, HColor backColorGeneral,
			HColor backColorElement, GroupingStart start, StyleBuilder styleBuilder) {
		super(title, comment, type, backColorElement, styleBuilder);
		this.backColorGeneral = backColorGeneral;
		this.start = Objects.requireNonNull(start);
		start.addChildren(this);
	}

	public Grouping getJustAfter() {
		final int idx = start.getChildren().indexOf(this);
		if (idx == -1) {
			throw new IllegalStateException();
		}
		if (idx + 1 >= start.getChildren().size()) {
			return null;
		}
		return start.getChildren().get(idx + 1);
	}

	public GroupingStart getGroupingStart() {
		return start;
	}

	@Override
	public int getLevel() {
		return start.getLevel();
	}

	@Override
	public final HColor getBackColorGeneral() {
		if (backColorGeneral == null) {
			return start.getBackColorGeneral();
		}
		return backColorGeneral;
	}

	public boolean dealWith(Participant someone) {
		return false;
	}

	public Url getUrl() {
		return null;
	}

	public boolean hasUrl() {
		return false;
	}

	@Override
	public boolean isParallel() {
		return start.isParallel();
	}

	private double posYendLevel;

	public void setPosYendLevel(double posYendLevel) {
		this.posYendLevel = posYendLevel;
	}

	public double getPosYendLevel() {
		return posYendLevel;
	}

	public boolean addLifeEvent(LifeEvent lifeEvent) {
		return true;
	}

	private List<Note> noteOnMessages = new ArrayList<>();

	public final void setNote(Note note) {
		if (note.getPosition() != NotePosition.LEFT && note.getPosition() != NotePosition.RIGHT) {
			throw new IllegalArgumentException();
		}
		this.noteOnMessages.add(note);
	}

	public final List<Note> getNoteOnMessages() {
		return noteOnMessages;
	}

}
