package jp.banana.planetside2.streaming.event;

import java.util.EventListener;

public interface FacilityControlEvent extends EventListener {
	public void event(String message);
}
