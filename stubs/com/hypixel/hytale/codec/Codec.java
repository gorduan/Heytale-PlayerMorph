package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.codecs.simple.StringCodec;
import com.hypixel.hytale.codec.codecs.simple.IntCodec;
import com.hypixel.hytale.codec.codecs.simple.FloatCodec;
import com.hypixel.hytale.codec.codecs.simple.DoubleCodec;
import com.hypixel.hytale.codec.codecs.simple.BooleanCodec;

public interface Codec<T> {
    StringCodec STRING = new StringCodec();
    IntCodec INTEGER = new IntCodec();
    FloatCodec FLOAT = new FloatCodec();
    DoubleCodec DOUBLE = new DoubleCodec();
    BooleanCodec BOOLEAN = new BooleanCodec();
}
