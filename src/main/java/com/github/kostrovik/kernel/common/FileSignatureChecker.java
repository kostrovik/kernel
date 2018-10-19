package com.github.kostrovik.kernel.common;

import com.github.kostrovik.kernel.dictionaries.FileSignatureDictionary;
import com.github.kostrovik.kernel.settings.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-10-05
 * github:  https://github.com/kostrovik/kernel
 */
public class FileSignatureChecker {
    private static Logger logger = Configurator.getConfig().getLogger(FileSignatureChecker.class.getName());
    private FileSignatureDictionary[] signatures;

    public FileSignatureChecker() {
        this.signatures = FileSignatureDictionary.values();
    }

    public String getExtension(File file) {
        try {
            return getExtension(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, String.format("Не найден файл: %s", file.getPath()), e);
        }
        return "";
    }

    public String getExtension(FileInputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            return "";
        }

        FileSignatureDictionary result = null;
        try (inputStream) {
            int bufferSize = (inputStream.available() > 255) ? 256 : inputStream.available();
            byte[] buffer = new byte[bufferSize];
            if (inputStream.read(buffer, 0, bufferSize) > 0) {
                result = getFileSignature(buffer);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка ввода вывода.", e);
        }
        return Objects.isNull(result) ? "" : result.getExtension();
    }

    private FileSignatureDictionary getFileSignature(byte[] buffer) {
        FileSignatureDictionary result = null;

        for (FileSignatureDictionary signature : signatures) {
            for (int[] pattern : signature.getSignature()) {
                boolean match = true;
                for (int i = 0; i < pattern.length; i++) {
                    if ((buffer[i] & 0xFF) != pattern[i]) {
                        match = false;
                    }
                }

                if (match) {
                    result = signature;
                }
            }
        }

        return result;
    }
}
