/*
 * Copyright 2012 Kulikov Dmitriy
 * Copyright 2017-2018 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.microedition.lcdui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.shell.AppClassLoader;

import ru.playsoftware.j2meloader.util.PNGUtils;

public class Image {
	private final Bitmap mBitmap;
	private Graphics mGraphics;
	private final Rect mBounds;
	private boolean isBlackWhiteAlpha;

	public Image(Bitmap bitmap) {
		if (bitmap == null) {
			throw new NullPointerException();
		}
		this.mBitmap = bitmap;
		mBounds = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public static Image createImage(int width, int height) {
		return createImage(width, height, Color.WHITE);
	}

	public static Image createImage(int width, int height, int argb) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if (argb != 0) bitmap.eraseColor(argb);
		return new Image(bitmap);
	}

	public static Image createImage(String resname) throws IOException {
		Bitmap b;
		try (InputStream stream = AppClassLoader.getResourceAsStream(null, resname)) {
			if (stream == null) {
				throw new IOException("Can't read image: " + resname);
			}
			b = PNGUtils.getFixedBitmap(stream);
		}
		if (b == null) {
			throw new IOException("Can't decode image: " + resname);
		}
		return new Image(b);
	}

	public static Image createImage(InputStream stream) throws IOException {
		Bitmap b = PNGUtils.getFixedBitmap(stream);
		if (b == null) {
			throw new IOException("Can't decode image");
		}
		return new Image(b);
	}

	public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
		Bitmap b = PNGUtils.getFixedBitmap(imageData, imageOffset, imageLength);
		if (b == null) {
			throw new IllegalArgumentException("Can't decode image");
		}
		return new Image(b);
	}

	public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
		Matrix m = transform == 0 ? null : Sprite.transformMatrix(transform, width / 2.0f, height / 2.0f);
		return new Image(Bitmap.createBitmap(image.mBitmap, x, y, width, height, m, false));
	}

	public static Image createImage(Image source) {
		if (source.isMutable())
			return new Image(Bitmap.createBitmap(source.mBitmap));
		return source;
	}

	public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
		if (!processAlpha) {
			final int length = width * height;
			int[] tmp = new int[length];
			for (int i = 0; i < length; i++) {
				tmp[i] = rgb[i] | 0xFF000000;
			}
			rgb = tmp;
		}
		return new Image(Bitmap.createBitmap(rgb, width, height, Bitmap.Config.ARGB_8888));
	}

	public Graphics getGraphics() {
		return new Graphics(this);
	}

	public boolean isMutable() {
		return mBitmap.isMutable();
	}

	public int getWidth() {
		return mBounds.right;
	}

	public int getHeight() {
		return mBounds.bottom;
	}

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
		mBitmap.getPixels(rgbData, offset, scanlength, x, y, width, height);
	}

	void copyTo(Image dst) {
		dst.getSingleGraphics().getCanvas().drawBitmap(mBitmap, mBounds, mBounds, null);
	}

	void copyTo(Image dst, int x, int y) {
		Rect r = new Rect(x, y, x + mBounds.right, y + mBounds.bottom);
		dst.getSingleGraphics().getCanvas().drawBitmap(mBitmap, mBounds, r, null);
	}

	public Graphics getSingleGraphics() {
		if (mGraphics == null) {
			mGraphics = getGraphics();
		}
		return mGraphics;
	}

	void setSize(int width, int height) {
		mBounds.right = width;
		mBounds.bottom = height;
	}

	public Rect getBounds() {
		return mBounds;
	}

	public boolean isBlackWhiteAlpha() {
		return isBlackWhiteAlpha;
	}

	public void setBlackWhiteAlpha(boolean blackWhiteAlpha) {
		isBlackWhiteAlpha = blackWhiteAlpha;
	}
}
