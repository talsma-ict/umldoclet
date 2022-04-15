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
package net.sourceforge.plantuml.svg;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.SignatureUtils;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.tikz.TikzGraphics;
import net.sourceforge.plantuml.ugraphic.UGroupType;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.USegment;
import net.sourceforge.plantuml.ugraphic.USegmentType;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;
import net.sourceforge.plantuml.xml.XmlFactories;

public class SvgGraphics {

	// http://tutorials.jenkov.com/svg/index.html
	// http://www.svgbasics.com/
	// http://apike.ca/prog_svg_text.html
	// http://www.w3.org/TR/SVG11/shapes.html
	// http://en.wikipedia.org/wiki/Scalable_Vector_Graphics

	// Animation:
	// http://srufaculty.sru.edu/david.dailey/svg/
	// Shadow:
	// http://www.svgbasics.com/filters3.html
	// http://www.w3schools.com/svg/svg_feoffset.asp
	// http://www.adobe.com/svg/demos/samples.html

	private static final String XLINK_TITLE1 = "title";
	private static final String XLINK_TITLE2 = "xlink:title";
	private static final String XLINK_HREF1 = "href";
	private static final String XLINK_HREF2 = "xlink:href";

	final private Document document;
	final private Element root;
	final private Element defs;
	final private Element gRoot;

	private String fill = "black";
	private String stroke = "black";

//	private Collection<String> classesForDarkness = new LinkedHashSet<>();
//	private String strokeDark = "black";
//	private String fillDark = "black";

	private String strokeWidth;
	private String strokeDasharray = null;
	private final String backcolor;

	private int maxX = 10;
	private int maxY = 10;

	private final String preserveAspectRatio;

	private final double scale;
	private final String filterUid;
	private final String shadowId;
	private final String gradientId;
	private final boolean svgDimensionStyle;
	private final LengthAdjust lengthAdjust;

	private final boolean interactive;

	final protected void ensureVisible(double x, double y) {
		if (x > maxX) {
			maxX = (int) (x + 1);
		}
		if (y > maxY) {
			maxY = (int) (y + 1);
		}
	}

