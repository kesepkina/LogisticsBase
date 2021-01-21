package com.epam.logistics_base.entity;

import com.epam.logistics_base.exception.LogisticsBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class LogisticsBase {

    private static final Logger log = LogManager.getLogger();

    private static LogisticsBase instance;
    private static boolean initialized;
    private static final int NUMBER_OF_TERMINALS = 5;
    private final Semaphore semaphore = new Semaphore(NUMBER_OF_TERMINALS, true);
    private static final List<Terminal> terminals = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();

    private LogisticsBase() {
        for (int i = 1; i <= NUMBER_OF_TERMINALS; i++) {
            terminals.add(new Terminal(i));
        }
    }

    public static LogisticsBase getInstance() {
        if (!initialized) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new LogisticsBase();
                }
                initialized = true;
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Optional<Terminal> takeTruck(Truck truck) throws LogisticsBaseException {
        Optional<Terminal> assignedTerminal = Optional.empty();
        try {
            log.info("Truck #{} with priority {} got in line", truck.getTruckId(), truck.getPriority());
            semaphore.acquire();
            try {
                lock.lock();
                int i = 0;
                while (i < NUMBER_OF_TERMINALS) {
                    Terminal terminal = terminals.get(i);
                    if (!terminal.isBusy()) {
                        terminal.setBusy(true);
                        log.info("Terminal #{} took truck #{}", terminal.getTerminalId(), truck.getTruckId());
                        assignedTerminal = Optional.of(terminal);
                        break;
                    }
                    i++;
                }
            } finally {
                lock.unlock();
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
