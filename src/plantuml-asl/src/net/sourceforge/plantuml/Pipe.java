/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.security.SFile;

public class Pipe {

	private final Option option;
	private final BufferedReader br;
	private final PrintStream ps;
	private final Stdrpt stdrpt;

	public Pipe(Option option, PrintStream ps, InputStream is, String charset) {
		this.option = option;
		try {
			this.br = new BufferedReader(
					new InputStreamReader(is, (charset != null) ? charset : Charset.defaultCharset().name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Invalid charset provided", e);
		}
		this.ps = ps;
		this.stdrpt = option.getStdrpt();
	}

	public void managePipe(ErrorStatus error) throws IOException {
		final boolean noStdErr = option.isPipeNoStdErr();

		for(String source = readFirstDiagram(); source != null; source = readSubsequentDiagram()) {
			final Defines defines = option.getDefaultDefines();
			final SFile newCurrentDir = option.getFileDir() == null ? null : new SFile(option.getFileDir());
			final SourceStringReader sourceStringReader = new SourceStringReader(defines, source, UTF_8,
					option.getConfig(), newCurrentDir);

			if (option.isComputeurl()) {
				computeUrlForDiagram(sourceStringReader);
			} else if (option.isSyntax()) {
				syntaxCheckDiagram(sourceStringReader, error);
			} else if (option.isPipeMap()) {
				createPipeMapForDiagram(sourceStringReader, error);
			} else {
				generateDiagram(sourceStringReader, error, noStdErr);
			}
			ps.flush();
		}
	}

	private void generateDiagram(SourceStringReader sourceStringReader, ErrorStatus error, boolean noStdErr) throws IOException {
		final OutputStream os = noStdErr ? new ByteArrayOutputStream() : ps;
		final DiagramDescription result = sourceStringReader.outputImage(os, option.getImageIndex(),
				option.getFileFormatOption());

		printInfo(noStdErr ? ps : System.err, sourceStringReader);
		if (result != null && "(error)".equalsIgnoreCase(result.getDescription())) {
			error.goWithError();
		} else {
			error.goOk();
			if (noStdErr) {
				final ByteArrayOutputStream baos = (ByteArrayOutputStream) os;
				baos.close();
				ps.write(baos.toByteArray());
			}
		}
		if (option.getPipeDelimitor() != null) {
			ps.println(option.getPipeDelimitor());
		}
	}

	private void createPipeMapForDiagram(SourceStringReader sourceStringReader, ErrorStatus error) throws IOException {
		final String result = sourceStringReader.getCMapData(option.getImageIndex(),
				option.getFileFormatOption());
		// https://forum.plantuml.net/10049/2019-pipemap-diagrams-containing-links-give-zero-exit-code
		// We don't check errors
		error.goOk();
		if (result == null) {
			ps.println();
		} else {
			ps.println(result);
		}
	}

	private void computeUrlForDiagram(SourceStringReader sourceStringReader) throws IOException {
		for (BlockUml s : sourceStringReader.getBlocks()) {
			ps.println(s.getEncodedUrl());
		}
	}

	private void syntaxCheckDiagram(SourceStringReader sourceStringReader, ErrorStatus error) {
		final Diagram system = sourceStringReader.getBlocks().get(0).getDiagram();
		if (system instanceof UmlDiagram) {
			error.goOk();
			ps.println(((UmlDiagram) system).getUmlDiagramType().name());
			ps.println(system.getDescription());
		} else if (system instanceof PSystemError) {
			error.goWithError();
			stdrpt.printInfo(ps, system);
		} else {
			error.goOk();
			ps.println("OTHER");
			ps.println(system.getDescription());
		}
	}

	private void printInfo(final PrintStream output, final SourceStringReader sourceStringReader) {
		final List<BlockUml> blocks = sourceStringReader.getBlocks();
		if (blocks.size() == 0) {
			stdrpt.printInfo(output, null);
		} else {
			stdrpt.printInfo(output, blocks.get(0).getDiagram());
		}
	}

	String readFirstDiagram() throws IOException {
		return readSingleDiagram(true);
	}

	String readSubsequentDiagram() throws IOException {
		return readSingleDiagram(false);
	}

	String readSingleDiagram(boolean unmarkedAllowed) throws IOException {
		State state = State.NO_CONTENT;
		String expectedEnd = null;
		final StringBuilder sb = new StringBuilder();

		String line;
		while(state != State.COMPLETE && (line = br.readLine()) != null) {

			if(line.startsWith("@@@format ")) {
				manageFormat(line);
			} else {
				if (state == State.NO_CONTENT && line.trim().length() > 0) {
					state = State.START_MARK_NOT_FOUND;
				}

				if (state == State.START_MARK_NOT_FOUND && line.startsWith("@start")) {
					sb.setLength(0); // discard any previous input
					state = State.START_MARK_FOUND;
					expectedEnd = "@end" + line.substring(6).split("^[A-Za-z]")[0];
				} else if (state == State.START_MARK_FOUND && line.startsWith(expectedEnd)) {
					state = State.COMPLETE;
				}

				if (state != State.NO_CONTENT) {
					sb.append(line).append("\n");
				}
			}
		}

		switch(state) {
			case NO_CONTENT:
				return null;
			case START_MARK_NOT_FOUND:
				return (unmarkedAllowed) ? "@startuml\n" + sb.toString() + "@enduml\n" : null;
			case START_MARK_FOUND:
				return sb.toString() + expectedEnd;
			case COMPLETE:
				return sb.toString();
			default:
				throw new IllegalStateException("Unexpected value: " + state);
		}
	}

	void manageFormat(String s) {
		if (s.contains("png")) {
			option.setFileFormatOption(new FileFormatOption(FileFormat.PNG));
		} else if (s.contains("svg")) {
			option.setFileFormatOption(new FileFormatOption(FileFormat.SVG));
		}
	}

	 enum State {
		NO_CONTENT,
		START_MARK_NOT_FOUND,
		START_MARK_FOUND,
		COMPLETE
	}
}
