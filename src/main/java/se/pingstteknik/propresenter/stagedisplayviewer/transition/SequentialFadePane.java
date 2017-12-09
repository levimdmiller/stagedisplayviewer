package se.pingstteknik.propresenter.stagedisplayviewer.transition;

import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import se.pingstteknik.propresenter.stagedisplayviewer.runner.LowerKeyHandler;
import se.pingstteknik.propresenter.stagedisplayviewer.util.FxTextUtils;
import se.pingstteknik.propresenter.stagedisplayviewer.util.Logger;
import se.pingstteknik.propresenter.stagedisplayviewer.util.LoggerFactory;

/**
 * Creates a Pane that has a sequential fade transition whenever the text is changed.
 * (I.e., fade out, then fade in).
 * @author levimiller
 *
 */
public class SequentialFadePane extends FadePane{
	private static final Logger log = LoggerFactory.getLogger(LowerKeyHandler.class);
    private static final FxTextUtils  fxTextUtils = new FxTextUtils();
    private final FadeTransition fadeOut, fadeIn;
	Text child;
	
	/**
	 * Creates a Pane that has a cross fade transition whenever the text is changed.
	 * @param front - Node to display.
	 * @param copy - duplicate of front.
	 */
	public SequentialFadePane(Text child, int duration) {
		this.getChildren().add(child);
		this.child = child;
		 // Initialize fade animations for updating stage display text.
        fadeOut = new FadeTransition(Duration.millis(duration), child);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeIn = new FadeTransition(Duration.millis(duration),child);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
	}
	
	@Override
	public void setText(String text) {
		// Minor code reuse :(
		child.setFont(fxTextUtils.getOptimizedFont(text, child.getWrappingWidth()));
        
		// Change text and animate.
		fadeOut.setOnFinished(e -> {
            child.setText(text);
            fadeIn.play();
        });
        fadeOut.play();
	}
}
