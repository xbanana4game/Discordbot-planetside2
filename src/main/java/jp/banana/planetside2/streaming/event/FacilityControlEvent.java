package jp.banana.planetside2.streaming.event;

import jp.banana.planetside2.streaming.entity.FacilityControl;


public interface FacilityControlEvent extends EventListener {
	public void event(FacilityControl message);
}
