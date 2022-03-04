package com.strands.interviews.eventsystem;

import com.strands.interviews.eventsystem.events.SimpleEvent;
import com.strands.interviews.eventsystem.events.SubEvent;
import com.strands.interviews.eventsystem.impl.DefaultEventManager;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;

public class DefaultEventManagerTest
{
    private EventManager eventManager = new DefaultEventManager();

    @Test
    public void testPublishNullEvent()
    {
        eventManager.publishEvent(null);
    }

    @Test
    public void testRegisterListenerAndPublishEvent()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.publishEvent(new SimpleEvent(this));
        assertTrue(eventListenerMock.isCalled());
    }

    @Test
    public void testListenerWithoutMatchingEventClass()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SubEvent.class});
        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.publishEvent(new SimpleEvent(this));
        assertFalse(eventListenerMock.isCalled());
    }

    @Test
    public void testUnregisterListener()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMock2 = new EventListenerMock(new Class[]{SimpleEvent.class});

        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.registerListener("another.key", eventListenerMock2);
        eventManager.unregisterListener("some.key");

        eventManager.publishEvent(new SimpleEvent(this));
        assertFalse(eventListenerMock.isCalled());
        assertTrue(eventListenerMock2.isCalled());
    }


    /**
     * Check that registering and unregistering listeners behaves properly.
     */
    @Test
    public void testRemoveNonexistentListener()
    {
        DefaultEventManager dem = (DefaultEventManager)eventManager;
        assertEquals(0, dem.getListeners().size());
        eventManager.registerListener("some.key", new EventListenerMock(new Class[]{SimpleEvent.class}));
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("this.key.is.not.registered");
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("some.key");
        assertEquals(0, dem.getListeners().size());
    }

    /**
     * Registering duplicate keys on different listeners should only fire the most recently added.
     */
    @Test
    public void testDuplicateKeysForListeners()
    {
        EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SimpleEvent.class});
        EventListenerMock eventListenerMock2 = new EventListenerMock(new Class[]{SimpleEvent.class});

        eventManager.registerListener("some.key", eventListenerMock);
        eventManager.registerListener("some.key", eventListenerMock2);

        eventManager.publishEvent(new SimpleEvent(this));

        assertTrue(eventListenerMock2.isCalled());
        assertFalse(eventListenerMock.isCalled());

        eventListenerMock.resetCalled();
        eventListenerMock2.resetCalled();

        eventManager.unregisterListener("some.key");
        eventManager.publishEvent(new SimpleEvent(this));

        assertFalse(eventListenerMock2.isCalled());
        assertFalse(eventListenerMock.isCalled());
    }

    /**
     * Attempting to register a null with a valid key should result in an illegal argument exception
     */
    @Test
    public void testAddValidKeyWithNullListener()
    {
        try
        {
            eventManager.registerListener("bogus.key", null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException ex)
        {
        }
    }
    
    @Test
    public void testPublishEvent() 
    {
    	DefaultEventManager dem = (DefaultEventManager)eventManager;
		EventListenerMock eventListenerMock = new EventListenerMock(new Class[]{SubEvent.class});
        
		dem.registerListener("firstEventKey", eventListenerMock);
		dem.publishEvent(new SubEvent(this));
		Class[] events = eventListenerMock.getHandledEventClasses();
		
		//It is verified that the event has been added correctly.
		assertTrue(eventListenerMock.isCalled());
		
		//The classes of recorded events are compared
		for (int i = 0; i < events.length; i++) {
			assertEquals(events[i].getClass(), (SubEvent.class).getClass());
		}
	}
    
    @Test 
    public void testListenAllEvents()
    {
    	DefaultEventManager dem = (DefaultEventManager)eventManager;
    	 EventListenerMock eventListenerMock1 = new EventListenerMock(new Class[]{SimpleEvent.class});
         EventListenerMock eventListenerMock2 = new EventListenerMock(new Class[]{SimpleEvent.class});
         EventListenerMock eventListenerMock3 = new EventListenerMock(new Class[]{SubEvent.class});
         EventListenerMock eventListenerMock4 = new EventListenerMock(new Class[]{SubEvent.class});
         EventListenerMock eventListenerMockEmpty = new EventListenerMock(null);
    	
     	dem.registerListener("listener1.key", eventListenerMock1);
    	dem.registerListener("listener2.keyy", eventListenerMock2);
    	dem.registerListener("listener3.key", eventListenerMock3);
    	dem.registerListener("listener4.key", eventListenerMock4);
    	dem.registerListener("listenerEmpty.key", eventListenerMockEmpty);
    	
		//It is verified that the event has been added correctly.
    	dem.publishEvent(new SimpleEvent(this));
    	dem.publishEvent(new SubEvent(this));
		assertTrue(eventListenerMockEmpty.isCalled());
    }
}
