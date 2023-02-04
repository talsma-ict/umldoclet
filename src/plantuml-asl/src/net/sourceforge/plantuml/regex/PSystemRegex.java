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
package net.sourceforge.plantuml.regex;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.ebnf.ETile;
import net.sourceforge.plantuml.ebnf.ETileAlternation;
import net.sourceforge.plantuml.ebnf.ETileBox;
import net.sourceforge.plantuml.ebnf.ETileConcatenation;
import net.sourceforge.plantuml.ebnf.ETileOneOrMore;
import net.sourceforge.plantuml.ebnf.ETileOptional;
import net.sourceforge.plantuml.ebnf.ETileZeroOrMore;
import net.sourceforge.plantuml.ebnf.Symbol;
import net.sourceforge.plantuml.ebnf.TextBlockable;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColors;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.CharInspector;

public class PSystemRegex extends TitledDiagram {

	private final List<TextBlockable> expressions = new ArrayList<>();

	public PSystemRegex(UmlSource source) {
		super(source, UmlDiagramType.REGEX, null);
		final ISkinParam skinParam = getSkinParam();
		this.style = ETile.getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());
		this.fontConfiguration = style.getFontConfiguration(skinParam.getIHtmlColorSet());
		this.colorSet = skinParam.getIHtmlColorSet();
		this.lineColor = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Regular Expression)");
	}

	private final Deque<ETile> stack = new ArrayDeque<>();
	private final FontConfiguration fontConfiguration;
	private final Style style;
	private final HColorSet colorSet;
	private final HColor lineColor;