	public SvgGraphics(String backcolor, boolean svgDimensionStyle, Dimension2D minDim, double scale, String hover,
			long seed, String preserveAspectRatio, LengthAdjust lengthAdjust, DarkStrategy darkStrategy,
			boolean interactive) {
		try {
			this.lengthAdjust = lengthAdjust;
			this.svgDimensionStyle = svgDimensionStyle;
			this.scale = scale;
			this.document = getDocument();
			this.backcolor = backcolor;
			this.preserveAspectRatio = preserveAspectRatio;
			this.interactive = interactive;
			ensureVisible(minDim.getWidth(), minDim.getHeight());

			this.root = getRootNode();

			// Create a node named defs, which will be the parent
			// for a pair of linear gradient definitions.
			defs = simpleElement("defs");
			gRoot = simpleElement("g");
			strokeWidth = "" + scale;
			this.filterUid = "b" + getSeed(seed);
			this.shadowId = "f" + getSeed(seed);
			this.gradientId = "g" + getSeed(seed);
			if (hover != null)
				defs.appendChild(getPathHover(hover));

			if (interactive) {
				final Element styles = getStylesForInteractiveMode();
				if (styles != null)
					defs.appendChild(styles);

				final Element script = getScriptForInteractiveMode();
				if (script != null)
					defs.appendChild(script);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	private Element getStylesForInteractiveMode() {
		final Element style = simpleElement("style");
		final String text = getData("default.css");
		if (text == null)
			return null;

		final CDATASection cdata = document.createCDATASection(text);
		style.setAttribute("type", "text/css");
		style.appendChild(cdata);
		return style;
	}

//	private Element getStylesForDarkness() {
//		final Element style = simpleElement("style");
//		final StringBuilder text1 = new StringBuilder();
//		final StringBuilder text2 = new StringBuilder("@media (prefers-color-scheme:dark) {");
//		final Pattern p = Pattern.compile("^(\\w)_(\\w+)_(\\w+)$");
//		for (String s : this.classesForDarkness) {
//			final Matcher m = p.matcher(s);
//			if (m.matches() == false)
//				throw new IllegalStateException();
//			final String color1 = m.group(2);
//			final String color2 = m.group(3);
//			final String type = m.group(1);
//			if ("f".equals(type)) {
//				text1.append("*." + s + " {fill:#" + color1 + ";}");
//				text2.append("*." + s + " {fill:#" + color2 + ";}");
//			} else if ("s".equals(type)) {
//				text1.append("*." + s + " {stroke:#" + color1 + ";}");
//				text2.append("*." + s + " {stroke:#" + color2 + ";}");
//			} else
//				throw new IllegalStateException();
//		}
//		text2.append("}");
//		final CDATASection cdata = document.createCDATASection(text1.toString() + text2.toString());
//		style.setAttribute("type", "text/css");
//		style.appendChild(cdata);
//		return style;
//	}

	private Element getScriptForInteractiveMode() {
		final Element script = document.createElement("script");
		final String text = getData("default.js");
		if (text == null)
			return null;

		script.setTextContent(text);
		return script;
	}

	private static String getData(final String name) {
		try {
			final InputStream is = SvgGraphics.class.getResourceAsStream("/svg/" + name);
			return FileUtils.readText(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Element getPathHover(String hover) {
		final Element style = simpleElement("style");
		final CDATASection cdata = document.createCDATASection("path:hover { stroke: " + hover + " !important;}");
		style.setAttribute("type", "text/css");
		style.appendChild(cdata);
		return style;
	}

	private static String getSeed(final long seed) {
		return Long.toString(Math.abs(seed), 36);
	}

	private Element pendingBackground;

	public void paintBackcolorGradient(ColorMapper mapper, HColorGradient gr) {
		final String id = createSvgGradient(mapper.toRGB(gr.getColor1()), mapper.toRGB(gr.getColor2()), gr.getPolicy());
		setFillColor("url(#" + id + ")");
		setStrokeColor(null);
		pendingBackground = createRectangleInternal(0, 0, 0, 0);
		getG().appendChild(pendingBackground);
	}

	// This method returns a reference to a simple XML
	// element node that has no attributes.
	private Element simpleElement(String type) {
		final Element theElement = (Element) document.createElement(type);
		root.appendChild(theElement);
		return theElement;
	}

	private Document getDocument() throws ParserConfigurationException {
		final DocumentBuilder builder = XmlFactories.newDocumentBuilder();
		final Document document = builder.newDocument();
		document.setXmlStandalone(true);
		return document;
	}

	// This method returns a reference to a root node that
	// has already been appended to the document.
	private Element getRootNode() {
		// Create the root node named svg and append it to
		// the document.
		final Element svg = (Element) document.createElement("svg");
		document.appendChild(svg);

		// Set some attributes on the root node that are
		// required for proper rendering. Note that the
		// approach used here is somewhat different from the
		// approach used in the earlier program named Svg01,
		// particularly with regard to the style.
		svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		svg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		svg.setAttribute("version", "1.1");

		return svg;
	}

	public void svgEllipse(double x, double y, double xRadius, double yRadius, double deltaShadow) {
		manageShadow(deltaShadow);
		if (hidden == false) {
			final Element elt = (Element) document.createElement("ellipse");
			elt.setAttribute("cx", format(x));
			elt.setAttribute("cy", format(y));
			elt.setAttribute("rx", format(xRadius));
			elt.setAttribute("ry", format(yRadius));
			fillMe(elt);
			elt.setAttribute("style", getStyle());
			manageDarkStroke(elt);
			addFilterShadowId(elt, deltaShadow);
			getG().appendChild(elt);
		}
		ensureVisible(x + xRadius + deltaShadow * 2, y + yRadius + deltaShadow * 2);
	}

	public void svgArcEllipse(double rx, double ry, double x1, double y1, double x2, double y2) {
		if (hidden == false) {
			final String path = "M" + format(x1) + "," + format(y1) + " A" + format(rx) + "," + format(ry) + " 0 0 0 "
					+ format(x2) + " " + format(y2);
			final Element elt = (Element) document.createElement("path");
			elt.setAttribute("d", path);
			fillMe(elt);
			elt.setAttribute("style", getStyle());
			getG().appendChild(elt);
		}
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
	}

	private Map<List<Object>, String> gradients = new HashMap<List<Object>, String>();

	public String createSvgGradient(String color1, String color2, char policy) {
		final List<Object> key = Arrays.asList((Object) color1, color2, policy);
		String id = gradients.get(key);
		if (id == null) {
			final Element elt = (Element) document.createElement("linearGradient");
			if (policy == '|') {
				elt.setAttribute("x1", "0%");
				elt.setAttribute("y1", "50%");
				elt.setAttribute("x2", "100%");
				elt.setAttribute("y2", "50%");
			} else if (policy == '\\') {
				elt.setAttribute("x1", "0%");
				elt.setAttribute("y1", "100%");
				elt.setAttribute("x2", "100%");
				elt.setAttribute("y2", "0%");
			} else if (policy == '-') {
				elt.setAttribute("x1", "50%");
				elt.setAttribute("y1", "0%");
				elt.setAttribute("x2", "50%");
				elt.setAttribute("y2", "100%");
			} else {
				elt.setAttribute("x1", "0%");
				elt.setAttribute("y1", "0%");
				elt.setAttribute("x2", "100%");
				elt.setAttribute("y2", "100%");
			}
			id = gradientId + gradients.size();
			gradients.put(key, id);
			elt.setAttribute("id", id);

			final Element stop1 = (Element) document.createElement("stop");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("offset", "0%");
			final Element stop2 = (Element) document.createElement("stop");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("offset", "100%");

			elt.appendChild(stop1);
			elt.appendChild(stop2);
			defs.appendChild(elt);
		}
		return id;
	}

	public final void setFillColor(String fill) {
		this.fill = fill == null ? "none" : fill;
		// this.fillDark = this.fill;
	}

	public final void setFillColor(String fill, String fillDark) {
		this.fill = fill == null ? "none" : fill;
		// this.fillDark = fillDark == null ? "none" : fillDark;
	}

	public final void setStrokeColor(String stroke) {
		this.stroke = stroke == null ? "none" : stroke;
		// this.strokeDark = stroke;
	}

	public final void setStrokeColor(String stroke, String strokeDark) {
		this.stroke = stroke == null ? "none" : stroke;
		// this.strokeDark = strokeDark == null ? "none" : strokeDark;
	}

	public final void setStrokeWidth(double strokeWidth, String strokeDasharray) {
		this.strokeWidth = "" + (scale * strokeWidth);
		this.strokeDasharray = strokeDasharray;
	}

	private final List<Element> pendingAction = new ArrayList<>();

	public final Element getG() {
		if (pendingAction.size() == 0)
			return gRoot;

		return pendingAction.get(0);
	}

	public void svgRectangle(double x, double y, double width, double height, double rx, double ry, double deltaShadow,
			String id, String codeLine) {
		if (height <= 0 || width <= 0) {
			return;
			// To be restored when Teoz will be finished
			// throw new IllegalArgumentException();
		}
		manageShadow(deltaShadow);
		if (hidden == false) {
			final Element elt = createRectangleInternal(x, y, width, height);
			addFilterShadowId(elt, deltaShadow);
			if (rx > 0 && ry > 0) {
				elt.setAttribute("rx", format(rx));
				elt.setAttribute("ry", format(ry));
			}
			if (id != null)
				elt.setAttribute("id", id);

			if (codeLine != null)
				elt.setAttribute("codeLine", codeLine);

			getG().appendChild(elt);
		}
		ensureVisible(x + width + 2 * deltaShadow, y + height + 2 * deltaShadow);
	}

	private Element createRectangleInternal(double x, double y, double width, double height) {
		final Element elt = (Element) document.createElement("rect");
		elt.setAttribute("x", format(x));
		elt.setAttribute("y", format(y));
		elt.setAttribute("width", format(width));
		elt.setAttribute("height", format(height));
		fillMe(elt);
		elt.setAttribute("style", getStyleSpecial());
		manageDarkStroke(elt);
		return elt;
	}

	private void manageDarkStroke(final Element elt) {
//		if (strokeDark != null && stroke.equals(strokeDark) == false) {
//			final String attribute = elt.getAttribute("class");
//			if (attribute == null || attribute.length() == 0)
//				elt.setAttribute("class", getStrokeClassForDark());
//			else
//				elt.setAttribute("class", attribute + " " + getStrokeClassForDark());
//		}
	}

	public void svgLine(double x1, double y1, double x2, double y2, double deltaShadow) {
		manageShadow(deltaShadow);
		if (hidden == false) {
			final Element elt = (Element) document.createElement("line");
			elt.setAttribute("x1", format(x1));
			elt.setAttribute("y1", format(y1));
			elt.setAttribute("x2", format(x2));
			elt.setAttribute("y2", format(y2));
			elt.setAttribute("style", getStyle());
			manageDarkStroke(elt);
			addFilterShadowId(elt, deltaShadow);
			getG().appendChild(elt);
		}
		ensureVisible(x1 + 2 * deltaShadow, y1 + 2 * deltaShadow);
		ensureVisible(x2 + 2 * deltaShadow, y2 + 2 * deltaShadow);
	}

	private String getStyle() {
		final StringBuilder style = new StringBuilder();
		// if (stroke.equals(strokeDark))
		style.append("stroke:" + stroke + ";");
		style.append("stroke-width:" + strokeWidth + ";");
		if (fill.equals("#00000000"))
			style.append("fill:none;");

		if (strokeDasharray != null)
			style.append("stroke-dasharray:" + strokeDasharray + ";");

		return style.toString();
	}

	// https://forum.plantuml.net/12469/package-background-transparent-package-default-background?show=12479#c12479
	private String getStyleSpecial() {
		final StringBuilder style = new StringBuilder();
		// if (stroke.equals(strokeDark))
		style.append("stroke:" + stroke + ";");
		style.append("stroke-width:" + strokeWidth + ";");
		if (fill.equals("#00000000"))
			style.append("fill:none;");

		if (strokeDasharray != null)
			style.append("stroke-dasharray:" + strokeDasharray + ";");

		return style.toString();
	}

	public void svgPolygon(double deltaShadow, double... points) {
		assert points.length % 2 == 0;
		manageShadow(deltaShadow);
		if (hidden == false) {
			final Element elt = (Element) document.createElement("polygon");
			final StringBuilder sb = new StringBuilder();
			for (double coord : points) {
				if (sb.length() > 0)
					sb.append(",");

				sb.append(format(coord));
			}
			elt.setAttribute("points", sb.toString());
			fillMe(elt);
			elt.setAttribute("style", getStyleSpecial());
			manageDarkStroke(elt);
			addFilterShadowId(elt, deltaShadow);
			getG().appendChild(elt);
		}

		for (int i = 0; i < points.length; i += 2) {
			ensureVisible(points[i] + 2 * deltaShadow, points[i + 1] + 2 * deltaShadow);
		}

	}

	public void text(String text, double x, double y, String fontFamily, int fontSize, String fontWeight,
			String fontStyle, String textDecoration, double textLength, Map<String, String> attributes,
			String textBackColor) {
		if (hidden == false) {
			final Element elt = (Element) document.createElement("text");
			// required for web-kit based browsers
			// elt.setAttribute("text-rendering", "geometricPrecision");
			elt.setAttribute("x", format(x));
			elt.setAttribute("y", format(y));
			fillMe(elt);
			elt.setAttribute("font-size", format(fontSize));
			// elt.setAttribute("text-anchor", "middle");

			if (lengthAdjust == LengthAdjust.SPACING) {
				elt.setAttribute("lengthAdjust", "spacing");
				elt.setAttribute("textLength", format(textLength));
			} else if (lengthAdjust == LengthAdjust.SPACING_AND_GLYPHS) {
				elt.setAttribute("lengthAdjust", "spacingAndGlyphs");
				elt.setAttribute("textLength", format(textLength));
			}

			if (fontWeight != null)
				elt.setAttribute("font-weight", fontWeight);

			if (fontStyle != null)
				elt.setAttribute("font-style", fontStyle);

			if (textDecoration != null)
				elt.setAttribute("text-decoration", textDecoration);

			if (fontFamily != null) {
				// http://plantuml.sourceforge.net/qa/?qa=5432/svg-monospace-output-has-wrong-font-family
				if ("monospaced".equalsIgnoreCase(fontFamily))
					fontFamily = "monospace";

				elt.setAttribute("font-family", fontFamily);

				if (fontFamily.equalsIgnoreCase("monospace") || fontFamily.equalsIgnoreCase("courier"))
					text = text.replace(' ', (char) 160);

			}
			if (textBackColor != null) {
				final String backFilterId = getFilterBackColor(textBackColor);
				elt.setAttribute("filter", "url(#" + backFilterId + ")");
			}
			for (Map.Entry<String, String> ent : attributes.entrySet())
				elt.setAttribute(ent.getKey(), ent.getValue());

			elt.setTextContent(text);
			getG().appendChild(elt);

			// http://forum.plantuml.net/9158/hyperlink-without-underline
			// if (textDecoration != null && textDecoration.contains("underline")) {
			// final double delta = 2;
			// final Element elt2 = (Element) document.createElement("line");
			// elt2.setAttribute("x1", format(x));
			// elt2.setAttribute("y1", format(y + delta));
			// elt2.setAttribute("x2", format(x + textLength));
			// elt2.setAttribute("y2", format(y + delta));
			// elt2.setAttribute("style", getStyleInternal(fill, "1.0", null));
			// getG().appendChild(elt2);
			// }

		}
		ensureVisible(x, y);
		ensureVisible(x + textLength, y);
	}

	private final Map<String, String> filterBackColor = new HashMap<String, String>();

	private String getIdFilterBackColor(String color) {
		String result = filterBackColor.get(color);
		if (result == null) {
			result = filterUid + filterBackColor.size();
			filterBackColor.put(color, result);
		}
		return result;
	}

	private String getFilterBackColor(String color) {
		String id = filterBackColor.get(color);
		if (id != null)
			return id;

		id = getIdFilterBackColor(color);
		final Element filter = (Element) document.createElement("filter");
		filter.setAttribute("id", id);
		filter.setAttribute("x", "0");
		filter.setAttribute("y", "0");
		filter.setAttribute("width", "1");
		filter.setAttribute("height", "1");
		addFilter(filter, "feFlood", "flood-color", color, "result", "flood");
		addFilter(filter, "feComposite", "in", "SourceGraphic", "in2", "flood", "operator", "over");
		defs.appendChild(filter);
		return id;
	}

	private Transformer getTransformer() throws TransformerException {
		final Transformer transformer = XmlFactories.newTransformer();
		Log.info("Transformer=" + transformer.getClass());

		// // Sets the standalone property in the first line of
		// // the output file.
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		// transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "SVG 1.1");
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		return transformer;
	}

	public void createXml(OutputStream os) throws TransformerException, IOException {
		if (images.size() == 0) {
			createXmlInternal(os);
			return;
		}
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		createXmlInternal(baos);
		String s = new String(baos.toByteArray());
		for (Map.Entry<String, String> ent : images.entrySet()) {
			final String k = "<" + ent.getKey() + "/>";
			s = s.replace(k, ent.getValue());
		}
		s = removeXmlHeader(s);
		os.write(s.getBytes());
	}

	private String removeXmlHeader(String s) {
		s = s.replaceFirst("^<\\?xml [^<>]+?\\>", "");
		return s;
	}

	private void createXmlInternal(OutputStream os) throws TransformerException {
//		if (this.classesForDarkness.size() > 0)
//			defs.appendChild(getStylesForDarkness());

		// Get a DOMSource object that represents the
		// Document object
		final DOMSource source = new DOMSource(document);

		final int maxXscaled = (int) (maxX * scale);
		final int maxYscaled = (int) (maxY * scale);
		String style = "width:" + maxXscaled + "px;height:" + maxYscaled + "px;";
		if (/*this.classesForDarkness.size() == 0 &&*/ backcolor != null)
			style += "background:" + backcolor + ";";

		if (svgDimensionStyle) {
			root.setAttribute("style", style);
			root.setAttribute("width", format(maxX) + "px");
			root.setAttribute("height", format(maxY) + "px");
		}
		root.setAttribute("viewBox", "0 0 " + maxXscaled + " " + maxYscaled);
		root.setAttribute("zoomAndPan", "magnify");
		root.setAttribute("preserveAspectRatio", preserveAspectRatio);
		// root.setAttribute("contentScriptType", "application/ecmascript");
		root.setAttribute("contentStyleType", "text/css");

		if (pendingBackground != null) {
			pendingBackground.setAttribute("width", format(maxX));
			pendingBackground.setAttribute("height", format(maxY));
		}

		// Get a StreamResult object that points to the
		// screen. Then transform the DOM sending XML to
		// the screen.
		final StreamResult scrResult = new StreamResult(os);
		getTransformer().transform(source, scrResult);
	}

	public void svgPath(double x, double y, UPath path, double deltaShadow) {
		manageShadow(deltaShadow);
		ensureVisible(x, y);
		final StringBuilder sb = new StringBuilder();
		for (USegment seg : path) {
			final USegmentType type = seg.getSegmentType();
			final double coord[] = seg.getCoord();
			if (type == USegmentType.SEG_MOVETO) {
				sb.append("M" + format(coord[0] + x) + "," + format(coord[1] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_LINETO) {
				sb.append("L" + format(coord[0] + x) + "," + format(coord[1] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_QUADTO) {
				sb.append("Q" + format(coord[0] + x) + "," + format(coord[1] + y) + " " + format(coord[2] + x) + ","
						+ format(coord[3] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
				ensureVisible(coord[2] + x + 2 * deltaShadow, coord[3] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_CUBICTO) {
				sb.append("C" + format(coord[0] + x) + "," + format(coord[1] + y) + " " + format(coord[2] + x) + ","
						+ format(coord[3] + y) + " " + format(coord[4] + x) + "," + format(coord[5] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
				ensureVisible(coord[2] + x + 2 * deltaShadow, coord[3] + y + 2 * deltaShadow);
				ensureVisible(coord[4] + x + 2 * deltaShadow, coord[5] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_ARCTO) {
				// A25,25 0,0 5,395,40
				sb.append("A" + format(coord[0]) + "," + format(coord[1]) + " " + formatBoolean(coord[2]) + " "
						+ formatBoolean(coord[3]) + " " + formatBoolean(coord[4]) + " " + format(coord[5] + x) + ","
						+ format(coord[6] + y) + " ");
				ensureVisible(coord[5] + coord[0] + x + 2 * deltaShadow, coord[6] + coord[1] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_CLOSE) {
				// Nothing
			} else {
				Log.println("unknown3 " + seg);
			}

		}
		if (hidden == false) {
			final Element elt = (Element) document.createElement("path");
			elt.setAttribute("d", sb.toString());
			elt.setAttribute("style", getStyle());
			fillMe(elt);
			manageDarkStroke(elt);
			final String id = path.getComment();
			if (id != null)
				elt.setAttribute("id", id);

			final String codeLine = path.getCodeLine();
			if (codeLine != null)
				elt.setAttribute("codeLine", codeLine);

			addFilterShadowId(elt, deltaShadow);
			getG().appendChild(elt);
		}
	}

//	private String getFillClassForDark() {
//		final String result = "f_" + fill.toLowerCase().replaceAll("\\#", "") + "_"
//				+ fillDark.toLowerCase().replaceAll("\\#", "");
//		this.classesForDarkness.add(result);
//		return result;
//	}
//
//	private String getStrokeClassForDark() {
//		final String result = "s_" + stroke.toLowerCase().replaceAll("\\#", "") + "_"
//				+ strokeDark.toLowerCase().replaceAll("\\#", "");
//		this.classesForDarkness.add(result);
//		return result;
//	}

	private void fillMe(Element elt) {
		if (fill.equals("#00000000"))
			return;

//		if (fill.equals(fillDark) == false) {
//			if (elt.getAttribute("class") != null && elt.getAttribute("class").length() != 0)
//				throw new IllegalStateException();
//
//			elt.setAttribute("class", getFillClassForDark());
//			return;
//		}

		if (fill.matches("#[0-9A-Fa-f]{8}")) {
			elt.setAttribute("fill", fill.substring(0, 7));
			final double opacity = Integer.parseInt(fill.substring(7), 16) / 255.0;
			elt.setAttribute("fill-opacity", String.format(Locale.US, "%1.5f", opacity));
		} else {
			elt.setAttribute("fill", fill);
		}
	}

	private void addFilterShadowId(final Element elt, double deltaShadow) {
		if (deltaShadow > 0)
			elt.setAttribute("filter", "url(#" + shadowId + ")");

	}

	private StringBuilder currentPath = null;

	public void newpath() {
		currentPath = new StringBuilder();

	}

	public void moveto(double x, double y) {
		currentPath.append("M" + format(x) + "," + format(y) + " ");
		ensureVisible(x, y);
	}

	public void lineto(double x, double y) {
		currentPath.append("L" + format(x) + "," + format(y) + " ");
		ensureVisible(x, y);
	}

	public void closepath() {
		currentPath.append("Z ");

	}

	public void curveto(double x1, double y1, double x2, double y2, double x3, double y3) {
		currentPath.append("C" + format(x1) + "," + format(y1) + " " + format(x2) + "," + format(y2) + " " + format(x3)
				+ "," + format(y3) + " ");
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
		ensureVisible(x3, y3);

	}

	public void quadto(double x1, double y1, double x2, double y2) {
		currentPath.append("Q" + format(x1) + "," + format(y1) + " " + format(x2) + "," + format(y2) + " ");
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
	}

	private String format(double x) {
		return TikzGraphics.format(x * scale);
	}

	private String formatBoolean(double x) {
		return x == 0 ? "0" : "1";
	}

	public void fill(int windingRule) {
		if (hidden == false) {
			final Element elt = (Element) document.createElement("path");
			elt.setAttribute("d", currentPath.toString());
			fillMe(elt);
			// elt elt.setAttribute("style", getStyle());
			getG().appendChild(elt);
		}
		currentPath = null;

	}

	public void drawPathIterator(double x, double y, PathIterator path) {

		this.newpath();
		final double coord[] = new double[6];
		while (path.isDone() == false) {
			final int code = path.currentSegment(coord);
			if (code == PathIterator.SEG_MOVETO)
				this.moveto(coord[0] + x, coord[1] + y);
			else if (code == PathIterator.SEG_LINETO)
				this.lineto(coord[0] + x, coord[1] + y);
			else if (code == PathIterator.SEG_CLOSE)
				this.closepath();
			else if (code == PathIterator.SEG_CUBICTO)
				this.curveto(coord[0] + x, coord[1] + y, coord[2] + x, coord[3] + y, coord[4] + x, coord[5] + y);
			else if (code == PathIterator.SEG_QUADTO)
				this.quadto(coord[0] + x, coord[1] + y, coord[2] + x, coord[3] + y);
			else
				throw new UnsupportedOperationException("code=" + code);

			path.next();
		}

		this.fill(path.getWindingRule());

	}

	public void svgImage(BufferedImage image, double x, double y) throws IOException {
		if (hidden == false) {
			final Element elt = (Element) document.createElement("image");
			elt.setAttribute("width", format(image.getWidth()));
			elt.setAttribute("height", format(image.getHeight()));
			elt.setAttribute("x", format(x));
			elt.setAttribute("y", format(y));
			final String s = toBase64(image);
			elt.setAttribute("xlink:href", "data:image/png;base64," + s);
			getG().appendChild(elt);
		}
		ensureVisible(x, y);
		ensureVisible(x + image.getWidth(), y + image.getHeight());
	}

	private final Map<String, String> images = new HashMap<String, String>();

	public void svgImage(UImageSvg image, double x, double y) {
		if (hidden == false) {
			String svg = manageScale(image);
			final String pos = "<svg x=\"" + format(x) + "\" y=\"" + format(y) + "\">";
			svg = pos + svg.substring(5);
			final String key = "imagesvginlined" + image.getMD5Hex() + images.size();
			final Element elt = (Element) document.createElement(key);
			getG().appendChild(elt);
			images.put(key, svg);
		}
		ensureVisible(x, y);
		ensureVisible(x + image.getData("width"), y + image.getData("height"));
	}

	private String manageScale(UImageSvg svgImage) {
		final double svgScale = svgImage.getScale();
		String svg = svgImage.getSvg(false);
		if (svgScale * scale == 1)
			return svg;

		final String svg2 = svg.replace('\n', ' ').replace('\r', ' ');
		if (svg2.contains("<g ") == false && svg2.contains("<g>") == false) {
			svg = svg.replaceFirst("\\<svg\\>", "<svg><g>");
			svg = svg.replaceFirst("\\</svg\\>", "</g></svg>");
		}
		final String factor = format(svgScale);
		final String s1 = "\\<g\\b";
		final String s2 = "<g transform=\"scale(" + factor + "," + factor + ")\" ";
		svg = svg.replaceFirst(s1, s2);
		return svg;
	}

	private String toBase64(BufferedImage image) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SImageIO.write(image, "png", baos);
		final byte data[] = baos.toByteArray();
		return new String(Base64Coder.encode(data));
	}

	// Shadow

	private boolean withShadow = false;

	private void manageShadow(double deltaShadow) {
		if (deltaShadow != 0) {
			if (withShadow == false) {
				// <filter id="f1" x="0" y="0" width="120%" height="120%">
				final Element filter = (Element) document.createElement("filter");
				filter.setAttribute("id", shadowId);
				filter.setAttribute("x", "-1");
				filter.setAttribute("y", "-1");
				filter.setAttribute("width", "300%");
				filter.setAttribute("height", "300%");
				addFilter(filter, "feGaussianBlur", "result", "blurOut", "stdDeviation", "" + (2 * scale));
				addFilter(filter, "feColorMatrix", "type", "matrix", "in", "blurOut", "result", "blurOut2", "values",
						"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 .4 0");
				addFilter(filter, "feOffset", "result", "blurOut3", "in", "blurOut2", "dx", "" + (4 * scale), "dy",
						"" + (4 * scale));
				addFilter(filter, "feBlend", "in", "SourceGraphic", "in2", "blurOut3", "mode", "normal");
				defs.appendChild(filter);

			}
			withShadow = true;
		}
	}

	private void addFilter(Element filter, String name, String... data) {
		assert data.length % 2 == 0;
		final Element elt = (Element) document.createElement(name);
		for (int i = 0; i < data.length; i += 2)
			elt.setAttribute(data[i], data[i + 1]);

		filter.appendChild(elt);
	}

	private boolean hidden;

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public static final String MD5_HEADER = "<!--MD5=[";

	public static String getMD5Hex(String comment) {
		return SignatureUtils.getMD5Hex(comment);
	}

	public void addComment(String comment) {
		final String signature = getMD5Hex(comment);
		comment = "MD5=[" + signature + "]\n" + comment;
		final Comment commentElement = document.createComment(comment);
		getG().appendChild(commentElement);
	}

	public void addScriptTag(String url) {
		final Element script = document.createElement("script");
		script.setAttribute("type", "text/javascript");
		script.setAttribute("xlink:href", url);
		root.appendChild(script);
	}

	public void addScript(String scriptTextPath) {
		final Element script = document.createElement("script");
		final String scriptText = getData(scriptTextPath);
		final CDATASection cDATAScript = document.createCDATASection(scriptText);
		script.appendChild(cDATAScript);
		root.appendChild(script);
	}

	public void addStyle(String cssStylePath) {
		final Element style = simpleElement("style");
		final String text = getData(cssStylePath);

		final CDATASection cdata = document.createCDATASection(text);
		style.setAttribute("type", "text/css");
		style.appendChild(cdata);
		root.appendChild(style);
	}

	public void openLink(String url, String title, String target) {
		Objects.requireNonNull(url);

		// javascript: security issue
		if (SecurityUtils.ignoreThisLink(url))
			return;

//		if (pendingAction.size() > 0)
//			closeLink();

		pendingAction.add(0, (Element) document.createElement("a"));
		pendingAction.get(0).setAttribute("target", target);
		pendingAction.get(0).setAttribute(XLINK_HREF1, url);
		pendingAction.get(0).setAttribute(XLINK_HREF2, url);
		pendingAction.get(0).setAttribute("xlink:type", "simple");
		pendingAction.get(0).setAttribute("xlink:actuate", "onRequest");
		pendingAction.get(0).setAttribute("xlink:show", "new");
		if (title == null) {
			pendingAction.get(0).setAttribute(XLINK_TITLE1, url);
			pendingAction.get(0).setAttribute(XLINK_TITLE2, url);
		} else {
			title = formatTitle(title);
			pendingAction.get(0).setAttribute(XLINK_TITLE1, title);
			pendingAction.get(0).setAttribute(XLINK_TITLE2, title);
		}
	}

	private String formatTitle(String title) {
		final Pattern p = Pattern.compile("\\<U\\+([0-9A-Fa-f]+)\\>");
		final Matcher m = p.matcher(title);
		final StringBuffer sb = new StringBuffer(); // Can't be switched to StringBuilder in order to support Java 8
		while (m.find()) {
			final String num = m.group(1);
			final char c = (char) Integer.parseInt(num, 16);
			m.appendReplacement(sb, "" + c);
		}
		m.appendTail(sb);

		title = sb.toString().replaceAll("\\\\n", "\n");
		return title;
	}

	public void closeLink() {
		if (pendingAction.size() > 0) {
			final Element element = pendingAction.get(0);
			pendingAction.remove(0);
			if (element.getFirstChild() != null) {
				// Empty link
				getG().appendChild(element);
			}
		}
	}

	public void startGroup(Map<UGroupType, String> typeIdents) {
		if (typeIdents.isEmpty())
			throw new IllegalArgumentException();

		pendingAction.add(0, (Element) document.createElement("g"));

		for (Map.Entry<UGroupType, String> typeIdent : typeIdents.entrySet()) {
			if (typeIdent.getKey() == UGroupType.ID)
				pendingAction.get(0).setAttribute("id", typeIdent.getValue());
			if (interactive && typeIdent.getKey() == UGroupType.CLASS)
				pendingAction.get(0).setAttribute("class", typeIdent.getValue());
		}
	}

	public void closeGroup() {
		closeLink();
	}

}
