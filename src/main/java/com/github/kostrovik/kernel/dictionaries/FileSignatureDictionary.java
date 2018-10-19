package com.github.kostrovik.kernel.dictionaries;

/**
 * project: kernel
 * author:  kostrovik
 * date:    2018-10-05
 * github:  https://github.com/kostrovik/kernel
 */
public enum FileSignatureDictionary {
    PNG() {
        public int[][] getSignature() {
            return new int[][]{{0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}};
        }

        public String getExtension() {
            return "png";
        }
    },
    PDF() {
        public int[][] getSignature() {
            return new int[][]{{0x25, 0x50, 0x44, 0x46}};
        }

        public String getExtension() {
            return "pdf";
        }
    },
    JPEG() {
        public int[][] getSignature() {
            return new int[][]{
                    {0xFF, 0xD8, 0xFF, 0xE0},
                    {0xFF, 0xD8, 0xFF, 0xE1},
                    {0xFF, 0xD8, 0xFF, 0xE2},
                    {0xFF, 0xD8, 0xFF, 0xE3},
                    {0xFF, 0xD8, 0xFF, 0xE8},
            };
        }

        public String getExtension() {
            return "jpeg";
        }
    };

    public abstract int[][] getSignature();

    public abstract String getExtension();
}
