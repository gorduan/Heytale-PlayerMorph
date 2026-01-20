package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.server.core.Message;
import javax.annotation.Nonnull;

public interface CommandSender {
    void sendMessage(@Nonnull Message message);
    boolean hasPermission(@Nonnull String permission);
    @Nonnull String getDisplayName();
}
