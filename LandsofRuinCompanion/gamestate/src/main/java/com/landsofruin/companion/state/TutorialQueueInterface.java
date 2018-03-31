package com.landsofruin.companion.state;

import com.google.j2objc.annotations.ObjectiveCName;

/**
 * Created by juhani on 13/01/15.
 */
@ObjectiveCName("TutorialQueueInterface")
public interface TutorialQueueInterface {

    String popTutorialQueue();

    void addTutorialToQueue(String tutorial);

    void addTutorialToQueueDelayed(String tutorial, long delay);

}
