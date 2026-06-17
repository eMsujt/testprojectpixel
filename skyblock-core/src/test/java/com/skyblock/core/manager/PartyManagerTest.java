package com.skyblock.core.manager;

import com.skyblock.core.manager.PartyManager.Party;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PartyManagerTest {

    private static final PartyManager mgr = PartyManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(PartyManager.getInstance(), PartyManager.getInstance());
    }

    @Test
    void inviteAcceptFlow_JoinsLeaderParty() {
        UUID leader = UUID.randomUUID();
        UUID invitee = UUID.randomUUID();
        mgr.createParty(leader);

        mgr.sendInvite(leader, invitee);
        assertTrue(mgr.hasInvite(leader, invitee));

        Party party = mgr.acceptInvite(invitee);
        assertTrue(party.getAllMembers().contains(invitee));
        assertEquals(leader, party.getLeader());
        assertFalse(mgr.hasInvite(leader, invitee));
    }

    @Test
    void leaveAsLeader_DisbandsParty() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.leaveParty(leader);
        assertFalse(mgr.inParty(leader));
        assertFalse(mgr.inParty(member));
    }

    @Test
    void kick_RemovesMember() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.kickFromParty(member);
        assertFalse(mgr.inParty(member));
        assertTrue(mgr.inParty(leader));
    }

    @Test
    void transferLeadership_PromotesMember() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.transferLeadership(leader, member);
        Party party = mgr.getParty(member);
        assertEquals(member, party.getLeader());
        assertTrue(party.getMembers().contains(leader));
    }

    @Test
    void finderQueue_QueuesAndLeaves() {
        UUID player = UUID.randomUUID();
        String activity = "F7-" + player;

        mgr.queueForFinder(player, activity);
        assertTrue(mgr.isQueued(player));
        assertEquals(1, mgr.getFinderQueue(activity).size());

        assertTrue(mgr.leaveFinderQueue(player));
        assertFalse(mgr.isQueued(player));
        assertTrue(mgr.getFinderQueue(activity).isEmpty());
    }

    @Test
    void queueForFinder_RejectsDoubleQueue() {
        UUID player = UUID.randomUUID();
        String activity = "M3-" + player;
        mgr.queueForFinder(player, activity);
        assertThrows(IllegalStateException.class, () -> mgr.queueForFinder(player, activity));
        mgr.leaveFinderQueue(player);
    }

    @Test
    void formDungeonParty_PullsQueuedPlayersIntoDungeonParty() {
        String activity = "F7-" + UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        mgr.queueForFinder(first, activity);
        mgr.queueForFinder(second, activity);

        Party party = mgr.formDungeonParty(activity);
        assertNotNull(party);
        assertEquals(first, party.getLeader());
        assertTrue(party.getAllMembers().contains(second));
        assertTrue(party.isDungeonParty());
        assertEquals(activity, party.getDungeonFloor());

        // Both players are dequeued from the finder and now in the party.
        assertFalse(mgr.isQueued(first));
        assertFalse(mgr.isQueued(second));
        assertTrue(mgr.getFinderQueue(activity).isEmpty());
    }

    @Test
    void formDungeonParty_EmptyQueueReturnsNull() {
        assertNull(mgr.formDungeonParty("no-such-activity-" + UUID.randomUUID()));
    }

    @Test
    void startDungeon_TagsExistingParty() {
        UUID leader = UUID.randomUUID();
        mgr.createParty(leader);
        assertFalse(mgr.getParty(leader).isDungeonParty());

        mgr.startDungeon(leader, "F5");
        assertTrue(mgr.getParty(leader).isDungeonParty());
        assertEquals("F5", mgr.getParty(leader).getDungeonFloor());
    }
}
