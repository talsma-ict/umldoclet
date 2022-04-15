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
package net.sourceforge.plantuml.ugraphic.debug;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.Color;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UComment;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.USegment;
import net.sourceforge.plantuml.ugraphic.USegmentType;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorMiddle;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class UGraphicDebug extends AbstractCommonUGraphic implements ClipContainer {

	private final List<String> output;
	private final double scaleFactor;
	private final Dimension2D dim;
	private final String svgLinkTarget;
	private final String hoverPathColorRGB;
	private final long seed;
	private final String preserveAspectRatio;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicDebug(this, output, scaleFactor, dim, svgLinkTarget, hoverPathColorRGB, seed,
				preserveAspectRatio);
	}

	private UGraphicDebug(UGraphicDebug other, List<String> output, double scaleFactor, Dimension2D dim,
			String svgLinkTarget, String hoverPathColorRGB, long seed, String preserveAspectRatio) {
		super(other);
		this.output = output;
		this.scaleFactor = scaleFactor;
		this.dim = dim;
		this.svgLinkTarget = svgLinkTarget;
		this.hoverPathColorRGB = hoverPathColorRGB;
		this.seed = seed;
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public UGraphicDebug(double scaleFactor, Dimension2D dim, String svgLinkTarget, String hoverPathColorRGB, long seed,
			String preserveAspectRatio) {
		super(HColorUtils.WHITE, new ColorMapperIdentity(), new StringBounderDebug());
		this.output = new ArrayList<>();
		this.scaleFactor = scaleFactor;
		this.dim = dim;
		this.svgLinkTarget = svgLinkTarget;
		this.hoverPathColorRGB = hoverPathColorRGB;
		this.seed = seed;
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public void draw(UShape shape) {
		if (shape instanceof ULine) {
			outLine((ULine) shape);
		} else if (shape instanceof URectangle) {
			outRectangle((URectangle) shape);
		} else if (shape instanceof UText) {
			outText((UText) shape);
		} else if (shape instanceof UPolygon) {
			outPolygon((UPolygon) shape);
		} else if (shape instanceof UEllipse) {
			outEllipse((UEllipse) shape);
		} else if (shape instanceof UEmpty) {
			outEmpty((UEmpty) shape);
		} else if (shape instanceof UPath) {
			outPath((UPath) shape);
		} else if (shape instanceof UComment) {
			outComment((UComment) shape);
		} else if (shape instanceof DotPath) {
			outPath(((DotPath) shape).toUPath());
		} else if (shape instanceof UCenteredCharacter) {
			outCenteredCharacter(((UCenteredCharacter) shape));
		} else {
			System.err.println("UGraphicDebug " + shape.getClass().getSimpleName());
			output.add("UGraphicDebug " + shape.getClass().getSimpleName() + " " + new Date());
		}
	}

	private void outCenteredCharacter(UCenteredCharacter shape) {
		output.add("CENTERED_CHAR:");
		output.add("  char: " + shape.getChar());
		output.add("  position: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  font: " + shape.getFont().toStringDebug());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("");

	}

	private void outComment(UComment shape) {
		output.add("COMMENT: " + shape.getComment());
	}

	private void outPath(UPath shape) {
		output.add("PATH:");
		for (USegment seg : shape) {
			final USegmentType type = seg.getSegmentType();
			final double coord[] = seg.getCoord();
			output.add("   - type: " + type);
			if (type == USegmentType.SEG_ARCTO) {
				output.add("     radius: " + pointd(coord[0], coord[1]));
				output.add("     angle: " + coord[2]);
				output.add("     largeArcFlag: " + (coord[3] != 0));
				output.add("     sweepFlag: " + (coord[4] != 0));
				output.add("     dest: " + pointd(coord[5], coord[6]));
			} else
				for (int i = 0; i < type.getNbPoints(); i++) {
					final String key = "     pt" + (i + 1) + ": ";
					output.add(key + pointd(coord[2 * i], coord[2 * i + 1]));
				}
		}

		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

	}

	private void outPolygon(UPolygon shape) {
		output.add("POLYGON:");
		output.add("  points:");
		for (Point2D pt : shape.getPoints()) {
			final double xp = getTranslateX() + pt.getX();
			final double yp = getTranslateY() + pt.getY();
			output.add("   - " + pointd(xp, yp));
		}
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

	}

	private void outText(UText shape) {
		output.add("TEXT:");
		output.add("  text: " + shape.getText());
		output.add("  position: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  orientation: " + shape.getOrientation());
		output.add("  font: " + shape.getFontConfiguration().toStringDebug());
		output.add("  color: " + colorToString(shape.getFontConfiguration().getColor()));
		output.add("  extendedColor: " + colorToString(shape.getFontConfiguration().getExtendedColor()));
		output.add("");
	}

	private void outEmpty(UEmpty shape) {
		output.add("EMPTY:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("");

	}

	private void outEllipse(UEllipse shape) {
		output.add("ELLIPSE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("  start: " + shape.getStart());
		output.add("  extend: " + shape.getExtend());
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

	}

	private void outRectangle(URectangle shape) {
		output.add("RECTANGLE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("  xCorner: " + (int) shape.getRx());
		output.add("  yCorner: " + (int) shape.getRy());
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

	}

	private void outLine(ULine shape) {
		output.add("LINE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getDX(), getTranslateY() + shape.getDY()));
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("");

	}

	private String pointd(double x, double y) {
		return String.format(Locale.US, "[ %.4f ; %.4f ]", x, y);
	}

	private String colorToString(HColor color) {
		if (color == null) {
			return "NULL_COLOR";
		}
		if (color instanceof HColorSimple) {
			final HColorSimple simple = (HColorSimple) color;
			final Color internal = simple.getColor999();
			if (simple.isMonochrome()) {
				return "monochrome " + Integer.toHexString(internal.getRGB());
			}
			return Integer.toHexString(internal.getRGB());
		}
		if (color instanceof HColorMiddle) {
			final HColorMiddle middle = (HColorMiddle) color;
			return "middle(" + colorToString(middle.getC1()) + " & " + colorToString(middle.getC1()) + " )";
		}
		System.err.println("Error colorToString " + color.getClass().getSimpleName());
		return color.getClass().getSimpleName() + " " + new Date();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		print(os, "DPI: " + dpi);
		print(os, "dimension: " + pointd(dim.getWidth(), dim.getHeight()));
		print(os, "scaleFactor: " + String.format(Locale.US, "%.4f", scaleFactor));
		print(os, "seed: " + seed);
		print(os, "svgLinkTarget: " + svgLinkTarget);
		print(os, "hoverPathColorRGB: " + hoverPathColorRGB);
		print(os, "preserveAspectRatio: " + preserveAspectRatio);
		print(os, "");

		for (String s : output) {
			print(os, s);
		}
		os.flush();
	}

	private void print(OutputStream os, String out) throws UnsupportedEncodingException, IOException {
		os.write(out.getBytes(UTF_8));
		os.write("\n".getBytes(UTF_8));
	}

}
