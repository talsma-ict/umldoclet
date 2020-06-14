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
package net.sourceforge.plantuml.bpm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;

public class BpmDiagram extends UmlDiagram {

	private void cleanGrid(Grid grid) {
		while (true) {
			final boolean v1 = new CleanerEmptyLine().clean(grid);
			final boolean v2 = new CleanerInterleavingLines().clean(grid);
			final boolean v3 = new CleanerMoveBlock().clean(grid);
			if (v1 == false && v2 == false && v3 == false) {
				return;
			}
		}
	}

	private final BpmElement start = new BpmElement(null, BpmElementType.START);

	private List<BpmEvent> events = new ArrayList<BpmEvent>();
	private Deque<BpmBranch> branches = new ArrayDeque<BpmBranch>();

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Bpm Diagram)");
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.BPM;
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final double dpiFactor = 1;
		final double margin = 10;
		final ImageBuilder imageBuilder = new ImageBuilder(getSkinParam(), dpiFactor,
				fileFormatOption.isWithMetadata() ? getMetadata() : null, getWarningOrError(), margin, margin,
				getAnimation());
		imageBuilder.setUDrawable(getUDrawable());

		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, seed(), os);
	}

	private UDrawable getUDrawable() {
		final Grid grid = createGrid();
		cleanGrid(grid);
		final GridArray gridArray = grid.toArray(SkinParam.create(getUmlDiagramType()));
		// gridArray.addEdges(edges);
		System.err.println("gridArray=" + gridArray);
		return gridArray;
	}

	public CommandExecutionResult addEvent(BpmEvent event) {
		this.events.add(event);
		return CommandExecutionResult.ok();
	}

	private Coord current;
	private Cell last;

	private Grid createGrid() {
		final Grid grid = new Grid();
		this.current = grid.getRoot();
		// this.edges.clear();
		last = grid.getCell(current);
		grid.getCell(current).setData(start);

		for (BpmEvent event : events) {
			if (event instanceof BpmEventAdd) {
				final BpmEventAdd tmp = (BpmEventAdd) event;
				addInGrid(grid, tmp.getElement());
			} else if (event instanceof BpmEventResume) {
				final String idDestination = ((BpmEventResume) event).getId();
				current = grid.getById(idDestination);
				last = grid.getCell(current);
				if (last == null) {
					throw new IllegalStateException();
				}
				final Navigator<Line> nav = grid.linesOf(current);
				final Line newLine = new Line();
				nav.insertAfter(newLine);
				final Col row = current.getCol();
				current = new Coord(newLine, row);
			} else if (event instanceof BpmEventGoto) {
				final BpmEventGoto tmp = (BpmEventGoto) event;
				final String idDestination = tmp.getId();
				current = grid.getById(idDestination);
				final Cell src = last;
				last = grid.getCell(current);
				if (last == null) {
					throw new IllegalStateException();
				}
				final Navigator<Line> nav = grid.linesOf(current);
				final Line newLine = new Line();
				nav.insertAfter(newLine);
				final Col row = current.getCol();
				current = new Coord(newLine, row);
				src.addConnectionTo2(last.getData());
			} else {
				throw new IllegalStateException();
			}
		}
		grid.addConnections();
		// for (GridEdge edge : edges) {
		// System.err.println("EDGE=" + edge.getEdgeDirection());
		// edge.addLineIn(grid);
		// }
		// grid.addEdge(edges);
		return grid;
	}

	private void addInGrid(Grid grid, BpmElement element) {
		final Navigator<Col> nav = grid.colsOf(current);
		final Col newRow = new Col();
		nav.insertAfter(newRow);
		current = new Coord(current.getLine(), newRow);
		grid.getCell(current).setData(element);
		last.addConnectionTo2(grid.getCell(current).getData());
		last = grid.getCell(current);

	}

	public CommandExecutionResult newBranch() {
		final BpmBranch branch = new BpmBranch(events.size());
		branches.addLast(branch);
		return addEvent(new BpmEventAdd(branch.getEntryElement()));
	}

	public CommandExecutionResult elseBranch() {
		final BpmBranch branch = branches.getLast();
		final int counter = branch.incAndGetCounter();
		if (counter == 2) {
			addEvent(new BpmEventAdd(branch.getElseElement()));
			return addEvent(branch.getResumeEntryEvent());
		}
		addEvent(branch.getGoToEndEvent());
		return addEvent(branch.getResumeEntryEvent());
	}

	public CommandExecutionResult endBranch() {
		final BpmBranch branch = branches.removeLast();
		return addEvent(branch.getGoToEndEvent());
	}
}
