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

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.Objects;

import net.sourceforge.plantuml.annotation.HaxeIgnored;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

/**
 * A FileFormat with some parameters.
 * 
 * 
 * @author Arnaud Roques
 * 
 */
public final class FileFormatOption implements Serializable {

	private final FileFormat fileFormat;
	private boolean withMetadata;
	private final boolean useRedForError;
	private final String svgLinkTarget;
	private final String hoverColor;
	private final TikzFontDistortion tikzFontDistortion;
	private final double scale;
	private final String preserveAspectRatio;
	private final String watermark;
	private final ColorMapper colorMapper;

	public double getScaleCoef() {
		return scale;
	}

	@HaxeIgnored
	public FileFormatOption(FileFormat fileFormat) {
		this(fileFormat, true, false, null, false, null, TikzFontDistortion.getDefault(), 1.0, null, null,
				ColorMapper.IDENTITY);
	}

	@HaxeIgnored
	public FileFormatOption(FileFormat fileFormat, boolean withMetadata) {
		this(fileFormat, withMetadata, false, null, false, null, TikzFontDistortion.getDefault(), 1.0, null, null,
				ColorMapper.IDENTITY);
	}

	private FileFormatOption(FileFormat fileFormat, boolean withMetadata, boolean useRedForError, String svgLinkTarget,
			boolean debugsvek, String hoverColor, TikzFontDistortion tikzFontDistortion, double scale,
			String preserveAspectRatio, String watermark, ColorMapper colorMapper) {
		this.hoverColor = hoverColor;
		this.watermark = watermark;
		this.fileFormat = fileFormat;
		this.withMetadata = withMetadata;
		this.useRedForError = useRedForError;
		this.svgLinkTarget = svgLinkTarget;
		this.debugsvek = debugsvek;
		this.tikzFontDistortion = Objects.requireNonNull(tikzFontDistortion);
		this.scale = scale;
		this.preserveAspectRatio = preserveAspectRatio;
		this.colorMapper = colorMapper;
	}

	public StringBounder getDefaultStringBounder(SvgCharSizeHack charSizeHack) {
		return fileFormat.getDefaultStringBounder(tikzFontDistortion, charSizeHack);
	}

	public String getSvgLinkTarget() {
		return svgLinkTarget;
	}

	public final boolean isWithMetadata() {
		return withMetadata;
	}

	public final String getPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public FileFormatOption withUseRedForError() {
		return new FileFormatOption(fileFormat, withMetadata, true, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withTikzFontDistortion(TikzFontDistortion tikzFontDistortion) {
		return new FileFormatOption(fileFormat, withMetadata, true, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withSvgLinkTarget(String svgLinkTarget) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withPreserveAspectRatio(String preserveAspectRatio) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withHoverColor(String hoverColor) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withScale(double scale) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withWartermark(String watermark) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	public FileFormatOption withColorMapper(ColorMapper colorMapper) {
		return new FileFormatOption(fileFormat, withMetadata, useRedForError, svgLinkTarget, debugsvek, hoverColor,
				tikzFontDistortion, scale, preserveAspectRatio, watermark, colorMapper);
	}

	@Override
	public String toString() {
		return fileFormat.toString();
	}

	public final FileFormat getFileFormat() {
		return fileFormat;
	}

	@Deprecated
	public AffineTransform getAffineTransform() {
		return null;
	}

	public final boolean isUseRedForError() {
		return useRedForError;
	}

	private boolean debugsvek = false;

	public void setDebugSvek(boolean debugsvek) {
		this.debugsvek = debugsvek;
	}

	public boolean isDebugSvek() {
		return debugsvek;
	}

	public final String getHoverColor() {
		return hoverColor;
	}

	public void hideMetadata() {
		this.withMetadata = false;
	}

	public final TikzFontDistortion getTikzFontDistortion() {
		return tikzFontDistortion;
	}

	public final String getWatermark() {
		return watermark;
	}

	public ColorMapper getColorMapper() {
		return colorMapper;
	}

}
