package jp.banana.planetside2.streaming.event;

public interface HeartbeatEvent extends EventListener {

	public void event(String message);

}
