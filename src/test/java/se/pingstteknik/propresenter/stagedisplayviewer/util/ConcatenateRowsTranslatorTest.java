package se.pingstteknik.propresenter.stagedisplayviewer.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.ConcatenateRowsTranslator;

/**
 * @author danielkihlgren
 */
public class ConcatenateRowsTranslatorTest {
    private final ConcatenateRowsTranslator translator = new ConcatenateRowsTranslator(true);

    @Test
    public void twoRowsShouldBePreservedByDefault() throws Exception {
        ConcatenateRowsTranslator translator = new ConcatenateRowsTranslator(true);
        assertThat(translator.transform("rad 1\nrad 2", null), is("rad 1\nrad 2"));
    }

    @Test
    public void twoRowsShouldBeTransformedIntoOneWhenPreserveTwoLinesAreDisabled() throws Exception {
        ConcatenateRowsTranslator translator = new ConcatenateRowsTranslator(false);
        assertThat(translator.transform("rad 1\nrad 2", null), is("rad 1 rad 2"));
    }

    @Test
    public void threeRowsShouldBeTransformedIntoTwo() throws Exception {
        assertThat(translator.transform("rad 1\nrad 2\nrad 3", null), is("rad 1 rad 2\nrad 3"));
    }

    @Test
    public void fourRowsShouldBeTransformedIntoTwo() throws Exception {
        assertThat(translator.transform("rad 1\nrad 2\nrad 3\nrad 4", null), is("rad 1 rad 2\nrad 3 rad 4"));
    }

    @Test
    public void spacesShouldBeTrimmed() throws Exception {
        assertThat(translator.transform("  rad 1  \n  rad 2  \n  rad 3  \n  rad 4  ", null), is("rad 1 rad 2\nrad 3 rad 4"));
    }

}