//	public CommandExecutionResult addBlocLines(BlocLines blines, String commentAbove, String commentBelow) {
//		final boolean isCompact = getPragma().isDefine("compact");
//		final CharIterator it = new CharIteratorImpl(blines);
//		final EbnfExpression tmp1 = EbnfExpression.create(it, isCompact, commentAbove, commentBelow);
//		if (tmp1.isEmpty())
//			return CommandExecutionResult.error("Unparsable expression");
//		expressions.add(tmp1);
//		return CommandExecutionResult.ok();
//
//	}
//
//	public CommandExecutionResult addNote(final Display note, Colors colors) {
//		expressions.add(new TextBlockable() {
//			@Override
//			public TextBlock getUDrawable(ISkinParam skinParam) {
//				final FloatingNote f = FloatingNote.create(note, skinParam, SName.ebnf);
//				return TextBlockUtils.withMargin(f, 0, 0, 5, 15);
//			}
//		});
//		return CommandExecutionResult.ok();
//	}

	@Override
	protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	private TextBlockBackcolored getTextBlock() {
//		while (stack.size() > 1)
//			concatenation();
		final ETile peekFirst = stack.peekFirst();
		final TextBlock tb = new AbstractTextBlock() {

			@Override
			public void drawU(UGraphic ug) {
				peekFirst.drawU(ug.apply(HColors.BLACK));
			}

			@Override
			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return peekFirst.calculateDimension(stringBounder);
			}
		};
		return TextBlockUtils.addBackcolor(tb, null);

	}

	public CommandExecutionResult addBlocLines(BlocLines from) {
		final CharInspector it = from.inspector();
		final List<ReToken> parsed1 = RegexExpression.parse(it);
		// System.err.println("parsed1=" + parsed1);
		final List<ReToken> parsed2 = addImplicitConcatenation(parsed1);
		// System.err.println("parsed2=" + parsed2);
		final ShuntingYard shuntingYard = new ShuntingYard(parsed2.iterator());
		final List<ReToken> result = shuntingYard.getOuputQueue();
		// System.err.println("result=" + result);
		for (ReToken token : result)
			if (token.getType() == ReTokenType.SIMPLE_CHAR)
				push(token, Symbol.TERMINAL_STRING1);
			else if (token.getType() == ReTokenType.ESCAPED_CHAR)
				push(token, Symbol.TERMINAL_STRING1);
			else if (token.getType() == ReTokenType.GROUP)
				push(token, Symbol.SPECIAL_SEQUENCE);
			else if (token.getType() == ReTokenType.CLASS)
				push(token, Symbol.LITTERAL);
			else if (token.getType() == ReTokenType.ANCHOR)
				push(token, Symbol.LITTERAL);
			else if (token.getType() == ReTokenType.CONCATENATION_IMPLICIT)
				concatenation();
			else if (token.getType() == ReTokenType.ALTERNATIVE)
				alternation();
			else if (token.getType() == ReTokenType.QUANTIFIER && token.getData().startsWith("*"))
				repetitionZeroOrMore(false);
			else if (token.getType() == ReTokenType.QUANTIFIER && token.getData().startsWith("+"))
				repetitionOneOrMore();
			else if (token.getType() == ReTokenType.QUANTIFIER && token.getData().startsWith("?"))
				optional();
			else
				throw new UnsupportedOperationException(token.toString());

		return CommandExecutionResult.ok();
	}

	private List<ReToken> addImplicitConcatenation(List<ReToken> list) {
		final List<ReToken> result = new ArrayList<>();
		for (ReToken token : list) {
			if (result.size() > 0
					&& ReTokenType.needImplicitConcatenation(result.get(result.size() - 1).getType(), token.getType()))
				result.add(new ReToken(ReTokenType.CONCATENATION_IMPLICIT, ""));
			result.add(token);
		}
		return result;
	}

	private void push(ReToken element, Symbol type) {
		// final Symbol type = Symbol.LITTERAL;
		stack.addFirst(new ETileBox(element.getData(), type, fontConfiguration, style, colorSet, getSkinParam()));
	}

	private void repetitionZeroOrMore(boolean isCompact) {
		final ETile arg1 = stack.removeFirst();
		if (isCompact)
			stack.addFirst(new ETileZeroOrMore(arg1));
		else
			stack.addFirst(new ETileOptional(new ETileOneOrMore(arg1), getSkinParam()));
	}

	private void optional() {
		final ETile arg1 = stack.removeFirst();
		stack.addFirst(new ETileOptional(arg1, getSkinParam()));
	}

	private void repetitionOneOrMore() {
		final ETile arg1 = stack.removeFirst();
		stack.addFirst(new ETileOneOrMore(arg1));
	}

	private void alternation() {
		final ETile arg1 = stack.removeFirst();
		final ETile arg2 = stack.removeFirst();
		if (arg1 instanceof ETileAlternation) {
			arg1.push(arg2);
			stack.addFirst(arg1);
		} else if (arg2 instanceof ETileAlternation) {
			arg2.push(arg1);
			stack.addFirst(arg2);
		} else {
			final ETile concat = new ETileAlternation();
			concat.push(arg1);
			concat.push(arg2);
			stack.addFirst(concat);
		}
	}

	private void concatenation() {
		final ETile arg1 = stack.removeFirst();
		final ETile arg2 = stack.removeFirst();
		if (isBoxMergeable(arg1) && isBoxMergeable(arg2)) {
			final ETileBox box1 = (ETileBox) arg1;
			final ETileBox box2 = (ETileBox) arg2;
			stack.addFirst(box2.mergeWith(box1));
			return;
		}
		if (isConcatenation1Mergeable(arg1) && isBoxMergeable(arg2)) {
			final ETileConcatenation concat1 = (ETileConcatenation) arg1;
			final ETileBox box1 = (ETileBox) concat1.getFirst();
			final ETileBox box2 = (ETileBox) arg2;
			concat1.overideFirst(box2.mergeWith(box1));
			stack.addFirst(concat1);
			return;
		}
		if (arg1 instanceof ETileConcatenation) {
			arg1.push(arg2);
			stack.addFirst(arg1);
		} else if (arg2 instanceof ETileConcatenation) {
			arg2.push(arg1);
			stack.addFirst(arg2);
		} else {
			final ETile concat = new ETileConcatenation();
			concat.push(arg1);
			concat.push(arg2);
			stack.addFirst(concat);
		}
	}

	private boolean isConcatenation1Mergeable(ETile tile) {
		if (tile instanceof ETileConcatenation) {
			final ETileConcatenation concat = (ETileConcatenation) tile;
			return isBoxMergeable(concat.getFirst());
		}
		return false;
	}

	private boolean isBoxMergeable(ETile tile) {
		if (tile instanceof ETileBox) {
			final ETileBox box = (ETileBox) tile;
			return box.getSymbol() == Symbol.TERMINAL_STRING1;
		}
		return false;
	}

}
