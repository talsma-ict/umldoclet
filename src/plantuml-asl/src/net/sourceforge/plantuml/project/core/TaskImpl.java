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
package net.sourceforge.plantuml.project.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.project.Load;
import net.sourceforge.plantuml.project.LoadPlanable;
import net.sourceforge.plantuml.project.PlanUtils;
import net.sourceforge.plantuml.project.Solver;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;

public class TaskImpl extends AbstractTask implements Task, LoadPlanable {

	private final SortedSet<Day> pausedDay = new TreeSet<Day>();
	private final Set<DayOfWeek> pausedDayOfWeek = new HashSet<DayOfWeek>();
	private final Solver solver;
	private final Map<Resource, Integer> resources = new LinkedHashMap<Resource, Integer>();
	private final LoadPlanable defaultPlan;
	private boolean diamond;

	private int completion = 100;
	private Display note;

	private Url url;
	private CenterBorderColor colors;

	public void setUrl(Url url) {
		this.url = url;
	}

	public TaskImpl(TaskCode code, LoadPlanable defaultPlan, Day calendar) {
		super(code);
		this.defaultPlan = defaultPlan;
		this.solver = new Solver(this);
		if (calendar == null) {
			setStart(Day.create(0));
		} else {
			setStart(calendar);
		}
		setLoad(Load.inWinks(1));
	}

	public int getLoadAt(Day instant) {
		if (pausedDay.contains(instant)) {
			return 0;
		}
		if (pausedDayOfWeek(instant)) {
			return 0;
		}

		LoadPlanable result = defaultPlan;
		if (resources.size() > 0) {
			result = PlanUtils.multiply(defaultPlan, getRessourcePlan());
		}
		return result.getLoadAt(instant);
	}

	private boolean pausedDayOfWeek(Day instant) {
		for (DayOfWeek dayOfWeek : pausedDayOfWeek) {
			if (instant.getDayOfWeek() == dayOfWeek) {
				return true;
			}
		}
		return false;
	}

	public int loadForResource(Resource res, Day instant) {
		if (resources.keySet().contains(res) && instant.compareTo(getStart()) >= 0
				&& instant.compareTo(getEnd()) <= 0) {
			if (res.isClosedAt(instant)) {
				return 0;
			}
			return resources.get(res);
		}
		return 0;
	}

	public void addPause(Day pause) {
		this.pausedDay.add(pause);
	}

	public void addPause(DayOfWeek pause) {
		this.pausedDayOfWeek.add(pause);
	}

	private LoadPlanable getRessourcePlan() {
		if (resources.size() == 0) {
			throw new IllegalStateException();
		}
		return new LoadPlanable() {
			public int getLoadAt(Day instant) {
				int result = 0;
				for (Map.Entry<Resource, Integer> ent : resources.entrySet()) {
					final Resource res = ent.getKey();
					if (res.isClosedAt(instant)) {
						continue;
					}
					final int percentage = ent.getValue();
					result += percentage;
				}
				return result;
			}
		};
	}

	public String getPrettyDisplay() {
		if (resources.size() > 0) {
			final StringBuilder result = new StringBuilder(code.getSimpleDisplay());
			result.append(" ");
			for (Iterator<Map.Entry<Resource, Integer>> it = resources.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<Resource, Integer> ent = it.next();
				result.append("{");
				result.append(ent.getKey().getName());
				final int percentage = ent.getValue();
				if (percentage != 100) {
					result.append(":" + percentage + "%");
				}
				result.append("}");
				if (it.hasNext()) {
					result.append(" ");
				}
			}
			return result.toString();
		}
		return code.getSimpleDisplay();
	}

	@Override
	public String toString() {
		return code.toString();
	}

	public String debug() {
		return "" + getStart() + " ---> " + getEnd() + "   [" + getLoad() + "]";
	}

	public TaskCode getCode() {
		return code;
	}

	public Day getStart() {
		Day result = (Day) solver.getData(TaskAttribute.START);
		while (getLoadAt(result) == 0) {
			result = result.increment();
		}
		return result;
	}

	public Day getEnd() {
		return (Day) solver.getData(TaskAttribute.END);
	}

	public Load getLoad() {
		return (Load) solver.getData(TaskAttribute.LOAD);
	}

	public void setLoad(Load load) {
		solver.setData(TaskAttribute.LOAD, load);
	}

	public void setStart(Day start) {
		solver.setData(TaskAttribute.START, start);
	}

	public void setEnd(Day end) {
		solver.setData(TaskAttribute.END, end);
	}

	public void setColors(CenterBorderColor colors) {
		this.colors = colors;
	}

	public void addResource(Resource resource, int percentage) {
		this.resources.put(resource, percentage);
	}

	public void setDiamond(boolean diamond) {
		this.diamond = diamond;
	}

	public boolean isDiamond() {
		return this.diamond;
	}

	public void setCompletion(int completion) {
		this.completion = completion;
	}

	public final Url getUrl() {
		return url;
	}

	public final CenterBorderColor getColors() {
		return colors;
	}

	public final int getCompletion() {
		return completion;
	}

	public final Collection<Day> getAllPaused() {
		final SortedSet<Day> result = new TreeSet<Day>(pausedDay);
		for (DayOfWeek dayOfWeek : pausedDayOfWeek) {
			addAll(result, dayOfWeek);
		}
		return Collections.unmodifiableCollection(result);
	}

	private void addAll(SortedSet<Day> result, DayOfWeek dayOfWeek) {
		final Day start = getStart();
		final Day end = getEnd();
		for (Day current = start; current.compareTo(end) <= 0; current = current.increment()) {
			if (current.getDayOfWeek() == dayOfWeek) {
				result.add(current);
			}
		}
	}

	public void setNote(Display note) {
		this.note = note;
	}

	public Display getNote() {
		return note;
	}

}
