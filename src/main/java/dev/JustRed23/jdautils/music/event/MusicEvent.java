package dev.JustRed23.jdautils.music.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Nullable;

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

	/**
	 * @return the member responsible for this event, if applicable, otherwise null
	 */
	default @Nullable Member member() {
		return null;
	}
}
