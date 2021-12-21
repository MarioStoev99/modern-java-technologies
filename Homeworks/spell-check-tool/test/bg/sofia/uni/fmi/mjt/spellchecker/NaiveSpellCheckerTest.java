package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NaiveSpellCheckerTest {

    private static NaiveSpellChecker naiveSpellChecker;

    @BeforeClass
    public static void setUp() {
        String dictionary = "family\n" +
                "Football\n" +
                "football\n" +
                "various\n" +
                "variety\n" +
                "common\n" +
                "team\n" +
                "involves\n" +
                "varying\n" +
                "degree\n" +
                "degrees\n" +
                "kicking\n" +
                "score\n" +
                "goal\n" +
                "Unqualified\n" +
                "normally\n" +
                "word\n" +
                "normally\n" +
                "means\n" +
                "form\n" +
                "popular\n" +
                "used\n" +
                "sports\n" +
                "involve\n" +
                "ball";

        String stopwords = "a\n" +
                "about\n" +
                "above\n" +
                "after\n" +
                "again\n" +
                "against\n" +
                "all\n" +
                "am\n" +
                "an\n" +
                "and\n" +
                "any\n" +
                "are\n" +
                "aren't\n" +
                "as\n" +
                "at\n" +
                "be\n" +
                "because\n" +
                "been\n" +
                "before\n" +
                "being\n" +
                "below\n" +
                "between\n" +
                "both\n" +
                "but\n" +
                "by\n" +
                "can't\n" +
                "cannot\n" +
                "could\n" +
                "couldn't\n" +
                "did\n" +
                "didn't\n" +
                "do\n" +
                "does\n" +
                "doesn't\n" +
                "doing\n" +
                "don't\n" +
                "down\n" +
                "during\n" +
                "each\n" +
                "few\n" +
                "for\n" +
                "from\n" +
                "further\n" +
                "had\n" +
                "hadn't\n" +
                "has\n" +
                "hasn't\n" +
                "have\n" +
                "haven't\n" +
                "having\n" +
                "he\n" +
                "he'd\n" +
                "he'll\n" +
                "he's\n" +
                "her\n" +
                "here\n" +
                "here's\n" +
                "hers\n" +
                "herself\n" +
                "him\n" +
                "himself\n" +
                "his\n" +
                "how\n" +
                "how's\n" +
                "i\n" +
                "i'd\n" +
                "i'll\n" +
                "i'm\n" +
                "i've\n" +
                "if\n" +
                "in\n" +
                "into\n" +
                "is\n" +
                "isn't\n" +
                "it\n" +
                "it's\n" +
                "its\n" +
                "itself\n" +
                "let's\n" +
                "me\n" +
                "more\n" +
                "most\n" +
                "mustn't\n" +
                "my\n" +
                "myself\n" +
                "no\n" +
                "nor\n" +
                "not\n" +
                "of\n" +
                "off\n" +
                "on\n" +
                "once\n" +
                "only\n" +
                "or\n" +
                "other\n" +
                "ought\n" +
                "our\n" +
                "ours\n" +
                "ourselves\n" +
                "out\n" +
                "over\n" +
                "own\n" +
                "same\n" +
                "shan't\n" +
                "she\n" +
                "she'd\n" +
                "she'll\n" +
                "she's\n" +
                "should\n" +
                "shouldn't\n" +
                "so\n" +
                "some\n" +
                "such\n" +
                "than\n" +
                "that\n" +
                "that's\n" +
                "the\n" +
                "their\n" +
                "theirs\n" +
                "them\n" +
                "themselves\n" +
                "then\n" +
                "there\n" +
                "there's\n" +
                "these\n" +
                "they\n" +
                "they'd\n" +
                "they'll\n" +
                "they're\n" +
                "they've\n" +
                "this\n" +
                "those\n" +
                "through\n" +
                "to\n" +
                "too\n" +
                "under\n" +
                "until\n" +
                "up\n" +
                "very\n" +
                "was\n" +
                "wasn't\n" +
                "we\n" +
                "we'd\n" +
                "we'll\n" +
                "we're\n" +
                "we've\n" +
                "were\n" +
                "weren't\n" +
                "what\n" +
                "what's\n" +
                "when\n" +
                "when's\n" +
                "where\n" +
                "where's\n" +
                "which\n" +
                "while\n" +
                "who\n" +
                "who's\n" +
                "whom\n" +
                "why\n" +
                "why's\n" +
                "with\n" +
                "won't\n" +
                "would\n" +
                "wouldn't\n" +
                "you\n" +
                "you'd\n" +
                "you'll\n" +
                "you're\n" +
                "you've\n" +
                "your\n" +
                "yours\n" +
                "yourself\n" +
                "yourselves";

        Reader dictionaryReader = new StringReader(dictionary);
        Reader stopWordsReader = new StringReader(stopwords);
        naiveSpellChecker = new NaiveSpellChecker(dictionaryReader, stopWordsReader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMetadataNull() {
        naiveSpellChecker.metadata(null);
    }

    @Test
    public void testMetadataSuccess() {
        String text = "Football is a family of team sports that involve, to varying degrees, kicking a ball to score a goal. Unqualified,\n" +
                "the word football normally means the form of football that is the most popular where the word is used.";
        Reader reader = new StringReader(text);
        Metadata actual = naiveSpellChecker.metadata(reader);
        Metadata expected = new Metadata(179, 21, 0);
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsNull() {
        naiveSpellChecker.findClosestWords(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsPassNegativeNumberAsArgument() {
        naiveSpellChecker.findClosestWords("oxoboxo", -1);
    }

    @Test
    public void testFindClosestWordsSuccess() {
        List<String> actual = naiveSpellChecker.findClosestWords("degre", 2);

        List<String> expected = new ArrayList<>();
        expected.add("degree");
        expected.add("degrees");

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeTryReaderWithNullAsArgument() {
        naiveSpellChecker.analyze(null, new StringWriter(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeTryWriterWithNullAsArgument() {
        naiveSpellChecker.analyze(new StringReader(""), null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeNegativeDigit() {
        naiveSpellChecker.analyze(new StringReader(""), new StringWriter(), -1);
    }

    @Test
    public void testAnalyzeSuccess() {
        String text = "Football is a famil of team sports that involve, to varying degre, kicking a ball to score a goal. Unqualified,\n" +
                "the word football normally means the form of football that is the most popula where the word is used.";
        Writer writer = new StringWriter();
        naiveSpellChecker.analyze(new StringReader(text), writer, 2);
        String expected = "Football is a famil of team sports that involve, to varying degre, kicking a ball to score a goal. Unqualified,the word football normally means the form of football that is the most popula where the word is used.\n" +
                "===Metadata===\n" +
                "175 characters, 18 words, 3 spelling issue(s) found\n" +
                "===Findings===\n" +
                "Line #1,{famil}- Possible suggestions are {family, team}\n" +
                "Line #2,{popula}- Possible suggestions are {popular, sports}\n" +
                "Line #1,{degre}- Possible suggestions are {degree, degrees}\n";
        assertEquals(expected, writer.toString());
    }

    @Test
    public void testFindClosestWordsWhenNisGreaterThanDictionarySize() {
        List<String> actualClosestWords = naiveSpellChecker.findClosestWords("Footbal", 50);
        List<String> expectedClosestWords = List.of("Football", "football", "ball", "goal", "normally", "Unqualified", "popular");

        assertArrayEquals(expectedClosestWords.toArray(), actualClosestWords.toArray());
    }

    @Test
    public void testMetadataEmptyReader() {
        Metadata actualMetadata = naiveSpellChecker.metadata(new StringReader(""));

        assertTrue(new Metadata(0, 0, 0).equals(actualMetadata));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsEmptyString() {
        naiveSpellChecker.findClosestWords("", 50);
    }

    @Test
    public void testMetadataWithOneWord() {
        String text = "football";
        Metadata actualMetadata = naiveSpellChecker.metadata(new StringReader(text));

        assertTrue(new Metadata(8, 1, 0).equals(actualMetadata));
    }

    @Test
    public void testMetadataWithTwoWords() {
        String text = "football football";
        Metadata actualMetadata = naiveSpellChecker.metadata(new StringReader(text));

        assertTrue(new Metadata(16, 2, 0).equals(actualMetadata));
    }

    @Test
    public void testAnalyzeEmptyReader() {
        Writer writer = new StringWriter();
        naiveSpellChecker.analyze(new StringReader(""), writer, 2);
        String expected = "\n" +
                "===Metadata===\n" +
                "0 characters, 0 words, 0 spelling issue(s) found\n" +
                "===Findings===\n";
        assertEquals(expected, writer.toString());
    }
}

