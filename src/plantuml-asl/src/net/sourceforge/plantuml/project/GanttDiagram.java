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
package net.sourceforge.plantuml.project;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.WithSprite;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.InnerStrategy;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.core.Moment;
import net.sourceforge.plantuml.project.core.MomentImpl;
import net.sourceforge.plantuml.project.core.PrintScale;
import net.sourceforge.plantuml.project.core.Resource;
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.core.TaskAttribute;
import net.sourceforge.plantuml.project.core.TaskCode;
import net.sourceforge.plantuml.project.core.TaskImpl;
import net.sourceforge.plantuml.project.core.TaskInstant;
import net.sourceforge.plantuml.project.core.TaskSeparator;
import net.sourceforge.plantuml.project.draw.FingerPrint;
import net.sourceforge.plantuml.project.draw.ResourceDraw;
import net.sourceforge.plantuml.project.draw.TaskDraw;
import net.sourceforge.plantuml.project.draw.TaskDrawDiamond;
import net.sourceforge.plantuml.project.draw.TaskDrawRegular;
import net.sourceforge.plantuml.project.draw.TaskDrawSeparator;
import net.sourceforge.plantuml.project.draw.TimeHeader;
import net.sourceforge.plantuml.project.draw.TimeHeaderDaily;
import net.sourceforge.plantuml.project.draw.TimeHeaderMonthly;
import net.sourceforge.plantuml.project.draw.TimeHeaderSimple;
import net.sourceforge.plantuml.project.draw.TimeHeaderWeekly;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.project.timescale.TimeScale;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class GanttDiagram extends TitledDiagram implements ToTaskDraw, WithSprite {

	private final Map<Task, TaskDraw> draws = new LinkedHashMap<Task, TaskDraw>();
	private final Map<TaskCode, Task> tasks = new LinkedHashMap<TaskCode, Task>();
	private final Map<String, Task> byShortName = new HashMap<String, Task>();
	private final List<GanttConstraint> constraints = new ArrayList<GanttConstraint>();
	private final HColorSet colorSet = HColorSet.instance();

	private final OpenClose openClose = new OpenClose();

	private final Map<String, Resource> resources = new LinkedHashMap<String, Resource>();
	private final Map<Day, HColor> colorDays = new HashMap<Day, HColor>();
	private final Map<DayOfWeek, HColor> colorDaysOfWeek = new HashMap<DayOfWeek, HColor>();
	private final Map<Day, String> nameDays = new HashMap<Day, String>();

	private PrintScale printScale = PrintScale.DAILY;
	private Day today;
	private double totalHeightWithoutFooter;
	private Day min = Day.create(0);
	private Day max;

	private Day printStart;
	private Day printEnd;

	private HColor linksColor = null;

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Project)");
	}

	public GanttDiagram() {
		super(UmlDiagramType.GANTT);
	}

	private int horizontalPages = 1;
	private int verticalPages = 1;

	final public int getHorizontalPages() {
		return horizontalPages;
	}

	final public void setHorizontalPages(int horizontalPages) {
		this.horizontalPages = horizontalPages;
	}

	final public int getVerticalPages() {
		return verticalPages;
	}

	final public void setVerticalPages(int verticalPages) {
		this.verticalPages = verticalPages;
	}

	@Override
	public int getNbImages() {
		return this.horizontalPages * this.verticalPages;
	}

	public final int getDpi(FileFormatOption fileFormatOption) {
		return 96;
	}

	@Override
	protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption, long seed)
			throws IOException {
		final Scale scale = getScale();

		final int margin1;
		final int margin2;
		if (UseStyle.useBetaStyle()) {
			margin1 = SkinParam.zeroMargin(0);
			margin2 = SkinParam.zeroMargin(0);
		} else {
			margin1 = 0;
			margin2 = 0;
		}
		final double dpiFactor = scale == null ? 1 : scale.getScale(100, 100);
		final ClockwiseTopRightBottomLeft margins = ClockwiseTopRightBottomLeft.margin1margin2(margin1, margin2);
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, dpiFactor,
				getMetadata(), "", margins, null);

		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);

		final StringBounder stringBounder = fileFormatOption.getDefaultStringBounder(getSkinParam());
		TextBlock result = getTextBlock(stringBounder);
		result = new AnnotatedWorker(this, getSkinParam(), stringBounder).addAdd(result);
		imageBuilder.setUDrawable(result);

		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, seed, os);
	}

	public void setPrintScale(PrintScale printScale) {
		this.printScale = printScale;
	}

	private boolean isHidden(Task task) {
		if (printStart == null || task instanceof TaskSeparator) {
			return false;
		}
		if (task.getEnd().compareTo(min) < 0) {
			return true;
		}
		if (task.getStart().compareTo(max) > 0) {
			return true;
		}
		return false;
	}

	private TextBlockBackcolored getTextBlock(StringBounder stringBounder) {
		if (printStart == null) {
			initMinMax();
		} else {
			this.min = printStart;
			this.max = printEnd;
		}
		final TimeHeader timeHeader = getTimeHeader();
		initTaskAndResourceDraws(timeHeader.getTimeScale(), timeHeader.getFullHeaderHeight(), stringBounder);
		return new TextBlockBackcolored() {

			public void drawU(UGraphic ug) {
				timeHeader.drawTimeHeader(ug, totalHeightWithoutFooter);
				drawConstraints(ug, timeHeader.getTimeScale());
				drawTasksRect(ug);
				drawTasksTitle(ug);
				drawResources(ug);
				if (showFootbox) {
					timeHeader.drawTimeFooter(ug.apply(UTranslate.dy(totalHeightWithoutFooter)));
				}
			}

			public Rectangle2D getInnerPosition(String member, StringBounder stringBounder, InnerStrategy strategy) {
				return null;
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final double xmin = timeHeader.getTimeScale().getStartingPosition(min);
				final double xmax = timeHeader.getTimeScale().getEndingPosition(max);
				return new Dimension2DDouble(xmax - xmin, getTotalHeight(timeHeader));
			}

			public MinMax getMinMax(StringBounder stringBounder) {
				throw new UnsupportedOperationException();
			}

			public HColor getBackcolor() {
				return null;
			}
		};
	}

	private TimeHeader getTimeHeader() {
		if (openClose.getCalendar() == null) {
			return new TimeHeaderSimple(min, max);
		} else if (printScale == PrintScale.WEEKLY) {
			return new TimeHeaderWeekly(openClose.getCalendar(), min, max, openClose, colorDays, colorDaysOfWeek,
					nameDays);
		} else if (printScale == PrintScale.MONTHLY) {
			return new TimeHeaderMonthly(openClose.getCalendar(), min, max, openClose, colorDays, colorDaysOfWeek,
					nameDays);
		} else {
			return new TimeHeaderDaily(openClose.getCalendar(), min, max, openClose, colorDays, colorDaysOfWeek,
					nameDays, printStart, printEnd);
		}
	}

	private double getTotalHeight(TimeHeader timeHeader) {
		if (showFootbox) {
			return totalHeightWithoutFooter + timeHeader.getTimeFooterHeight();
		}
		return totalHeightWithoutFooter;
	}

	private void drawTasksRect(UGraphic ug) {
		for (Task task : tasks.values()) {
			if (isHidden(task)) {
				continue;
			}
			final TaskDraw draw = draws.get(task);
			final UTranslate move = UTranslate.dy(draw.getY());
			draw.drawU(ug.apply(move));
		}
	}

	private void drawConstraints(final UGraphic ug, TimeScale timeScale) {
		for (GanttConstraint constraint : constraints) {
			if (printStart != null && constraint.isHidden(min, max)) {
				continue;
			}
			constraint.getUDrawable(timeScale, getLinkColor(), this).drawU(ug);
		}

	}

	private HColor getLinkColor() {
		if (linksColor == null) {
			final Style styleArrow = getDefaultStyleDefinitionArrow().getMergedStyle(getCurrentStyleBuilder());
			return styleArrow.value(PName.LineColor).asColor(colorSet);
		}
		return linksColor;
	}

	public StyleSignature getDefaultStyleDefinitionArrow() {
		return StyleSignature.of(SName.root, SName.element, SName.ganttDiagram, SName.arrow);
	}

	private void drawTasksTitle(final UGraphic ug1) {
		for (Task task : tasks.values()) {
			if (isHidden(task)) {
				continue;
			}
			final TaskDraw draw = draws.get(task);
			final UTranslate move = UTranslate.dy(draw.getY());
			draw.drawTitle(ug1.apply(move));
		}
	}

	private void drawResources(UGraphic ug) {
		for (Resource res : resources.values()) {
			final ResourceDraw draw = res.getResourceDraw();
			final UTranslate move = UTranslate.dy(draw.getY());
			draw.drawU(ug.apply(move));
		}
	}

	public void closeDayOfWeek(DayOfWeek day) {
		openClose.close(day);
	}

	public void closeDayAsDate(Day day) {
		openClose.close(day);
	}

	public void openDayAsDate(Day day) {
		openClose.open(day);
	}

	private void initTaskAndResourceDraws(TimeScale timeScale, double headerHeight, StringBounder stringBounder) {
		double y = headerHeight;
		for (Task task : tasks.values()) {
			final TaskDraw draw;
			if (task instanceof TaskSeparator) {
				draw = new TaskDrawSeparator(((TaskSeparator) task).getName(), timeScale, y, min, max);
			} else {
				final TaskImpl tmp = (TaskImpl) task;
				if (tmp.isDiamond()) {
					draw = new TaskDrawDiamond(timeScale, y, tmp.getPrettyDisplay(), getStart(tmp), getSkinParam(),
							task, this);
				} else {
					final boolean oddStart = printStart != null && min.compareTo(getStart(tmp)) == 0;
					final boolean oddEnd = printStart != null && max.compareTo(getEnd(tmp)) == 0;
					draw = new TaskDrawRegular(timeScale, y, tmp.getPrettyDisplay(), getStart(tmp), getEnd(tmp),
							oddStart, oddEnd, getSkinParam(), task, this, getConstraints(task));
				}
				draw.setColorsAndCompletion(tmp.getColors(), tmp.getCompletion(), tmp.getUrl(), tmp.getNote());
			}
			if (task.getRow() == null) {
				y += draw.getHeightTask();
			}
			draws.put(task, draw);
		}
		while (magicPushOnce(stringBounder)) {
			//
		}
		if (lastY(stringBounder) != 0) {
			y = lastY(stringBounder);
			for (Resource res : resources.values()) {
				final ResourceDraw draw = new ResourceDraw(this, res, timeScale, y, min, max);
				res.setTaskDraw(draw);
				y += draw.getHeight();
			}
		}
		this.totalHeightWithoutFooter = y;
	}

	private Collection<GanttConstraint> getConstraints(Task task) {
		final List<GanttConstraint> result = new ArrayList<GanttConstraint>();
		for (GanttConstraint constraint : constraints) {
			if (constraint.isOn(task)) {
				result.add(constraint);
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	private double lastY(StringBounder stringBounder) {
		double result = 0;
		for (TaskDraw td : draws.values()) {
			result = Math.max(result, td.getY() + td.getHeightMax(stringBounder));
		}
		return result;
	}

	private boolean magicPushOnce(StringBounder stringBounder) {
		final List<FingerPrint> notes = new ArrayList<FingerPrint>();
		for (TaskDraw td : draws.values()) {
			final FingerPrint taskPrint = td.getFingerPrint();
			for (FingerPrint note : notes) {
				final double deltaY = note.overlap(taskPrint);
				if (deltaY > 0) {
					pushIncluding(td, deltaY);
					return true;
				}
			}

			final FingerPrint fingerPrintNote = td.getFingerPrintNote(stringBounder);
			if (fingerPrintNote != null) {
				notes.add(fingerPrintNote);
			}
		}
		return false;
	}

	private void pushIncluding(TaskDraw first, double deltaY) {
		boolean skipping = true;
		if (first.getTrueRow() != null) {
			first = first.getTrueRow();
		}
		for (TaskDraw td : draws.values()) {
			if (td == first) {
				skipping = false;
			}
			if (skipping) {
				continue;
			}
			td.pushMe(deltaY + 1);
		}

	}

	private Day getStart(final TaskImpl tmp) {
		if (printStart == null) {
			return tmp.getStart();
		}
		return Day.max(min, tmp.getStart());
	}

	private Day getEnd(final TaskImpl tmp) {
		if (printStart == null) {
			return tmp.getEnd();
		}
		return Day.min(max, tmp.getEnd());
	}

	private void initMinMax() {
		if (tasks.size() == 0) {
			max = min.increment();
		} else {
			max = null;
			for (Task task : tasks.values()) {
				if (task instanceof TaskSeparator) {
					continue;
				}
				final Day start = task.getStart();
				final Day end = task.getEnd();
				// if (min.compareTo(start) > 0) {
				// min = start;
				// }
				if (max == null || max.compareTo(end) < 0) {
					max = end;
				}
			}
		}
		if (openClose.getCalendar() != null) {
			for (Day d : colorDays.keySet()) {
				if (d.compareTo(max) > 0) {
					max = d;
				}
			}
			for (Day d : nameDays.keySet()) {
				if (d.compareTo(max) > 0) {
					max = d;
				}
			}
		}
	}

	public Day getThenDate() {
		Day result = getStartingDate();
		for (Day d : colorDays.keySet()) {
			if (d.compareTo(result) > 0) {
				result = d;
			}
		}
		for (Day d : nameDays.keySet()) {
			if (d.compareTo(result) > 0) {
				result = d;
			}
		}
		return result;
	}

	public Task getExistingTask(String id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}
		Task result = byShortName.get(id);
		if (result != null) {
			return result;
		}
		final TaskCode code = new TaskCode(id);
		return tasks.get(code);
	}

	public GanttConstraint forceTaskOrder(Task task1, Task task2) {
		final TaskInstant end1 = new TaskInstant(task1, TaskAttribute.END);
		task2.setStart(end1.getInstantPrecise());
		final GanttConstraint result = new GanttConstraint(end1, new TaskInstant(task2, TaskAttribute.START));
		addContraint(result);
		return result;
	}

	public Task getOrCreateTask(String codeOrShortName, String shortName, boolean linkedToPrevious) {
		if (codeOrShortName == null) {
			throw new IllegalArgumentException();
		}
		Task result = shortName == null ? null : byShortName.get(shortName);
		if (result != null) {
			return result;
		}
		result = byShortName.get(codeOrShortName);
		if (result != null) {
			return result;
		}
		final TaskCode code = new TaskCode(codeOrShortName);
		result = tasks.get(code);
		if (result == null) {
			Task previous = null;
			if (linkedToPrevious) {
				previous = getLastCreatedTask();
			}
			result = new TaskImpl(code, openClose);
			tasks.put(code, result);
			if (byShortName != null) {
				byShortName.put(shortName, result);
			}
			if (previous != null) {
				forceTaskOrder(previous, result);
			}
		}
		return result;
	}

	private Task getLastCreatedTask() {
		final List<Task> all = new ArrayList<Task>(tasks.values());
		for (int i = all.size() - 1; i >= 0; i--) {
			if (all.get(i) instanceof TaskImpl) {
				return all.get(i);
			}
		}
		return null;
	}

	public void addSeparator(String comment) {
		TaskSeparator separator = new TaskSeparator(comment, tasks.size());
		tasks.put(separator.getCode(), separator);
	}

	public void addContraint(GanttConstraint constraint) {
		constraints.add(constraint);
	}

	public HColorSet getIHtmlColorSet() {
		return colorSet;
	}

	public void setStartingDate(Day start) {
		openClose.setCalendar(start);
		this.min = start;
	}

	public Day getStartingDate() {
		return openClose.getCalendar();
	}

	public Day getStartingDate(int nday) {
		if (openClose.getCalendar() == null) {
			return null;
		}
		return openClose.getCalendar().addDays(nday);
	}

	public int daysInWeek() {
		return openClose.daysInWeek();
	}

	public boolean isOpen(Day day) {
		return openClose.getLoadAt(day) > 0;
	}

	public boolean affectResource(Task result, String description) {
		final Pattern p = Pattern.compile("([^:]+)(:(\\d+))?");
		final Matcher m = p.matcher(description);
		if (m.find() == false) {
			throw new IllegalArgumentException();
		}
		final Resource resource = getResource(m.group(1));
		int percentage = 100;
		if (m.group(3) != null) {
			percentage = Integer.parseInt(m.group(3));
		}
		if (percentage == 0) {
			return false;
		}
		result.addResource(resource, percentage);
		return true;
	}

	public Resource getResource(String resourceName) {
		Resource resource = resources.get(resourceName);
		if (resource == null) {
			resource = new Resource(resourceName);
		}
		resources.put(resourceName, resource);
		return resource;
	}

	public int getLoadForResource(Resource res, Day i) {
		int result = 0;
		for (Task task : tasks.values()) {
			if (task instanceof TaskSeparator) {
				continue;
			}
			final TaskImpl task2 = (TaskImpl) task;
			result += task2.loadForResource(res, i);
		}
		return result;
	}

	public Moment getExistingMoment(String id) {
		Moment result = getExistingTask(id);
		if (result == null) {
			Day start = null;
			Day end = null;
			for (Map.Entry<Day, String> ent : nameDays.entrySet()) {
				if (ent.getValue().equalsIgnoreCase(id) == false) {
					continue;
				}
				start = min(start, ent.getKey());
				end = max(end, ent.getKey());
			}
			if (start != null) {
				result = new MomentImpl(start, end);
			}
		}
		return result;
	}

	private Day min(Day d1, Day d2) {
		if (d1 == null) {
			return d2;
		}
		if (d1.compareTo(d2) > 0) {
			return d2;
		}
		return d1;
	}

	private Day max(Day d1, Day d2) {
		if (d1 == null) {
			return d2;
		}
		if (d1.compareTo(d2) < 0) {
			return d2;
		}
		return d1;
	}

	public void colorDay(Day day, HColor color) {
		colorDays.put(day, color);
	}

	public void colorDay(DayOfWeek day, HColor color) {
		colorDaysOfWeek.put(day, color);
	}

	public void nameDay(Day day, String name) {
		nameDays.put(day, name);
	}

	public void setTodayColors(CenterBorderColor colors) {
		if (today == null) {
			this.today = Day.today();
		}
		colorDay(today, colors.getCenter());
	}

	public CommandExecutionResult setToday(Day date) {
		this.today = date;
		return CommandExecutionResult.ok();
	}

	public CommandExecutionResult deleteTask(Task task) {
		task.setColors(new CenterBorderColor(HColorUtils.WHITE, HColorUtils.BLACK));
		return CommandExecutionResult.ok();
	}

	public void setPrintInterval(Day start, Day end) {
		this.printStart = start;
		this.printEnd = end;
	}

	public void setLinksColor(HColor color) {
		this.linksColor = color;
	}

	public TaskDraw getTaskDraw(Task task) {
		return draws.get(task);
	}

	public CommandExecutionResult addNote(Display note) {
		Task last = null;
		for (Task current : tasks.values())
			last = current;
		if (last == null) {
			return CommandExecutionResult.error("No task defined");
		}
		last.setNote(note);
		return CommandExecutionResult.ok();
	}

	public LoadPlanable getDefaultPlan() {
		return openClose;
	}

	private boolean showFootbox = true;

	public void setShowFootbox(boolean footbox) {
		this.showFootbox = footbox;

	}

}
