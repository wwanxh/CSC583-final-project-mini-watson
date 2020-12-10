package edu.arizona.cs;

import java.util.regex.Pattern;

public class Preferences {
    public static final String TRAIN_DATA_SET_PATH = "wiki-training-data";
    public static final String TEST_DATA_PATH = "questions.txt";
    public static final String INDEX_FILE_PATH = "src/main/resources/index-7.7.1";

    public static final String PAGE_TITLE_REGEX = "\\[\\[.*\\]\\]\\n?";
    public static final Pattern PAGE_TITLE_MATCHER = Pattern.compile(PAGE_TITLE_REGEX);
    public static final String PAGE_SUBTITLE_REGEX = "\\=\\=.*\\=\\=\\n?";
    public static final Pattern PAGE_SUBTITLE_MATCHER = Pattern.compile(PAGE_SUBTITLE_REGEX);

    public static final int LEMMATIZATION_THREAD_NUMBER = 14;

    public static final String URL_DETECTOR_REGEX = "https?://\\S+\\s?";
    public static final String NON_ASCII_DETECTOR_REGEX = "\\P{Print}";
    // Remove Punctuation
    public static final String PUNCT_DETECTOR_REGEX = "[^a-zA-Z ]";
}
