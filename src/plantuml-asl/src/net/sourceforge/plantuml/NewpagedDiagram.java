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
package net.sourceforge.plantuml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;

public class NewpagedDiagram extends AbstractPSystem {

	private final List<Diagram> diagrams = new ArrayList<Diagram>();

	public NewpagedDiagram(AbstractPSystem diag1, AbstractPSystem diag2) {
		if (diag1 instanceof NewpagedDiagram) {
			throw new IllegalArgumentException();
		}
		if (diag2 instanceof NewpagedDiagram) {
			throw new IllegalArgumentException();
		}
		this.diagrams.add(diag1);
		this.diagrams.add(diag2);
	}

	@Override
	public String toString() {
		return super.toString() + " SIZE=" + diagrams.size() + " " + diagrams;
	}

	public Diagram getLastDiagram() {
		return diagrams.get(diagrams.size() - 1);
	}

	public CommandExecutionResult executeCommand(Command cmd, BlocLines lines) {
		final int nb = diagrams.size();
		final CommandExecutionResult tmp = cmd.execute(diagrams.get(nb - 1), lines);
		if (tmp.getNewDiagram() instanceof NewpagedDiagram) {
			final NewpagedDiagram new1 = (NewpagedDiagram) tmp.getNewDiagram();
			// System.err.println("this=" + this);
			// System.err.println("new1=" + new1);
			if (new1.size() != 2) {
				throw new IllegalStateException();
			}
			if (new1.diagrams.get(0) != this.diagrams.get(nb - 1)) {
				throw new IllegalStateException();
			}
			this.diagrams.add(new1.diagrams.get(1));
			return tmp.withDiagram(this);

		}
		return tmp;
	}

	private int size() {
		return diagrams.size();
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		return diagrams.get(num).exportDiagram(os, 0, fileFormat);
	}

	public int getNbImages() {
		int nb = 0;
		for (Diagram d : diagrams) {
			nb += d.getNbImages();
		}
		return nb;
	}

	public DiagramDescription getDescription() {
		final StringBuilder sb = new StringBuilder();
		for (Diagram d : diagrams) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(d.getDescription());
		}
		return new DiagramDescription(sb.toString());
	}

	public String getWarningOrError() {
		final StringBuilder sb = new StringBuilder();
		for (Diagram d : diagrams) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			if (d.getWarningOrError() != null) {
				sb.append(d.getWarningOrError());
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}

	@Override
	public void makeDiagramReady() {
		super.makeDiagramReady();
		for (Diagram diagram : diagrams) {
			((AbstractPSystem) diagram).makeDiagramReady();
		}
	}

	@Override
	public String checkFinalError() {
		for (Diagram p : getDiagrams()) {
			final String check = ((AbstractPSystem) p).checkFinalError();
			if (check != null) {
				return check;
			}
		}
		return super.checkFinalError();
	}

	public final List<Diagram> getDiagrams() {
		return Collections.unmodifiableList(diagrams);
	}

}
