package io.recar.once2go.obdlib.obdcomponents;

public interface ObdProgressListener {

    void stateUpdate(final ObdCommandJob job);

}