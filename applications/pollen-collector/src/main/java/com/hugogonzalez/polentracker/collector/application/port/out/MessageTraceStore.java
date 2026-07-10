package com.hugogonzalez.polentracker.collector.application.port.out;
import com.hugogonzalez.polentracker.collector.application.model.MessageTrace;
public interface MessageTraceStore { void save(MessageTrace trace); }
