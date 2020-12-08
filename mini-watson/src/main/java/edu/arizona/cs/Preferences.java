package edu.arizona.cs;

import java.util.regex.Pattern;

public class Preferences {
    public static final String TRAIN_DATA_SET_PATH = "wiki-training-data";
    public static final String TEST_DATA_PATH = "questions.txt";
    public static final String INDEX_FILE_PATH = "src/main/resources/index";

    public static final String PAGE_TITLE_REGEX = "\\[\\[.*\\]\\]\\n?";
    public static final Pattern PAGE_TITLE_MATCHER = Pattern.compile(PAGE_TITLE_REGEX);
    public static final String PAGE_SUBTITLE_REGEX = "\\=\\=.*\\=\\=\\n?";
    public static final Pattern PAGE_SUBTITLE_MATCHER = Pattern.compile(PAGE_SUBTITLE_REGEX);

    public static final int LEMMATIZATION_THREAD_NUMBER = 14;
}
