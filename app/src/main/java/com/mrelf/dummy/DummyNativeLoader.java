package com.mrelf.dummy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DummyNativeLoader {
	static {
		try {
			// Use the correct filename for your platform (e.g., "libmylibrary.so",
			// "mylibrary.dll")
			String libFileName = "libc++_shared.so";
			InputStream in = DummyNativeLoader.class.getResourceAsStream("/" + libFileName);
			File tempFile = File.createTempFile("mylibrary", ".tmp");

			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				in.transferTo(out);
			}

			System.load(tempFile.getAbsolutePath());
			tempFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
