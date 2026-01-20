package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.server.core.ui.builder.CustomUICommand;
import com.hypixel.hytale.server.core.ui.builder.CustomUIEventBinding;

public class CustomPage {
    public CustomPage(String className, boolean initialized, boolean clear,
                      CustomPageLifetime lifetime,
                      CustomUICommand[] commands,
                      CustomUIEventBinding[] events) {}
}
