package com.example.fortbyte_conglomerate.utils.observer;

import com.example.fortbyte_conglomerate.utils.events.Event;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
