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
package net.sourceforge.plantuml.openiconic;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;

public class SvgPath {

	// http://www.w3.org/TR/SVG11/paths.html#PathDataEllipticalArcCommands
	// https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Paths
	// http://tutorials.jenkov.com/svg/path-element.html

	private List<Movement> movements = new ArrayList<>();
	private List<SvgCommand> commands = new ArrayList<>();

	public SvgPath(String path) {
		// System.err.println("before=" + path);
		path = StringDecipher.decipher(path);
		// System.err.println("after=" + path);

		for (final StringTokenizer st = new StringTokenizer(path); st.hasMoreTokens();) {
			final String token = st.nextToken();

			if (token.matches("[a-zA-Z]")) {
				commands.add(new SvgCommandLetter(token));
			} else {
				commands.add(new SvgCommandNumber(token));
			}
		}
		commands = insertMissingLetter(commands);
		checkArguments(commands);
		SvgPosition last = new SvgPosition();
		SvgPosition lastMove = new SvgPosition();
		SvgPosition mirrorControlPoint = null;
		final Iterator<SvgCommand> iterator = commands.iterator();
		while (iterator.hasNext()) {
			Movement movement = new Movement(iterator);
			movement = movement.toAbsoluteUpperCase(last);

			if (movement.getLetter() == 'Z')
				last = lastMove;

			if (movement.is('S'))
				movement = movement.mutoToC(mirrorControlPoint);

			movements.add(movement);

			if (movement.getLetter() == 'M')
				lastMove = movement.lastPosition();

			if (movement.lastPosition() != null)
				last = movement.lastPosition();

			mirrorControlPoint = movement.getMirrorControlPoint();
		}
	}

	private List<SvgCommand> insertMissingLetter(List<SvgCommand> commands) {
		final List<SvgCommand> result = new ArrayList<>();
		final Iterator<SvgCommand> it = commands.iterator();
		SvgCommandLetter lastLetter = null;
		while (it.hasNext()) {
			final SvgCommand cmd = it.next();
			// System.err.println("cmd=" + cmd);
			final int nb;
			if (cmd instanceof SvgCommandNumber) {
				// System.err.println("INSERTING " + lastLetter);
				result.add(lastLetter);
				result.add(cmd);
				nb = lastLetter.argumentNumber() - 1;
			} else {
				result.add(cmd);
				lastLetter = ((SvgCommandLetter) cmd).implicit();
				nb = lastLetter.argumentNumber();
			}
			for (int i = 0; i < nb; i++) {
				final SvgCommandNumber number = (SvgCommandNumber) it.next();
				result.add(number);
			}
		}
		return result;
	}

	private void checkArguments(List<SvgCommand> commands) {
		final Iterator<SvgCommand> it = commands.iterator();
		while (it.hasNext()) {
			final SvgCommandLetter cmd = (SvgCommandLetter) it.next();
			final int nb = cmd.argumentNumber();
			for (int i = 0; i < nb; i++) {
				final SvgCommandNumber number = (SvgCommandNumber) it.next();
			}
		}
	}

	public String toSvg() {
		final StringBuilder result = new StringBuilder("<path d=\"");
		for (Movement move : movements) {
			result.append(move.toSvg());
			result.append(' ');
		}
		result.append("\"/>");
		return result.toString();
	}

	private UPath toUPath(double factorx, double factory) {
		final UPath result = new UPath();
		for (Movement move : movements) {
			final char letter = move.getLetter();
			final SvgPosition lastPosition = move.lastPosition();
			if (letter == 'M') {
				result.moveTo(lastPosition.getXDouble() * factorx, lastPosition.getYDouble() * factory);
			} else if (letter == 'C') {
				final SvgPosition ctl1 = move.getSvgPosition(0);
				final SvgPosition ctl2 = move.getSvgPosition(2);
				result.cubicTo(ctl1.getXDouble() * factorx, ctl1.getYDouble() * factory, ctl2.getXDouble() * factorx,
						ctl2.getYDouble() * factory, lastPosition.getXDouble() * factorx,
						lastPosition.getYDouble() * factory);
			} else if (letter == 'L') {
				result.lineTo(lastPosition.getXDouble() * factorx, lastPosition.getYDouble() * factory);
			} else if (letter == 'A') {
				final double rx = move.getArgument(0);
				final double ry = move.getArgument(1);
				final double x_axis_rotation = move.getArgument(2);
				final double large_arc_flag = move.getArgument(3);
				final double sweep_flag = move.getArgument(4);
				result.arcTo(rx * factorx, ry * factory, x_axis_rotation, large_arc_flag, sweep_flag,
						lastPosition.getXDouble() * factorx, lastPosition.getYDouble() * factory);
			} else if (letter == 'Z') {
				result.closePath();
			} else {
				throw new UnsupportedOperationException("letter " + letter);
			}
		}
		result.setOpenIconic(true);
		return result;
	}

	private UPath toUPath(AffineTransform at) {
		final UPath result = new UPath();
		for (Movement move : movements) {
			final char letter = move.getLetter();
			final SvgPosition lastPosition = move.lastPosition();
			if (letter == 'M') {
				result.moveTo(lastPosition.affine(at));
			} else if (letter == 'C') {
				final SvgPosition ctl1 = move.getSvgPosition(0);
				final SvgPosition ctl2 = move.getSvgPosition(2);
				result.cubicTo(ctl1.affine(at), ctl2.affine(at), lastPosition.affine(at));
			} else if (letter == 'L') {
				result.lineTo(lastPosition.affine(at));
			} else if (letter == 'A') {
				final double rx = move.getArgument(0);
				final double ry = move.getArgument(1);
				final double x_axis_rotation = move.getArgument(2);
				final double large_arc_flag = move.getArgument(3);
				final double sweep_flag = move.getArgument(4);
				final Point2D tmp = lastPosition.affine(at);
				result.arcTo(rx * at.getScaleX(), ry * at.getScaleY(), x_axis_rotation, large_arc_flag, sweep_flag,
						tmp.getX(), tmp.getY());
			} else if (letter == 'Z') {
				result.closePath();
			} else {
				throw new UnsupportedOperationException("letter " + letter);
			}
		}
		result.setOpenIconic(true);
		return result;
	}

	public void drawMe(UGraphic ug, double factor) {
		final UPath path = toUPath(factor, factor);
		ug.draw(path);
	}

	public void drawMe(UGraphic ug, AffineTransform at) {
		final UPath path = toUPath(at);
		ug.draw(path);
	}
}
