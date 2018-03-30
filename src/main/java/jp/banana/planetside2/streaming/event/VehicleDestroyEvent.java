package jp.banana.planetside2.streaming.event;

import java.util.EventListener;

public interface VehicleDestroyEvent extends EventListener{
	public void event(String message);
}
