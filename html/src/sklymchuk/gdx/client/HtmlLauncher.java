package sklymchuk.gdx.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.user.client.Timer;
import sklymchuk.gdx.Starter;
import sklymchuk.gdx.client.dto.InputStateImpl;
import sklymchuk.gdx.client.ws.EventListenerCallback;
import sklymchuk.gdx.client.ws.WebSocket;


public class HtmlLauncher extends GwtApplication {

    private MessageProcessor messageProcessor;

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                //return new GwtApplicationConfiguration(480, 320);
        }

        private native WebSocket getWebSocket(String url)
                /*-{
                return new WebSocket(url);
                }-*/
        ;

        private native void log(Object obj)
                /*-{
                              console.log(obj);
                 }-*/
        ;

        private native String toJson(Object obj)
                /*-{
                              return JSON.stringify(obj);
                 }-*/
        ;

        @Override
        public ApplicationListener createApplicationListener () {
                final WebSocket client = getWebSocket("ws://localhost:8888/ws");
                final Starter starter = new Starter(new InputStateImpl());
                messageProcessor = new MessageProcessor(starter);

                starter.setMessageSender(
                        message -> client.send(toJson(message)));

            final Timer timer =
                    new Timer() {
                        @Override
                        public void run() {
                            starter.handleTimer();
                        }
                    };

            final EventListenerCallback eventListenerCallback =
                        event -> {
                            messageProcessor.processEvent(event);
                        };

            client.addEventListener("open", event -> {
                timer.scheduleRepeating(1000 / 30);
                messageProcessor.processEvent(event);
                });
                client.addEventListener("close", eventListenerCallback);
                client.addEventListener("error", eventListenerCallback);
                client.addEventListener("message", eventListenerCallback);

                return starter;
        }
}