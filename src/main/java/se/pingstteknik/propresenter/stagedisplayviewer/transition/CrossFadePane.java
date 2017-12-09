package se.pingstteknik.propresenter.stagedisplayviewer.transition;


import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import se.pingstteknik.propresenter.stagedisplayviewer.runner.LowerKeyHandler;
import se.pingstteknik.propresenter.stagedisplayviewer.util.FxTextUtils;
import se.pingstteknik.propresenter.stagedisplayviewer.util.Logger;
import se.pingstteknik.propresenter.stagedisplayviewer.util.LoggerFactory;

/**
 * Contains two identical Text elements, and toggles their visibility using a FadeTransition
 * In order to achieve a cross fade transition.
 * @author levimiller
 *
 */
public class CrossFadePane extends FadePane {
    private static final Logger log = LoggerFactory.getLogger(LowerKeyHandler.class);
    private static final FxTextUtils  fxTextUtils = new FxTextUtils();
	Text[] children;
	int curr, duration;
	
	/**
	 * Creates a Pane that has a cross fade transition whenever the text is changed.
	 * @param front - Node to display.
	 * @param copy - duplicate of front.
	 */
	public CrossFadePane(Text front, Text copy, int duration) {
		children = new Text[] {
			front, copy
		};
		this.getChildren().add(front);
		this.getChildren().add(copy);
		curr = 0;
		this.duration = duration;
	}
	
	private Text get(int i) {
		return children[i % children.length];
	}
	
	/**
	 * Sets the text displayed, after a cross fade animation.
	 * @param text
	 */
	public void setText(String text) {
		log.info("Setting text...");
		Text front = get(curr);
		Text back = get(curr + 1); // hidden node
		
		// Only play transition if the text changes.
		if(!front.getText().equals(text)) {
			// Set up transitions.
		    FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), back);
		    fadeIn.setFromValue(0.0);
		    fadeIn.setToValue(1.0);
		    FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), front);
		    fadeOut.setFromValue(1.0);
		    fadeOut.setToValue(0.0);
		    
		    // update the text.
		    back.setText(text);
		    
		    // Update the font to accommodate the text changes.
		    for(Text c : children)
		    		c.setFont(fxTextUtils.getOptimizedFont(text, c.getWrappingWidth()));
		    log.info("Starting animations.");
		    fadeIn.play();
		    fadeOut.play();
			curr++;
		}
	}
}
