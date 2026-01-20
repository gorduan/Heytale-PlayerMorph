package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.server.core.Message;
import javax.annotation.Nonnull;

/**
 * Stub for ParseResult - holds parsing results and failures.
 */
public class ParseResult {
    private boolean failed;

    public ParseResult() {
        this(false);
    }

    public ParseResult(boolean throwExceptionWhenFailed) {
    }

    public void fail(@Nonnull Message reason, Message... otherMessages) {
        this.failed = true;
    }

    public void fail(@Nonnull Message reason) {
        this.fail(reason, (Message[]) null);
    }

    public boolean failed() {
        return this.failed;
    }
}
