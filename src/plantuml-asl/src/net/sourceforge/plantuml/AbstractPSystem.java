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

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.ProtectedCommand;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositionned;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.stats.StatsUtilsIncrement;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.version.License;
import net.sourceforge.plantuml.version.Version;

public abstract class AbstractPSystem implements Diagram {

	private UmlSource source;
	private Scale scale;

	private String getVersion() {
		final StringBuilder toAppend = new StringBuilder();
		toAppend.append("PlantUML version ");
		toAppend.append(Version.versionString());
		toAppend.append("(" + Version.compileTimeString() + ")\n");
		toAppend.append("(" + License.getCurrent() + " source distribution)\n");
		for (String name : OptionPrint.interestingProperties()) {
			toAppend.append(name);
			toAppend.append(BackSlash.CHAR_NEWLINE);
		}
		return toAppend.toString();
	}

	final public String getMetadata() {
		if (source == null) {
			return getVersion();
		}
		final String rawString = source.getRawString();
		final String plainString = source.getPlainString();
		if (rawString != null && rawString.equals(plainString)) {
			return rawString + BackSlash.NEWLINE + getVersion();
		}
		return rawString + BackSlash.NEWLINE + plainString + BackSlash.NEWLINE + getVersion();
	}

	final public UmlSource getSource() {
		return source;
	}

	final public long seed() {
		if (source == null) {
			return 42;
		}
		return getSource().seed();
	}

	final public void setSource(UmlSource source) {
		this.source = source;
	}

	public int getNbImages() {
		return 1;
	}

	public DisplayPositionned getTitle() {
		if (source == null) {
			return DisplayPositionned.single(Display.empty(), HorizontalAlignment.CENTER, VerticalAlignment.TOP);
		}
		return DisplayPositionned.single(source.getTitle(), HorizontalAlignment.CENTER, VerticalAlignment.TOP);
	}

	public String getWarningOrError() {
		return null;
	}

	public String checkFinalError() {
		return null;
	}

	public void makeDiagramReady() {
	}

	public boolean isOk() {
		return true;
	}

	public CommandExecutionResult executeCommand(Command cmd, BlocLines lines) {
		cmd = new ProtectedCommand(cmd);
		try {
			return cmd.execute(this, lines);
		} catch (NoSuchColorException e) {
			return CommandExecutionResult.badColor();
		}
	}

	public boolean hasUrl() {
		return false;
	}

	final public ImageData exportDiagram(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final long now = System.currentTimeMillis();
		try {
			return exportDiagramNow(os, index, fileFormatOption, seed());
		} finally {
			if (OptionFlags.getInstance().isEnableStats()) {
				StatsUtilsIncrement.onceMoreGenerate(System.currentTimeMillis() - now, getClass(),
						fileFormatOption.getFileFormat());
			}
		}
	}

	final public void setScale(Scale scale) {
		this.scale = scale;
	}

	final public Scale getScale() {
		return scale;
	}

	protected abstract ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption,
			long seed) throws IOException;

}
