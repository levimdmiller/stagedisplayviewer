package se.pingstteknik.propresenter.stagedisplayviewer.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.CapitalizeRowsTranslator;

/**
 * @author danielkihlgren
 */
public class CapitalizeRowsTranslatorTest {

    @Test
    public void internationalCharactersShouldBeCapitalizedCorrectly() throws Exception {
        assertThat(new CapitalizeRowsTranslator().transform("å\nä\nö", null), is("Å\nÄ\nÖ"));
    }

}