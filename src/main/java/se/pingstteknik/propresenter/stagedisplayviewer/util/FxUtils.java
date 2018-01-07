package se.pingstteknik.propresenter.stagedisplayviewer.util;

import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.FONT_FAMILY;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.MARGIN_BOTTOM;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.MAX_FONT_SIZE;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.OUTPUT_SCREEN;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.OUTPUT_WIDTH_PERCENTAGE;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.TEXT_SHADOW_SPREAD;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import se.pingstteknik.propresenter.stagedisplayviewer.config.Property;

public class FxUtils {
    private static final Logger log = LoggerFactory.getLogger(FxUtils.class);

    public Text createLowerKey() {
        Text lowerKey = new Text();
        lowerKey.setFont(Font.font(FONT_FAMILY.toString(), FontWeight.MEDIUM, MAX_FONT_SIZE.toInt()));
        lowerKey.setFill(Color.WHITE);
        lowerKey.setWrappingWidth(getWrappingWidth());
        lowerKey.setTextAlignment(getAlignment());
        lowerKey.getStyleClass().add("display-text");
        DropShadow ds = new DropShadow();
        ds.setOffsetY(0.0);
        ds.setOffsetX(0.0);
        ds.setColor(Color.BLACK);
        ds.setSpread(TEXT_SHADOW_SPREAD.toDouble());
        ds.setBlurType(BlurType.GAUSSIAN);
        lowerKey.setEffect(ds);
        return lowerKey;
    }
    
    /**
     * Converts the String property TextAlignment to the appropriate TextAlignment.
     * @return
     */
    private TextAlignment getAlignment() {
	    	try {
	    		return TextAlignment.valueOf(Property.TEXT_ALIGN.toString().toUpperCase());
	    	} catch(IllegalArgumentException e) {
	    		log.warn(String.format(
					"Invalid TEXT_ALIGN property: %s. It should be one of (Case insensitive): Center, Right, Left, or Justify.",
					Property.TEXT_ALIGN.toString()
					), e
				);
	    		// Default to center align.
	    		return TextAlignment.CENTER;
	    	}
    }
    
    /**
     * Converts the String property VerticalAlignment to the appropriate VerticalAlignment.
     * @return
     */
    private Pos getVerticalAlignment() {
	    	try {
	    		String key = Property.VERTICAL_ALIGN.toString().toUpperCase();
	    		key = "CENTER".equals(key) ? key : key + "_CENTER";
	    		return Pos.valueOf(key);
	    	} catch(IllegalArgumentException e) {
	    		log.warn(String.format(
					"Invalid VERTICAL_ALIGN property: %s. It should be one of (Case insensitive): Center, Right, Left, or Justify.",
					Property.VERTICAL_ALIGN.toString()
					), e
				);
	    		// Default to center align.
	    		return Pos.BOTTOM_CENTER;
	    	}
    }

    public Scene createScene(Node lowerKey) {
        Rectangle2D bounds = getScreenBounds();
        Scene scene = new Scene(createRoot(lowerKey), bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().add("styles.css");
        scene.getStylesheets().add("file:///" + new File("styles.css").getAbsolutePath().replace("\\", "/"));
        return scene;
    }

    public void startOnCorrectScreen(Stage stage) {
        Rectangle2D visualBounds = getScreen().getVisualBounds();
        stage.setX(visualBounds.getMinX() + 100);
        stage.setY(visualBounds.getMinY() + 100);
    }

    private double getWrappingWidth() {
        return getScreenBounds().getWidth() * outputWidthPercentage();
    }

    private Rectangle2D getScreenBounds() {
    	// Uses property width/height if specified, or defaults to screen bounds.
    	Rectangle2D screen = getScreen().getBounds();
    	double width = Property.WIDTH.toDouble();
    	double height = Property.HEIGHT.toDouble();
    	return new Rectangle2D(screen.getMinX(), screen.getMinY(), 
			width > 0 ? width : screen.getWidth(), 
			height > 0 ? height : screen.getHeight()
		);
    }

    private Screen getScreen() {
        return OUTPUT_SCREEN.toInt() <= Screen.getScreens().size()
                ? Screen.getScreens().get(OUTPUT_SCREEN.toInt()-1)
                : Screen.getPrimary();
    }

    private double outputWidthPercentage() {
        return 0.01 * (double) OUTPUT_WIDTH_PERCENTAGE.toInt();
    }

    private GridPane createRoot(Node lowerKey) {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setAlignment(getVerticalAlignment());
        root.setPadding(new Insets(10, 10, MARGIN_BOTTOM.toInt(), 10));
        root.add(lowerKey, 0, 0, 2, 1);
        return root;
    }
}
