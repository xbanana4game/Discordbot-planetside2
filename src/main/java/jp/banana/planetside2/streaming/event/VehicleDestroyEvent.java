package jp.banana.planetside2.streaming.event;

import jp.banana.planetside2.streaming.entity.VehicleDestroy;

public interface VehicleDestroyEvent extends EventListener{
	public void event(VehicleDestroy vd);
}
