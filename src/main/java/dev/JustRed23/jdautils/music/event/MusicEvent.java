package dev.JustRed23.jdautils.music.event;

/**
 * Main event interface, used for all music related events
 */
public interface MusicEvent {
	/**
	 * @return the id of the guild this event is related to
	 */
	long guildId();
}
