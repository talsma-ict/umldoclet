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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FloatingNote;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EbnfExpression implements TextBlockable {

	private final List<Token> tokens = new ArrayList<>();
	private final boolean isCompact;
	private final String commentAbove;
	private final String commentBelow;

	public static EbnfExpression create(CharIterator it, boolean isCompact, String commentAbove, String commentBelow) {
		return new EbnfExpression(it, isCompact, commentAbove, commentBelow);
	}

	private EbnfExpression(CharIterator it, boolean isCompact, String commentAbove, String commentBelow) {
		this.isCompact = isCompact;
		this.commentAbove = commentAbove;
		this.commentBelow = commentBelow;
		while (true) {
			final char ch = it.peek(0);
			if (Character.isWhitespace(ch)) {
			} else if (isLetterOrDigit(ch)) {
				final String litteral = readLitteral(it);
				tokens.add(new Token(Symbol.LITTERAL, litteral));
				continue;
			} else if (ch == '*') {
				tokens.add(new Token(Symbol.REPETITION_SYMBOL, null));
			} else if (ch == '(' && it.peek(1) == '*') {
				final String comment = readComment(it);
				if (comment.trim().length() > 0)
					tokens.add(new Token(Symbol.COMMENT_TOKEN, comment));
				continue;
			} else if (ch == ',') {
				tokens.add(new Token(Symbol.CONCATENATION, null));
			} else if (ch == '|') {
				tokens.add(new Token(Symbol.ALTERNATION, null));
			} else if (ch == '=') {
				tokens.add(new Token(Symbol.DEFINITION, null));
			} else if (ch == '(') {
				tokens.add(new Token(Symbol.GROUPING_OPEN, null));
			} else if (ch == ')') {
				tokens.add(new Token(Symbol.GROUPING_CLOSE, null));
			} else if (ch == '[') {
				tokens.add(new Token(Symbol.OPTIONAL_OPEN, null));
			} else if (ch == ']') {
				tokens.add(new Token(Symbol.OPTIONAL_CLOSE, null));
			} else if (ch == '{') {
				tokens.add(new Token(Symbol.REPETITION_OPEN, null));
			} else if (ch == '}' && it.peek(1) == '-') {
				tokens.add(new Token(Symbol.REPETITION_MINUS_CLOSE, null));
				it.next();
			} else if (ch == '}') {
				tokens.add(new Token(Symbol.REPETITION_CLOSE, null));
			} else if (ch == ';' || ch == 0) {
				// it.next();
				break;
			} else if (ch == '\"') {
				final String litteral = readString(it);
				tokens.add(new Token(Symbol.TERMINAL_STRING1, litteral));
			} else if (ch == '\'') {
				final String litteral = readString(it);
				tokens.add(new Token(Symbol.TERMINAL_STRING2, litteral));
			} else {
				tokens.clear();
				return;
			}
			it.next();
			continue;
		}
	}

	public TextBlock getUDrawable(ISkinParam skinParam) {
		final Style style = ETile.getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());
		final FontConfiguration fc = style.getFontConfiguration(skinParam.getIHtmlColorSet());

		if (tokens.size() == 0)
			return EbnfEngine.syntaxError(fc, skinParam);

		try {
			final Iterator<Token> iterator = tokens.iterator();
			final Token name = iterator.next();
			final Token definition = iterator.next();
			if (definition.getSymbol() != Symbol.DEFINITION)
				return EbnfEngine.syntaxError(fc, skinParam);

			final TextBlock main;
			if (iterator.hasNext()) {
				final List<Token> full = new ShuntingYard(iterator).getOuputQueue();
				if (full.size() == 0)
					return EbnfEngine.syntaxError(fc, skinParam);

				main = getMainDrawing(skinParam, full.iterator());
			} else {
				final HColor lineColor = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
				main = new ETileWithCircles(new ETileEmpty(), lineColor);
			}

			TextBlock titleBox = new TitleBox(name.getData(), fc);
			if (commentAbove != null)
				titleBox = TextBlockUtils.mergeTB(getNoteAbove(skinParam), titleBox, HorizontalAlignment.CENTER);
			if (commentBelow != null)
				titleBox = TextBlockUtils.mergeTB(titleBox, getNoteBelow(skinParam), HorizontalAlignment.CENTER);

			return TextBlockUtils.mergeTB(titleBox, TextBlockUtils.withMargin(main, 0, 0, 10, 15),
					HorizontalAlignment.LEFT);
		} catch (Exception e) {
			e.printStackTrace();
			return EbnfEngine.syntaxError(fc, skinParam);
		}
	}

	private TextBlock getNoteAbove(ISkinParam skinParam) {
		if (commentAbove == null)
			return null;
		final FloatingNote note = FloatingNote.create(Display.getWithNewlines(commentAbove), skinParam, SName.ebnf);
		return note;
	}

	private TextBlock getNoteBelow(ISkinParam skinParam) {
		if (commentBelow == null)
			return null;
		final FloatingNote note = FloatingNote.create(Display.getWithNewlines(commentBelow), skinParam, SName.ebnf);
		return note;
	}

	private TextBlock getMainDrawing(ISkinParam skinParam, Iterator<Token> it) {
		final EbnfEngine engine = new EbnfEngine(skinParam);
		while (it.hasNext()) {
			final Token element = it.next();
			if (element.getSymbol() == Symbol.TERMINAL_STRING1 || element.getSymbol() == Symbol.TERMINAL_STRING2
					|| element.getSymbol() == Symbol.LITTERAL)
				engine.push(element);
			else if (element.getSymbol() == Symbol.COMMENT_ABOVE)
				engine.commentAbove(element.getData());
			else if (element.getSymbol() == Symbol.COMMENT_BELOW)
				engine.commentBelow(element.getData());
			else if (element.getSymbol() == Symbol.ALTERNATION)
				engine.alternation();
			else if (element.getSymbol() == Symbol.CONCATENATION)
				engine.concatenation();
			else if (element.getSymbol() == Symbol.OPTIONAL)
				engine.optional();
			else if (element.getSymbol() == Symbol.REPETITION_ZERO_OR_MORE)
				engine.repetitionZeroOrMore(isCompact);
			else if (element.getSymbol() == Symbol.REPETITION_ONE_OR_MORE)
				engine.repetitionOneOrMore();
			else if (element.getSymbol() == Symbol.REPETITION_SYMBOL)
				engine.repetitionSymbol();
			else
				throw new UnsupportedOperationException(element.toString());
		}

		return engine.getTextBlock();
	}

	private String readString(CharIterator it) {
		final char separator = it.peek(0);
		it.next();
		final StringBuilder sb = new StringBuilder();
		while (true) {
			final char ch = it.peek(0);
			if (ch == separator)
				return sb.toString();
			sb.append(ch);
			it.next();
		}
	}

	private String readLitteral(CharIterator it) {
		final StringBuilder sb = new StringBuilder();
		while (true) {
			final char ch = it.peek(0);
			if (isLetterOrDigit(ch) == false)
				return sb.toString();
			sb.append(ch);
			it.next();
		}
	}

	private String readComment(CharIterator it) {
		final StringBuilder sb = new StringBuilder();
		it.next();
		it.next();
		while (true) {
			final char ch = it.peek(0);
			if (ch == '\0')
				return sb.toString();
			if (ch == '*' && it.peek(1) == ')') {
				it.next();
				it.next();
				return sb.toString();
			}
			sb.append(ch);
			it.next();
		}
	}

	private boolean isLetterOrDigit(char ch) {
		return ch == '-' || ch == '_' || Character.isLetterOrDigit(ch);
	}

	public boolean isEmpty() {
		return tokens.size() == 0;
	}

}
