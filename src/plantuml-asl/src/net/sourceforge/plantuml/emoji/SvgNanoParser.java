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
package net.sourceforge.plantuml.emoji;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.openiconic.SvgPath;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.ColorUtils;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorSimple;
import net.sourceforge.plantuml.ugraphic.color.HColors;

// Emojji from https://twemoji.twitter.com/
// Shorcut from https://api.github.com/emojis

public class SvgNanoParser implements Sprite {
	private final List<String> data = new ArrayList<>();
	private int minGray = 999;
	private int maxGray = -1;
	private final String svgStart;
	private final boolean keepColors;

	private String extractData(String name, String s) {
		final Pattern p = Pattern.compile(name + "=\"([^\"]+)\"");
		final Matcher m = p.matcher(s);
		if (m.find())
			return m.group(1);

		return null;
	}

	public SvgNanoParser(String svg, boolean keepColors) {
		this(Collections.singletonList(svg), keepColors);
	}

	public SvgNanoParser(List<String> svg, boolean keepColors) {
		this.svgStart = svg.get(0);
		this.keepColors = keepColors;

		for (String singleLine : svg) {
			final Pattern p = Pattern
					.compile("(\\<text .*?\\</text\\>)|(\\<(svg|path|g|circle|ellipse)[^<>]*\\>)|(\\</[^<>]*\\>)");
			final Matcher m = p.matcher(singleLine);
			while (m.find()) {
				final String s = m.group(0);
				if (s.startsWith("<path") || s.startsWith("<g ") || s.startsWith("<g>") || s.startsWith("</g>")
						|| s.startsWith("<circle ") || s.startsWith("<ellipse ") || s.startsWith("<text "))
					data.add(s);
				else if (s.startsWith("<svg") || s.startsWith("</svg")) {
					// Ignore
				} else
					System.err.println("???=" + s);
			}
		}
	}

	public void drawU(UGraphic ug, double scale, HColor colorForMonochrome) {
		UGraphicWithScale ugs = new UGraphicWithScale(ug, scale);

		synchronized (this) {
			if (colorForMonochrome != null && maxGray == -1)
				computeMinMaxGray();
		}

		final List<UGraphicWithScale> stack = new ArrayList<>();
		for (String s : data) {
			if (s.startsWith("<path ")) {
				drawPath(ugs, s, colorForMonochrome);
			} else if (s.startsWith("</g>")) {
				ugs = stack.remove(0);
			} else if (s.startsWith("<g>")) {
				stack.add(0, ugs);
			} else if (s.startsWith("<g ")) {
				stack.add(0, ugs);
				ugs = applyFill(ugs, s, colorForMonochrome);
				ugs = applyTransform(ugs, s);
			} else if (s.startsWith("<circle ")) {
				drawCircle(ugs, s, colorForMonochrome);
			} else if (s.startsWith("<ellipse ")) {
				drawEllipse(ugs, s, colorForMonochrome);
			} else if (s.startsWith("<text ")) {
				drawText(ugs, s, colorForMonochrome);
			} else {
				System.err.println("**?=" + s);
			}
		}
	}

	private void computeMinMaxGray() {
		for (String s : data) {
			if (s.contains("<path ") || s.contains("<g ") || s.contains("<circle ") || s.contains("<ellipse ")) {
				final HColor color = justExtractColor(s);
				if (color != null) {
					final int gray = getGray(color);
					minGray = Math.min(minGray, gray);
					maxGray = Math.max(maxGray, gray);
				}
			} else {
				// Nothing
			}
		}
	}

	private int getGray(HColor col) {
		final Color tmp = ColorUtils.getGrayScaleColor(col.toColor(ColorMapper.MONOCHROME));
		return tmp.getGreen();
	}

	private UGraphicWithScale applyFill(UGraphicWithScale ugs, String s, HColor colorForMonochrome) {
		final String fillString = extractData("fill", s);
		if (fillString == null)
			return ugs;

		if (fillString.equals("none")) {
			final String strokeString = extractData("stroke", s);
			if (strokeString == null)
				return ugs;
			ugs = ugs.apply(HColors.none().bg());
			final HColor stroke = getTrueColor(strokeString, colorForMonochrome);
			ugs = ugs.apply(stroke);
			final String strokeWidth = extractData("stroke-width", s);
			if (strokeWidth != null)
				ugs = ugs.apply(new UStroke(Double.parseDouble(strokeWidth)));

		} else {
			final HColor fill = getTrueColor(fillString, colorForMonochrome);
			ugs = ugs.apply(fill).apply(fill.bg());
		}

		return ugs;
	}

