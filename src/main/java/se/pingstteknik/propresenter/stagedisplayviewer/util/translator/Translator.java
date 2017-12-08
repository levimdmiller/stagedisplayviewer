package se.pingstteknik.propresenter.stagedisplayviewer.util.translator;

/**
 * Defines a transformation for the text.
 * @author levimiller
 *
 */
public interface Translator {
	/**
	 * Transforms the given text.
	 * @param text - text to transform
	 * @param notes - notes
	 * @return
	 */
	public String transform(String currentSlideText, String notes);
}
