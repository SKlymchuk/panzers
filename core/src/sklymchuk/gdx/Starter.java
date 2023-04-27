package sklymchuk.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;

public class Starter extends ApplicationAdapter {
	SpriteBatch batch;
	private String meId;
	private final ObjectMap<String, Panzer> panzers = new ObjectMap<>();
	private final KeyboardAdapter inputProcessor;
	private MessageSender messageSender;

	public Starter(InputState inputState) {
		this.inputProcessor = new KeyboardAdapter(inputState);
	}

	@Override
	public void create () {
		Gdx.input.setInputProcessor(inputProcessor);
		batch = new SpriteBatch();
		Panzer me = new Panzer(100, 300);
		panzers.put(meId, me);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.52f, 0.6f, 0.52f, 1);
		batch.begin();
		for (String key : panzers.keys()) {
		    panzers.get(key).render(batch);
		}
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (Panzer panzer : panzers.values()) {
		  panzer.dispose();
		}
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public void handleTimer() {
		if (inputProcessor != null && !panzers.isEmpty()) {
			final Panzer me = panzers.get(meId);
			final InputState inputState = inputProcessor.updateAndGetInputState(me.getOrigin());
			messageSender.send(inputState);
		}
	}

	public void setMeId(String id) {
		this.meId = id;
	}

	public void evict(String id) {
		panzers.remove(id);
	}

	public void update(String id, float x, float y, float angle) {
		if (panzers.isEmpty()) {
			return;
		}

		Panzer panzer = panzers.get(id);
		if (panzer == null) {
			panzer = new Panzer(x, y, "black_panz.png");
			panzers.put(id, panzer);
		} else {
			panzer.moveTo(x, y);
		}

		panzer.rotateTo(angle);

	}
}
