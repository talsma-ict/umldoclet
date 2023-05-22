/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.drawing.svg;

import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;

public class SvgOption {

	private final boolean interactive;
	private final LengthAdjust lengthAdjust;
	private final String preserveAspectRatio;
	private final String hover;
	private final boolean svgDimensionStyle;
	private final XDimension2D minDim;
	private final HColor backcolor;
	private final double scale;
	private final ColorMapper colorMapper;
	private final String linkTarget;
	private final String font;

	public static SvgOption basic() {
		return new SvgOption(false, LengthAdjust.defaultValue(), "none", null, true, new XDimension2D(0, 0), null, 1.0,
				ColorMapper.IDENTITY, null, null);
	}

	private SvgOption(boolean interactive, LengthAdjust lengthAdjust, String preserveAspectRatio, String hover,
			boolean svgDimensionStyle, XDimension2D minDim, HColor backcolor, double scale, ColorMapper colorMapper,
			String linkTarget, String font) {
		this.interactive = interactive;
		this.lengthAdjust = lengthAdjust;
		this.preserveAspectRatio = preserveAspectRatio;
		this.hover = hover;
		this.svgDimensionStyle = svgDimensionStyle;
		this.minDim = minDim;
		this.backcolor = backcolor;
		this.scale = scale;
		this.colorMapper = colorMapper;
		this.linkTarget = linkTarget;
		this.font = font;
	}

	public SvgOption withInteractive() {
		return new SvgOption(true, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim, backcolor,
				scale, colorMapper, linkTarget, font);
	}

	public SvgOption withLengthAdjust(LengthAdjust lengthAdjust) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withPreserveAspectRatio(String preserveAspectRatio) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withHoverPathColorRGB(String hover) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withSvgDimensionStyle(boolean svgDimensionStyle) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withMinDim(XDimension2D minDim) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withBackcolor(HColor backcolor) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withScale(double scale) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withColorMapper(ColorMapper colorMapper) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withLinkTarget(String linkTarget) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public SvgOption withFont(String font) {
		return new SvgOption(interactive, lengthAdjust, preserveAspectRatio, hover, svgDimensionStyle, minDim,
				backcolor, scale, colorMapper, linkTarget, font);
	}

	public String getPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public LengthAdjust getLengthAdjust() {
		return lengthAdjust;
	}

	public String getHover() {
		return hover;
	}

	public boolean getSvgDimensionStyle() {
		return svgDimensionStyle;
	}

	public XDimension2D getMinDim() {
		return minDim;
	}

	public HColor getBackcolor() {
		return backcolor;
	}

	public double getScale() {
		return scale;
	}

	public ColorMapper getColorMapper() {
		return colorMapper;
	}

	public final String getLinkTarget() {
		return linkTarget;
	}

	public final String getFont() {
		return font;
	}

}
