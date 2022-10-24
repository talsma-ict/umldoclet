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
package net.sourceforge.plantuml.ebnf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FloatingNote;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;

public class PSystemEbnf extends TitledDiagram {

	private final List<TextBlockable> expressions = new ArrayList<>();

	public PSystemEbnf(UmlSource source) {
		super(source, UmlDiagramType.EBNF, null);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(EBNF)");
	}

	public CommandExecutionResult addBlocLines(BlocLines blines, String commentAbove, String commentBelow) {
		final boolean isCompact = getPragma().isDefine("compact");
		final CharIterator it = new CharIteratorImpl(blines);
		final EbnfExpression tmp1 = EbnfExpression.create(it, isCompact, commentAbove, commentBelow);
		if (tmp1.isEmpty())
			return CommandExecutionResult.error("Unparsable expression");
		expressions.add(tmp1);
		return CommandExecutionResult.ok();

	}

	public CommandExecutionResult addNote(final Display note, Colors colors) {
		expressions.add(new TextBlockable() {
			@Override
			public TextBlock getUDrawable(ISkinParam skinParam) {
				final FloatingNote f = FloatingNote.create(note, skinParam, SName.ebnf);
				return TextBlockUtils.withMargin(f, 0, 0, 5, 15);
			}
		});
		return CommandExecutionResult.ok();
	}

	@Override
	protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	private TextBlockBackcolored getTextBlock() {
		if (expressions.size() == 0) {
			final Style style = ETile.getStyleSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			final FontConfiguration fc = style.getFontConfiguration(getSkinParam().getIHtmlColorSet());

			final TextBlock tmp = EbnfEngine.syntaxError(fc, getSkinParam());
			return TextBlockUtils.addBackcolor(tmp, null);
		}

		TextBlock result = expressions.get(0).getUDrawable(getSkinParam());
		for (int i = 1; i < expressions.size(); i++)
			result = TextBlockUtils.mergeTB(result, expressions.get(i).getUDrawable(getSkinParam()),
					HorizontalAlignment.LEFT);
		return TextBlockUtils.addBackcolor(result, null);
	}

}
