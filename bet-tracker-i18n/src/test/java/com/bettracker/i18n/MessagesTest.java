package com.bettracker.i18n;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Tomer Cohen
 */
public class MessagesTest {
    @Test
    public void getMessage() {
        String message = Messages.getMessage("bettracker.application.intro", null);
        assertNotNull(message);
        assertEquals("Bet Tracker Application", message);
    }
}
