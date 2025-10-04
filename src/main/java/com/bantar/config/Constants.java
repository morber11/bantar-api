package com.bantar.config;

public class Constants {
    public static final String DEFAULT_QUESTIONS_ICEBREAKERS_PATH = "static/questions/questions_icebreakers.json";
    public static final String QUESTIONS_ICEBREAKERS_WITH_CATEGORY = "static/questions/migrations/questions_icebreakers_with_category.json";
    public static final String ICEBREAKERS_LLM_PROMPT = "Give me %d icebreakers style response in the form of a simple, neutral question e.g \"What is the last dream you had?\"." +
            " return the response in the from of an easily parsable JSON e.g id: 0, text: \"What is the last dream you had?\"" +
            "do not ask any questions that may be deemed offensive";
    public static final String GEMINI_MODEL = "gemini-2.5-flash";
}
