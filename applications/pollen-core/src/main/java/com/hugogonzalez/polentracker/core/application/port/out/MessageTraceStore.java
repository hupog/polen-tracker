package com.hugogonzalez.polentracker.core.application.port.out;
import com.hugogonzalez.polentracker.core.application.model.MessageTrace;
public interface MessageTraceStore { void save(MessageTrace trace); }
