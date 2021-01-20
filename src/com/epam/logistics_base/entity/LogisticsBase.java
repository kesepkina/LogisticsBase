package com.epam.logistics_base.entity;

import com.epam.logistics_base.exception.LogisticsBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class LogisticsBase {

    private static final Logger log = LogManager.getLogger();

    private static volatile LogisticsBase instance;
    private static final int NUMBER_OF_TERMINALS = 5;
    private final Semaphore semaphore = new Semaphore(NUMBER_OF_TERMINALS, true);
    private static final List<Terminal> terminals = new ArrayList<>();

    private LogisticsBase() {
        for(int i = 1; i <= 5; i++){
            terminals.add(new Terminal(i));
        }
    }

    public static LogisticsBase getInstance() {
        LogisticsBase localInstance = instance;
        if(localInstance == null) {
            synchronized (LogisticsBase.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new LogisticsBase();
                }
            }
        }
        return localInstance;
    }

    public Optional<Terminal> takeTruck(Truck truck) throws LogisticsBaseException {
        Optional<Terminal> assignedTerminal = Optional.empty();
        try{
            log.info("Truck #{} with priority {} got in line", truck.getTruckId(), truck.getPriority());
            semaphore.acquire();
            int i = 0;
            while (i < NUMBER_OF_TERMINALS) {
               Terminal terminal = terminals.get(i);
                if(!terminal.isBusy()) {
                    terminal.setBusy(true);
                    log.info("Terminal #{} took truck #{}", terminal.getTerminalId(), truck.getTruckId());
                    assignedTerminal = Optional.of(terminal);
                    break;
                }
                i++;
            }
        } catch (InterruptedException e) {
            throw new LogisticsBaseException("Current thread " + Thread.currentThread().getName() + " was interrupted", e);
        }
        return assignedTerminal;
    }

    public void releaseTerminal(Truck truck, Terminal terminal) {
        log.info("Truck #{} left terminal #{}", truck.getTruckId(), terminal.getTerminalId());
        terminal.setBusy(false);
        semaphore.release();
    }
}
