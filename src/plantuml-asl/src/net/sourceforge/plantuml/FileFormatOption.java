/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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

import net.sourceforge.plantuml.graphic.StringBounder;

/**
 * A FileFormat with some parameters.
 * 
 * 
 * @author Arnaud Roques
 * 
 */
public final class FileFormatOption implements Serializable {

	private final FileFormat fileFormat;
	private final AffineTransform affineTransform;
	private boolean withMetadata;
	private final boolean useRedForError;
	private final String svgLinkTarget;
	private final String hoverColor;
	private final TikzFontDistortion tikzFontDistortion;
	private final double scale;
	private final String preserveAspectRatio;

	public double getScaleCoef() {
		return scale;
	}

	public FileFormatOption(FileFormat fileFormat) {
		this(fileFormat, null, true, false, "_top", false, null, TikzFontDistortion.getDefault(), 1.0, "none");
	}

	public StringBounder getDefaultStringBounder() {
		return fileFormat.getDefaultStringBounder(tikzFontDistortion);
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

	public FileFormatOption(FileFormat fileFormat, boolean withMetadata) {
		this(fileFormat, null, withMetadata, false, "_top", false, null, TikzFontDistortion.getDefault(), 1.0, "none");
	}

	private FileFormatOption(FileFormat fileFormat, AffineTransform at, boolean withMetadata, boolean useRedForError,
			String svgLinkTarget, boolean debugsvek, String hoverColor, TikzFontDistortion tikzFontDistortion,
			double scale, String preserveAspectRatio) {
		this.hoverColor = hoverColor;
		this.fileFormat = fileFormat;
		this.affineTransform = at;
		this.withMetadata = withMetadata;
		this.useRedForError = useRedForError;
		this.svgLinkTarget = svgLinkTarget;
		this.debugsvek = debugsvek;
		this.tikzFontDistortion = tikzFontDistortion;
		this.scale = scale;
		this.preserveAspectRatio = preserveAspectRatio;
		if (tikzFontDistortion == null) {
			throw new IllegalArgumentException();
		}
	}

	public FileFormatOption withUseRedForError() {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, true, svgLinkTarget, debugsvek,
				hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	public FileFormatOption withTikzFontDistortion(TikzFontDistortion tikzFontDistortion) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, true, svgLinkTarget, debugsvek,
				hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	public FileFormatOption withSvgLinkTarget(String svgLinkTarget) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, useRedForError, svgLinkTarget,
				debugsvek, hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	public FileFormatOption withPreserveAspectRatio(String preserveAspectRatio) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, useRedForError, svgLinkTarget,
				debugsvek, hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	public FileFormatOption withHoverColor(String hoverColor) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, useRedForError, svgLinkTarget,
				debugsvek, hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	public FileFormatOption withScale(double scale) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, useRedForError, svgLinkTarget,
				debugsvek, hoverColor, tikzFontDistortion, scale, preserveAspectRatio);
	}

	@Override
	public String toString() {
		return fileFormat.toString() + " " + affineTransform;
	}

	public final FileFormat getFileFormat() {
		return fileFormat;
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
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

}
