package sklymchuk.gdx.client;

import com.google.gwt.json.client.*;
import sklymchuk.gdx.Starter;
import sklymchuk.gdx.client.ws.WsEvent;

public class MessageProcessor {
    private final Starter starter;

    public MessageProcessor(Starter starter) {
        this.starter = starter;
    }

    public void processEvent(WsEvent event) {
        final String data = event.getData();
        if (data != null) {
            final JSONValue parsed = JSONParser.parseStrict(data);
            final JSONArray array = parsed.isArray();
            final JSONObject object = parsed.isObject();

            if (array != null) {
                processArray(array);
            } else if (object != null) {
                processObject(object);
            }
        }

    }

    private void processObject(JSONObject object) {
        final JSONValue type = object.get("class");
        if (type != null) {
            switch (type.isString().stringValue()) {
                case "sessionKey":
                    final String meId = object.get("id").isString().stringValue();
                    starter.setMeId(meId);
                    break;
                case "evict":
                    final String evictId = object.get("id").isString().stringValue();
                    starter.evict(evictId);
                    break;
                case "panzer":
                    final float x = (float) object.get("x").isNumber().doubleValue();
                    final float y = (float) object.get("y").isNumber().doubleValue();
                    final float angle = (float) object.get("angle").isNumber().doubleValue();
                    final String id = object.get("id").isString().stringValue();
                    starter.update(id, x, y, angle);
                    break;
                default:
                    throw new RuntimeException("Unknown message type: " + type);
            }
        }

    }

    private void processArray(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            final JSONValue jsonValue = array.get(i);
            final JSONObject object = jsonValue.isObject();

            if (object != null) {
                processObject(object);
            }
        }

    }
}