	private HColor justExtractColor(String s) {
		final String fillString = extractData("fill", s);
		if (fillString == null)
			return null;

		if (fillString.equals("none")) {
			final String strokeString = extractData("stroke", s);
			if (strokeString == null)
				return null;

			final HColor stroke = getTrueColor(strokeString, null);
			return stroke;

		} else {
			final HColor fill = getTrueColor(fillString, null);
			return fill;
		}

	}

	private HColor getTrueColor(String code, HColor colorForMonochrome) {
		final HColorSimple result = (HColorSimple) HColorSet.instance().getColorOrWhite(code);
		if (colorForMonochrome == null)
			return result;
		final HColorSimple color = (HColorSimple) colorForMonochrome;
		if (color.isGray())
			return result.asMonochrome();
		return result.asMonochrome(color, this.minGray, this.maxGray);
	}

	private void drawCircle(UGraphicWithScale ugs, String s, HColor colorForMonochrome) {
		ugs = applyFill(ugs, s, colorForMonochrome);
		ugs = applyTransform(ugs, s);

		final double scalex = ugs.getAffineTransform().getScaleX();
		final double scaley = ugs.getAffineTransform().getScaleY();

		final double deltax = ugs.getAffineTransform().getTranslateX();
		final double deltay = ugs.getAffineTransform().getTranslateY();

		final double cx = Double.parseDouble(extractData("cx", s)) * scalex;
		final double cy = Double.parseDouble(extractData("cy", s)) * scaley;
		final double rx = Double.parseDouble(extractData("r", s)) * scalex;
		final double ry = Double.parseDouble(extractData("r", s)) * scaley;

		final UTranslate translate = new UTranslate(deltax + cx - rx, deltay + cy - ry);
		ugs.apply(translate).draw(new UEllipse(rx * 2, ry * 2));
	}

	private void drawEllipse(UGraphicWithScale ugs, String s, HColor colorForMonochrome) {

		ugs = applyFill(ugs, s, colorForMonochrome);
		ugs = applyTransform(ugs, s);

		final double scalex = ugs.getAffineTransform().getScaleX();
		final double scaley = ugs.getAffineTransform().getScaleY();

		final double deltax = ugs.getAffineTransform().getTranslateX();
		final double deltay = ugs.getAffineTransform().getTranslateY();

		final double cx = Double.parseDouble(extractData("cx", s)) * scalex;
		final double cy = Double.parseDouble(extractData("cy", s)) * scaley;
		final double rx = Double.parseDouble(extractData("rx", s)) * scalex;
		final double ry = Double.parseDouble(extractData("ry", s)) * scaley;

		final UTranslate translate = new UTranslate(deltax + cx - rx, deltay + cy - ry);
		ugs.apply(translate).draw(new UEllipse(rx * 2, ry * 2));
	}

	private void drawText(UGraphicWithScale ugs, String s, HColor colorForMonochrome) {
		final double x = Double.parseDouble(extractData("x", s));
		final double y = Double.parseDouble(extractData("y", s));
		final String fill = extractData("fill", s);
		final int fontSize = Integer.parseInt(extractData("font-size", s));

		final Pattern p = Pattern.compile("\\<text[^<>]*\\>(.*?)\\</text\\>");
		final Matcher m = p.matcher(s);
		if (m.find()) {
			final String text = m.group(1);
			HColor color = HColorSet.instance().getColorOrWhite(fill);
			final FontConfiguration fc = FontConfiguration.create(UFont.sansSerif(fontSize), color, color, false);
			final UText utext = new UText(text, fc);
			UGraphic ug = ugs.getUg();
			ug = ug.apply(new UTranslate(x, y));
			ug.draw(utext);
		}
	}

	private void drawPath(UGraphicWithScale ugs, String s, HColor colorForMonochrome) {
		s = s.replace("id=\"", "ID=\"");
		ugs = applyFill(ugs, s, colorForMonochrome);
		ugs = applyTransform(ugs, s);

		final int x1 = s.indexOf("d=\"");
		final int x2 = s.indexOf('"', x1 + 3);
		final String tmp = s.substring(x1 + 3, x2);

		final SvgPath svgPath = new SvgPath(tmp);
		svgPath.drawMe(ugs.getUg(), ugs.getAffineTransform());

	}

