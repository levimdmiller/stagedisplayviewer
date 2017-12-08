package se.pingstteknik.propresenter.stagedisplayviewer.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.RemoveLinesAfterEmptyLineTranslator;


/**
 * @author danielkihlgren
 * @version 1.2.0
 * @since 1.2.0
 */
public class RemoveLinesAfterEmptyLineTest {

    private final RemoveLinesAfterEmptyLineTranslator transformer = new RemoveLinesAfterEmptyLineTranslator();

    @Test
    public void testOneLine() throws Exception {
        assertThat(transformer.transform("", null), is(""));
        assertThat(transformer.transform("\n", null), is(""));
        assertThat(transformer.transform("first line", null), is("first line"));
    }

    @Test
    public void testTwoLines() throws Exception {
        assertThat(transformer.transform("\n", null), is(""));
        assertThat(transformer.transform("First Line\nSecond Line", null), is("First Line\nSecond Line"));
        assertThat(transformer.transform("First Line\n", null), is("First Line"));
        assertThat(transformer.transform("\nSecond Line", null), is(""));
    }

    @Test
    public void testThreeLines() throws Exception {
        assertThat(transformer.transform("First Line\nSecond Line\nThird Line", null), is("First Line\nSecond Line\nThird Line"));
        assertThat(transformer.transform("First Line\n\nThird Line", null), is("First Line"));
    }
}