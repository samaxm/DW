package com.sx.dw.video.propeller.headset;

public interface IHeadsetPlugListener {
    public void notifyHeadsetPlugged(boolean plugged, Object... extraData);
}
