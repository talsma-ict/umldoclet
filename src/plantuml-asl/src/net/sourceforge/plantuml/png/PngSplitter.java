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
package net.sourceforge.plantuml.png;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.SplitParam;
import net.sourceforge.plantuml.SuggestedFile;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.security.SFile;

public class PngSplitter {

	private final List<SFile> files = new ArrayList<>();

	public PngSplitter(SuggestedFile pngFile, int horizontalPages, int verticalPages, String metadata, int dpi,
			SplitParam splitParam) throws IOException {
		if (horizontalPages == 1 && verticalPages == 1) {
			this.files.add(pngFile.getFile(0));
			return;
		}

		Log.info("Splitting " + horizontalPages + " x " + verticalPages);
		final SFile full = pngFile.getTmpFile(); // SecurityUtils.File(pngFile.getParentFile(), pngFile.getName() + ".tmp");
		// Thread.yield();
		full.delete();
		// Thread.yield();
		final boolean ok = pngFile.getFile(0).renameTo(full);
		// Thread.yield();
		if (ok == false) {
			throw new IOException("Cannot rename");
		}

		// Thread.yield();
		final BufferedImage im = SImageIO.read(full);
		// Thread.yield();
		final PngSegment horizontalSegment = new PngSegment(im.getWidth(), horizontalPages);
		final PngSegment verticalSegment = new PngSegment(im.getHeight(), verticalPages);

		int x = 0;
		for (int i = 0; i < horizontalPages; i++) {
			for (int j = 0; j < verticalPages; j++) {
				final SFile f = pngFile.getFile(x++);
				this.files.add(f);
				final int width = horizontalSegment.getLen(i);
				final int height = verticalSegment.getLen(j);
				BufferedImage piece = im.getSubimage(horizontalSegment.getStart(i), verticalSegment.getStart(j), width,
						height);
				if (splitParam.isSet()) {
					BufferedImage withMargin = new BufferedImage(width + 2 * splitParam.getExternalMargin(), height + 2
							* splitParam.getExternalMargin(), BufferedImage.TYPE_INT_ARGB);
					final Graphics2D g2d = withMargin.createGraphics();
					if (splitParam.getExternalColor() != null) {
						g2d.setColor(splitParam.getExternalColor());
						g2d.fillRect(0, 0, withMargin.getWidth(), withMargin.getHeight());
					}
					g2d.drawImage(piece, splitParam.getExternalMargin(), splitParam.getExternalMargin(), null);

					if (splitParam.getBorderColor() != null) {
						g2d.setColor(splitParam.getBorderColor());
						g2d.drawRect(splitParam.getExternalMargin() - 1, splitParam.getExternalMargin() - 1,
								piece.getWidth() + 1, piece.getHeight() + 1);
					}

					piece = withMargin;

					g2d.dispose();
				}
				// Thread.yield();
				PngIO.write(piece, f, metadata, dpi);
				// Thread.yield();
			}
		}

		full.delete();
		Log.info("End of splitting");
	}

	public List<SFile> getFiles() {
		return Collections.unmodifiableList(files);
	}

}
