package site.hanschen.runwithyou.eventbus;

import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

import site.hanschen.runwithyou.service.RunnerCallback;

/**
 * 分发应用事件，可通过{@link #registerStepCallback(Object, OnStepCallback)}注册对{@link OnStepCallback}的监听
 * 注意, 请在适当的时机调用{@link #unregisterCallback(Object)}方法,否则可能会引起内存泄露
 *
 * @author HansChen
 */
public class EventBus extends RunnerCallback.Stub {

    private volatile static EventBus sEventBus;

    private EventBus() {
    }

    public static EventBus getInstance() {
        if (sEventBus == null) {
            synchronized (EventBus.class) {
                if (sEventBus == null) {
                    sEventBus = new EventBus();
                }
            }
        }
        return sEventBus;
    }

    private Map<Object, OnStepCallback> mStepCallbacks = new HashMap<>();

    public void registerStepCallback(Object subscriber, OnStepCallback callback) {
        if (subscriber != null) {
            if (!mStepCallbacks.containsKey(subscriber)) {
                mStepCallbacks.put(subscriber, callback);
            } else {
                throw new RuntimeException("Subscriber " + subscriber.getClass() + " already registered");
            }
        }
    }

    public void unregisterCallback(Object subscriber) {
        mStepCallbacks.remove(subscriber);
    }

    @Override
    public void onStepUpdate(final long count) throws RemoteException {
        new IterableDispatcher<OnStepCallback>(mStepCallbacks.values()) {
            @Override
            protected void execute(OnStepCallback callback) {
                callback.onStepUpdate(count);
            }
        }.dispatch();
    }
}
