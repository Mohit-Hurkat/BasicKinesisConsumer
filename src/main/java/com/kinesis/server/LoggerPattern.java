package com.kinesis.server;

/**
 * Created by mohit.hurkat.
 */
public class LoggerPattern {

    public static StringBuilder MPattern() {

        int n = 10;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (int i = 0; i <= n; ++i) {
            stringBuilder.append(letterF(i, n));
            stringBuilder.append(letterL(i, n));
            stringBuilder.append(letterA(i, n));
            stringBuilder.append(letterS(i, n));
            stringBuilder.append(letterH(i, n));
            stringBuilder.append("\n");
        }
        return stringBuilder;

    }

    private static String letterF(int pointer, int totalCount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pointer == 0) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("==");
            }
        } else if (pointer == totalCount / 2) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount / 2; ++i) {
                stringBuilder.append("==");
            }
            for (int i = totalCount / 2; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
        } else {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }


    private static String letterL(int pointer, int totalCount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pointer == totalCount) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("==");
            }
        } else {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private static String letterA(int pointer, int totalCount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pointer == totalCount / 2) {
            for (int i = 0; i < totalCount - pointer; ++i) {
                stringBuilder.append(" ");
            }
            for (int i = 0; i < totalCount / 2 + 1; ++i) {
                stringBuilder.append("~~");
            }
            for (int i = 0; i < totalCount - pointer; ++i) {
                stringBuilder.append(" ");
            }
        } else {
            for (int i = 0; i < totalCount - pointer; ++i) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("/");
            for (int i = totalCount - pointer; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
            stringBuilder.append("\\");
            for (int i = 0; i < totalCount - pointer; ++i) {
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private static String letterS(int pointer, int totalCount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pointer == 0 || pointer == totalCount / 2) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("==");
            }
        } else if (pointer == totalCount) {
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("==");
            }
            stringBuilder.append("|");
        } else if (pointer < totalCount / 2) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
        } else if (pointer > totalCount / 2) {
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
            stringBuilder.append("|");
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private static String letterH(int pointer, int totalCount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pointer == totalCount / 2) {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("==");
            }
            stringBuilder.append("|");
        } else {
            stringBuilder.append("|");
            for (int i = 0; i < totalCount; ++i) {
                stringBuilder.append("  ");
            }
            stringBuilder.append("|");
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }
}