	private UGraphicWithScale applyTransform(UGraphicWithScale ugs, String s) {
		final String transform = extractData("transform", s);
		if (transform == null)
			return ugs;

		if (transform.contains("rotate("))
			return applyRotate(ugs, transform);

		if (transform.contains("matrix("))
			return applyMatrix(ugs, transform);

		final double[] scale = getScale(transform);
		final UTranslate translate = getTranslate(transform);
		ugs = ugs.applyTranslate(translate.getDx(), translate.getDy());

		return ugs.applyScale(scale[0], scale[1]);
	}

	private UGraphicWithScale applyMatrix(UGraphicWithScale ugs, final String transform) {
		final Pattern p3 = Pattern.compile(
				"matrix\\(([-.0-9]+)[ ,]+([-.0-9]+)[ ,]+([-.0-9]+)[ ,]+([-.0-9]+)[ ,]+([-.0-9]+)[ ,]+([-.0-9]+)\\)");
		final Matcher m3 = p3.matcher(transform);
		if (m3.find()) {
			final double v1 = Double.parseDouble(m3.group(1));
			final double v2 = Double.parseDouble(m3.group(2));
			final double v3 = Double.parseDouble(m3.group(3));
			final double v4 = Double.parseDouble(m3.group(4));
			final double v5 = Double.parseDouble(m3.group(5));
			final double v6 = Double.parseDouble(m3.group(6));
			ugs = ugs.applyMatrix(v1, v2, v3, v4, v5, v6);
		} else
			System.err.println("WARNING: " + transform);
		return ugs;
	}

	private UGraphicWithScale applyRotate(UGraphicWithScale ugs, final String transform) {
		final Pattern p3 = Pattern.compile("rotate\\(([-.0-9]+)[ ,]+([-.0-9]+)[ ,]+([-.0-9]+)\\)");
		final Matcher m3 = p3.matcher(transform);
		if (m3.find()) {
			final double angle = Double.parseDouble(m3.group(1));
			final double x = Double.parseDouble(m3.group(2));
			final double y = Double.parseDouble(m3.group(3));
			ugs = ugs.applyRotate(angle, x, y);
		} else
			System.err.println("WARNING: " + transform);
		return ugs;
	}

	private UTranslate getTranslate(String transform) {
		double x = 0;
		double y = 0;

		final Pattern p3 = Pattern.compile("translate\\(([-.0-9]+)[ ,]+([-.0-9]+)\\)");
		final Matcher m3 = p3.matcher(transform);
		if (m3.find()) {
			x = Double.parseDouble(m3.group(1));
			y = Double.parseDouble(m3.group(2));
		} else {
			final Pattern p4 = Pattern.compile("translate\\(([-.0-9]+)\\)");
			final Matcher m4 = p4.matcher(transform);
			if (m4.find()) {
				x = Double.parseDouble(m4.group(1));
				y = Double.parseDouble(m4.group(1));
			}
		}
		return new UTranslate(x, y);
	}

	private double[] getScale(String transform) {
		final double scale[] = new double[] { 1, 1 };
		final Pattern p1 = Pattern.compile("scale\\(([-.0-9]+)\\)");
		final Matcher m1 = p1.matcher(transform);
		if (m1.find()) {
			scale[0] = Double.parseDouble(m1.group(1));
			scale[1] = scale[0];
		} else {
			final Pattern p2 = Pattern.compile("scale\\(([-.0-9]+)[ ,]+([-.0-9]+)\\)");
			final Matcher m2 = p2.matcher(transform);
			if (m2.find()) {
				scale[0] = Double.parseDouble(m2.group(1));
				scale[1] = Double.parseDouble(m2.group(2));
			}
		}
		return scale;
	}

	@Override
	public TextBlock asTextBlock(final HColor color, final double scale) {

		final UImageSvg data = new UImageSvg(svgStart, scale);
		final double width = data.getWidth();
		final double height = data.getHeight();

		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				SvgNanoParser.this.drawU(ug, scale, keepColors ? null : color);
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return new XDimension2D(width, height);
			}
		};
	}

}
