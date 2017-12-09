package se.pingstteknik.propresenter.stagedisplayviewer.transition;

import javafx.scene.layout.StackPane;

/**
 * Not an interface because I need a common ancestor for FadePane and Node.
 * @author levimiller
 *
 */
public abstract class FadePane extends StackPane {
	/**
	 * Sets the text, and applies a fade animation.
	 * @param text
	 */
	public abstract void setText(String text);
}
