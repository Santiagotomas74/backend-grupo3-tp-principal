package com.blackmesaresearch.hytrac.parser;

import java.util.Map;

public interface DocumentParser {
    Map<String, Object> parse(String rawText);
}
