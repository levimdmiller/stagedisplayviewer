package se.pingstteknik.propresenter.stagedisplayviewer.runner;

import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.HOST;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.PASSWORD;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.PORT;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.PRESERVE_TWO_LINES;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.REMOVE_LINES_AFTER_EMPTY_LINE;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.RESPONSE_TIME_MILLIS;
import static se.pingstteknik.propresenter.stagedisplayviewer.config.Property.TEXT_TRANSLATOR_ACTIVE;
import static se.pingstteknik.propresenter.stagedisplayviewer.util.ThreadUtil.sleep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.util.Duration;
import se.pingstteknik.propresenter.stagedisplayviewer.config.Property;
import se.pingstteknik.propresenter.stagedisplayviewer.model.StageDisplay;
import se.pingstteknik.propresenter.stagedisplayviewer.util.FxTextUtils;
import se.pingstteknik.propresenter.stagedisplayviewer.util.Logger;
import se.pingstteknik.propresenter.stagedisplayviewer.util.LoggerFactory;
import se.pingstteknik.propresenter.stagedisplayviewer.util.MidiModule;
import se.pingstteknik.propresenter.stagedisplayviewer.util.XmlDataReader;
import se.pingstteknik.propresenter.stagedisplayviewer.util.XmlParser;
import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.CapitalizeRowsTranslator;
import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.ConcatenateRowsTranslator;
import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.CustomNewLineTranslator;
import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.RemoveLinesAfterEmptyLineTranslator;
import se.pingstteknik.propresenter.stagedisplayviewer.util.translator.Translator;

/**
 * @author Daniel Kihlgren
 * @version 1.6.0
 * @since 1.0.0
 */
public class LowerKeyHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(LowerKeyHandler.class);
    private static final FxTextUtils  fxTextUtils = new FxTextUtils();
    private static final XmlDataReader xmlDataReader = new XmlDataReader();
    private static final XmlParser xmlParser = new XmlParser();
    private static final String SUCCESSFUL_LOGIN = "<StageDisplayLoginSuccess />";
    private static final String SUCCESSFUL_LOGIN_WINDOWS = "<StageDisplayLoginSuccess>";

    private volatile boolean running = true;
    private volatile boolean activeConnection = true;
    private final Text lowerKey;
    private final MidiModule midiModule;
    private final FadeTransition fadeOut, fadeIn;
    private final Translator[] translators;

    public LowerKeyHandler(Text lowerKey, MidiModule midiModule) throws IOException {
    	// Conditionally add transformers in order.
    	// Saves checking this condition every iteration.
    	// Could probably refactor to use dependency injection.
    	ArrayList<Translator> t = new ArrayList<>();
    	if(REMOVE_LINES_AFTER_EMPTY_LINE.isTrue())
    		t.add(new RemoveLinesAfterEmptyLineTranslator());
    	t.add(new CustomNewLineTranslator());
    	if(TEXT_TRANSLATOR_ACTIVE.isTrue())
    		t.add(new ConcatenateRowsTranslator(PRESERVE_TWO_LINES.isTrue()));
    	if(Property.CAPITALIZE_LINES.isTrue())
    		t.add(new CapitalizeRowsTranslator());
    	
    	translators = t.toArray(new Translator[t.size()]);
        this.lowerKey = lowerKey;
        this.midiModule = midiModule;
        
        // Initialize fade animations for updating stage display text.
        fadeOut = new FadeTransition(Duration.millis(Property.FADE_TIME.toInt()), lowerKey);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeIn = new FadeTransition(Duration.millis(Property.FADE_TIME.toInt()), lowerKey);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
    }

    @Override
    public void run() {
        while (running) {
            try (Socket socket = new Socket(HOST.toString(), PORT.toInt())) {
                try (
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
                ) {
                    activeConnection = true;
                    log.info("Connection to propresenter established at " + HOST.toString() + ":" + PORT.toString());
                    out.println(getLoginString());

                    String loginResponse = in.readLine();
                    if (SUCCESSFUL_LOGIN.equals(loginResponse) || SUCCESSFUL_LOGIN_WINDOWS.equals(loginResponse)) {
                        log.info("Login succeeded");
                    } else {
                        log.error("Login failed with incorrect password: " + PASSWORD.toString() + ", with response: " + loginResponse);
                        running = false;
                    }
                    while (running && activeConnection && socket.isConnected()) {
                        activeConnection = update(in);
                        sleep(RESPONSE_TIME_MILLIS.toInt());
                    }
                    log.info("Connection lost");
                }
            } catch (IOException e) {
                log.error("Connection to propresenter failed at " + HOST.toString() + ":" + PORT.toString(), e);
            }
            sleep(500);
        }

        log.info("Closing program");
        Platform.exit();
    }

    public void terminate() {
        log.info("Program is closing");
        running = false;
    }

    private boolean update(BufferedReader in) throws IOException {
        String xmlRawData = xmlDataReader.readXmlData(in);
        if (xmlRawData == null) {
            lowerKey.setText(" ");
            return false;
        }

        StageDisplay stageDisplay = xmlParser.parse(xmlRawData);
        String slideText = stageDisplay.getData("CurrentSlide");
        String slideNotes = stageDisplay.getData("CurrentSlideNotes");

        log.info("RAW XML: {}", xmlRawData);
        log.debug("Slide notes: {}", slideNotes);
        log.debug("Slide text unparsed: {}", slideText);

        if (!slideText.isEmpty()) {
        	for(Translator t : translators)
        		t.transform(slideText, slideNotes);
            lowerKey.setFont(fxTextUtils.getOptimizedFont(slideText, lowerKey.getWrappingWidth()));
            
            // Play the fade out/in animation if the slide text changes.
            if(!lowerKey.getText().equals(slideText)) {
	            final String text = slideText; // needs to be final for event handler.
	            fadeOut.setOnFinished(e -> {
	                lowerKey.setText(text);
	                fadeIn.play();
	            });
	            fadeOut.play();
            } else {
            	// Make sure initial text is displayed.
            	lowerKey.setText(slideText);
            }
            log.debug("Slide text parsed: {}", slideText);
        } else {
            lowerKey.setText(" ");
        }
        midiModule.handleMessage(slideNotes);
        return true;
    }

    private String getLoginString() {
        return "<StageDisplayLogin>" + Property.PASSWORD + "</StageDisplayLogin>\n\r";
    }
}
