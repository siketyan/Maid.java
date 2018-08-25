package com.sikeserver.maid.util;

import java.util.UUID;

class UUIDHelper {
    static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
