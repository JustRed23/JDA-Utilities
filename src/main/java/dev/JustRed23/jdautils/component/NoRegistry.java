package dev.JustRed23.jdautils.component;

import dev.JustRed23.jdautils.JDAUtilities;
import org.jetbrains.annotations.ApiStatus;

/**
 * This interface is used to prevent a class from getting created with {@link JDAUtilities#createComponent}
 */
@ApiStatus.Internal
public interface NoRegistry {}
