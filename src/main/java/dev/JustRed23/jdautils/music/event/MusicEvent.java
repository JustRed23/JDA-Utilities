package dev.JustRed23.jdautils.music.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Main event interface, used for all music related events
 */
public interface MusicEvent {

	/**
	 * @return the client responsible for handling guild interactions
	 */
	JDA client();

	/**
	 * @return the guild this event is related to
	 */
	Guild guild();
}
