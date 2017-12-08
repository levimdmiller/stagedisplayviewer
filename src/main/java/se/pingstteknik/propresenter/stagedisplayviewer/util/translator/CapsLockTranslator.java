package se.pingstteknik.propresenter.stagedisplayviewer.util.translator;

public class CapsLockTranslator implements Translator {

	@Override
	public String transform(String currentSlideText, String notes) {
		return currentSlideText.toUpperCase();
	}

}
