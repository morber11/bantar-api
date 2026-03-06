package com.bantar.slop;

public interface SlopProvider {
    String generate(String prompt) throws Exception;
}