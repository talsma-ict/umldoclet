/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.project3;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskImpl implements Task, LoadPlanable {

	private final TaskCode code;
	private final Solver3 solver;
	private final Map<Resource, Integer> resources2 = new LinkedHashMap<Resource, Integer>();
	private final LoadPlanable defaultPlan;
	private boolean diamond;

	public TaskImpl(TaskCode code, LoadPlanable defaultPlan) {
		this.code = code;
		this.defaultPlan = defaultPlan;
		this.solver = new Solver3(this);
		setStart(new InstantDay(0));
		setLoad(LoadInDays.inDay(1));
	}

	public int getLoadAt(Instant instant) {
		LoadPlanable result = defaultPlan;
		if (resources2.size() > 0) {
			result = PlanUtils.multiply(defaultPlan, getRessourcePlan());
		}
		return result.getLoadAt(instant);
		// return PlanUtils.minOf(getLoad(), plan1).getLoadAt(instant);
	}

	public int loadForResource(Resource res, Instant instant) {
		if (resources2.keySet().contains(res) && instant.compareTo(getStart()) >= 0 && instant.compareTo(getEnd()) <= 0) {
			if (res.isClosedAt(instant)) {
				return 0;
			}
			// int size = 0;
			return resources2.get(res);
			// for (Resource r : resources) {
			// if (r.getLoadAt(i) > 0) {
			// size++;
			// }
			// }
			// return getLoadAt(instant) / size;
		}
		return 0;
	}

	private LoadPlanable getRessourcePlan() {
		if (resources2.size() == 0) {
			throw new IllegalStateException();
		}
		return new LoadPlanable() {

			public int getLoadAt(Instant instant) {
				int result = 0;
				for (Map.Entry<Resource, Integer> ent : resources2.entrySet()) {
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
		if (resources2.size() > 0) {
			final StringBuilder result = new StringBuilder(code.getSimpleDisplay());
			result.append(" ");
			for (Iterator<Map.Entry<Resource, Integer>> it = resources2.entrySet().iterator(); it.hasNext();) {
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

	public Instant getStart() {
		Instant result = (Instant) solver.getData(TaskAttribute.START);
		while (getLoadAt(result) == 0) {
			result = result.increment();
		}
		return result;
	}

	public Instant getEnd() {
		return (Instant) solver.getData(TaskAttribute.END);
	}

	public Load getLoad() {
		return (Load) solver.getData(TaskAttribute.LOAD);
	}

	public void setLoad(Load load) {
		solver.setData(TaskAttribute.LOAD, load);
	}

	public void setStart(Instant start) {
		solver.setData(TaskAttribute.START, start);
	}

	public void setEnd(Instant end) {
		solver.setData(TaskAttribute.END, end);
	}

	private TaskDraw taskDraw;
	private ComplementColors colors;

	public void setTaskDraw(TaskDraw taskDraw) {
		taskDraw.setColors(colors);
		this.taskDraw = taskDraw;
	}

	public TaskDraw getTaskDraw() {
		return taskDraw;
	}

	public void setColors(ComplementColors colors) {
		this.colors = colors;
	}

	public void addResource(Resource resource, int percentage) {
		this.resources2.put(resource, percentage);
	}

	public void setDiamond(boolean diamond) {
		this.diamond = diamond;
	}

	public boolean isDiamond() {
		return this.diamond;
	}

}
