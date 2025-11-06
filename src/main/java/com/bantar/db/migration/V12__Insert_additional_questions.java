package com.bantar.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings("unused")
public class V12__Insert_additional_questions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        String insertQuestionSql = "INSERT INTO QUESTION (TEXT) VALUES (?)";
        String insertCategorySql = "INSERT INTO QUESTION_CATEGORY (QUESTION_ID, CATEGORY_CODE) VALUES (?, ?)";
        String selectQuestionSql = "SELECT ID FROM QUESTION WHERE TEXT = ?";
        String selectCategorySql = "SELECT COUNT(*) FROM QUESTION_CATEGORY WHERE QUESTION_ID = ? AND CATEGORY_CODE = ?";

        Map<String, List<String>> categoryToQuestions = buildQuestions();

        try (PreparedStatement selectQuestionStmt = connection.prepareStatement(selectQuestionSql);
             PreparedStatement insertQuestionStmt = connection.prepareStatement(insertQuestionSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement selectCategoryStmt = connection.prepareStatement(selectCategorySql);
             PreparedStatement insertCategoryStmt = connection.prepareStatement(insertCategorySql)) {

            for (Map.Entry<String, List<String>> e : categoryToQuestions.entrySet()) {
                String category = e.getKey();
                for (String text : e.getValue()) {
                    int questionId = -1;

                    selectQuestionStmt.setString(1, text);
                    try (ResultSet rs = selectQuestionStmt.executeQuery()) {
                        if (rs.next()) {
                            questionId = rs.getInt(1);
                        }
                    }

                    if (questionId == -1) {
                        insertQuestionStmt.setString(1, text);
                        insertQuestionStmt.executeUpdate();
                        try (ResultSet gen = insertQuestionStmt.getGeneratedKeys()) {
                            if (gen.next()) {
                                questionId = gen.getInt(1);
                            }
                        }
                    }

                    if (questionId == -1) {
                        throw new IllegalStateException("Failed to obtain question id for text: " + text);
                    }

                    selectCategoryStmt.setInt(1, questionId);
                    selectCategoryStmt.setString(2, category);
                    try (ResultSet crs = selectCategoryStmt.executeQuery()) {
                        if (crs.next() && crs.getInt(1) == 0) {
                            insertCategoryStmt.setInt(1, questionId);
                            insertCategoryStmt.setString(2, category);
                            insertCategoryStmt.addBatch();
                        }
                    }

                    List<String> extras = overlappingCategoriesFor(category);
                    for (String extra : extras) {
                        selectCategoryStmt.setInt(1, questionId);
                        selectCategoryStmt.setString(2, extra);
                        try (ResultSet ers = selectCategoryStmt.executeQuery()) {
                            if (ers.next() && ers.getInt(1) == 0) {
                                insertCategoryStmt.setInt(1, questionId);
                                insertCategoryStmt.setString(2, extra);
                                insertCategoryStmt.addBatch();
                            }
                        }
                    }
                }
            }

            insertCategoryStmt.executeBatch();
        }
    }

    private Map<String, List<String>> buildQuestions() {
        Map<String, List<String>> m = new HashMap<>();

        m.put("CASUAL", Arrays.asList(
                "What's something small that made your day recently?",
                "Do you prefer mornings or nights and why?",
                "What's your favorite way to relax after a long day?",
                "What's a habit you picked up in the last year?",
                "What's the best meal you've had this month?",
                "Do you prefer coffee or tea?",
                "What's a simple pleasure you enjoy?",
                "What's the last app you deleted and why?",
                "What's a chore you actually don't mind doing?",
                "What's a song you have on repeat right now?"
        ));

        m.put("ROMANTIC", Arrays.asList(
                "What's the most thoughtful thing someone has done for you?",
                "What's your favorite date idea that doesn't cost much?",
                "What quality do you find most attractive?",
                "What's a romantic movie or scene that stuck with you?",
                "What's your idea of a meaningful gift?",
                "How do you like to show someone you care?",
                "What's an unexpected way you maintain romance?",
                "What's a relationship pet peeve?",
                "What's a small tradition you'd like in a relationship?",
                "What's the best relationship advice you've received?"
        ));

        m.put("NSFW", Arrays.asList(
                "What's a boundary you consider important in intimate relationships?",
                "What's a flirtation line that always makes you smile?",
                "What's a date activity that's too bold for a first date?",
                "Have you ever kept a crush secret? Tell the story?",
                "What's an attraction trait that's surprising to others?",
                "What's a daring adventure you'd try with a partner?",
                "What's a red flag you watch for early in dating?",
                "What's a compliment that makes you blush?",
                "What's something you'd never do on a date?",
                "What's a romantic fantasy that's harmless to share?"
        ));

        m.put("BUSINESS", Arrays.asList(
                "What's the best lesson you learned from a past job?",
                "How do you prioritize when everything feels urgent?",
                "What's a skill you want to develop for your career?",
                "What's a workplace culture you thrive in?",
                "How do you handle constructive criticism?",
                "What's a business idea you've always wanted to try?",
                "What's the most important factor when hiring someone?",
                "How do you define professional success?",
                "What's a negotiation tip that worked for you?",
                "What's a habit that improved your productivity?"
        ));

        m.put("HOBBIES", Arrays.asList(
                "What's a hobby you started as a kid and still enjoy?",
                "What's a hobby you've tried but couldn't stick with?",
                "What's a craft or DIY project you're proud of?",
                "What's a hobby you'd like to learn this year?",
                "What's the most time-consuming hobby you have?",
                "What's a community you joined because of a hobby?",
                "What's a hobby that surprises people when you mention it?",
                "What's an item you own because of a hobby?",
                "What's a podcast or channel you follow for your hobby?",
                "What's a small win you had in your hobby recently?"
        ));

        m.put("SPORTS", Arrays.asList(
                "What's the first sport you remember playing?",
                "What's a sporting event you'd love to attend live?",
                "Which athlete inspires you and why?",
                "What's a sport you'd like to learn?",
                "What's a memorable game you watched or played?",
                "Do you prefer team sports or individual sports?",
                "What's a fitness goal you're proud of?",
                "What's a sport that everyone should try once?",
                "What's a skill that transfers from sports to life?",
                "What's your favorite way to stay active?"
        ));

        m.put("PHILOSOPHICAL", Arrays.asList(
                "Do you believe people are fundamentally good or selfish?",
                "Is free will real or an illusion?",
                "What makes a life well-lived to you?",
                "Do you think technology improves human experience overall?",
                "Is happiness the purpose of life?",
                "Can there be objective moral truths?",
                "What responsibility do we have to future generations?",
                "What does 'truth' mean to you?",
                "Is privacy worth sacrificing for safety?",
                "Would you rather know the exact date of your death or never know?"
        ));

        m.put("ART", Arrays.asList(
                "What piece of art has moved you the most?",
                "Which artist would you most like to meet?",
                "How do you judge whether something is good art?",
                "What's a medium you'd like to try creating in?",
                "What's a local gallery or venue you love?",
                "What's a film or book that feels like art to you?",
                "What's an art style you don't understand but respect?",
                "What's a memory associated with a song or painting?",
                "How does art influence the way you see the world?",
                "What's an artwork you'd like to own?"
        ));

        m.put("TRAVEL", Arrays.asList(
                "What's the most surprising food you tried while traveling?",
                "Which city felt most like home and why?",
                "What's a travel mishap that became a good story?",
                "Do you prefer planned trips or spontaneous travel?",
                "What's a place you returned to and why?",
                "What's a cultural custom you found fascinating?",
                "What's the best view you've ever seen?",
                "What's a travel gear you never leave behind?",
                "What's a destination still on your bucket list?",
                "How do you choose where to travel next?"
        ));

        m.put("TELEVISION_MOVIES", Arrays.asList(
                "What's a TV series you binged and loved?",
                "Which movie character do you relate to most?",
                "What's a film that changed your perspective?",
                "What's a guilty-pleasure movie you enjoy?",
                "Who's your favorite director or showrunner?",
                "What's a show you think is underrated?",
                "What's a movie soundtrack you can't stop listening to?",
                "What's a documentary that taught you something new?",
                "What's a TV finale that satisfied you?",
                "What's a genre you rarely watch but want to explore?"
        ));

        m.put("FASHION", Arrays.asList(
                "What's a clothing item that always makes you feel confident?",
                "Do you follow trends or create your own style?",
                "What's a fashion risk you took that paid off?",
                "What's a wardrobe staple you can't live without?",
                "How do you decide what to wear for important events?",
                "What's a brand you admire for ethical or design reasons?",
                "What's a style you once loved but now avoid?",
                "What's your favorite season for fashion and why?",
                "What's a fashion rule you break regularly?",
                "What's an accessory you wish you wore more often?"
        ));

        m.put("LIFESTYLE", Arrays.asList(
                "What's a morning routine that sets you up for the day?",
                "What's a habit you've dropped that improved your life?",
                "How do you balance work and personal time?",
                "What's a financial habit you recommend?",
                "What's your approach to personal wellness?",
                "What's a small change that saved you time?",
                "What's a home improvement you're proud of?",
                "What's a subscription you think is worth it?",
                "How do you unplug when you need to?",
                "What's a long-term goal you're working on?"
        ));

        m.put("FOOD_DRINK", Arrays.asList(
                "What's a comfort food from your childhood?",
                "What's an ingredient you can't stand?",
                "What's a dish you always order at a restaurant?",
                "What's a cuisine you'd like to learn to cook?",
                "What's the most memorable meal you've shared with others?",
                "What's a spice or herb you use all the time?",
                "What's a snack you always keep at home?",
                "What's a food you've tried only once but loved?",
                "What's a cooking hack you swear by?",
                "What's a food trend you think is overrated?"
        ));

        m.put("FUN_HUMOUR", Arrays.asList(
                "What's the funniest misunderstanding you've been part of?",
                "What's a prank that went too far or hilariously wrong?",
                "What's a joke you never get tired of?",
                "What's the most bizarre thing you've laughed at recently?",
                "What's a funny nickname you've had?",
                "What's the worst dad joke you've heard?",
                "What's an embarrassing laugh-inducing moment?",
                "What's a comedy show that always makes you laugh?",
                "What's a silly habit that amuses your friends?",
                "What's the strangest thing you've seen that made you laugh?"
        ));

        m.put("SCIENCE", Arrays.asList(
                "What scientific topic would you like to learn more about?",
                "What's a technology you think will reshape everyday life?",
                "What ethical concern worries you about scientific progress?",
                "Which scientist or inventor do you admire most?",
                "What's a science fiction idea you hope becomes reality?",
                "What's an experiment you would love to run?",
                "What's a myth you'd like to test scientifically?",
                "What's a recent lab discovery that caught your attention?",
                "How do you feel about AI in creative fields?",
                "What's a personal experiment you tried and what did you learn?"
        ));

        m.put("RELATIONSHIPS", Arrays.asList(
                "What's the best way someone has supported you through a hard time?",
                "How do you rebuild trust after it's been broken?",
                "What's a trait you admire most in your friends?",
                "What's a boundary that improved a relationship for you?",
                "How do you keep friendships strong over long distances?",
                "What's one thing you wish people understood about you?",
                "How do you approach difficult conversations with loved ones?",
                "What's a small act that makes you feel valued?",
                "What's a way you show appreciation to people who matter?",
                "What's a lesson relationships have taught you?"
        ));

        m.put("CHILDHOOD", Arrays.asList(
                "What's a game you loved playing as a kid?",
                "What's a childhood tradition you miss?",
                "Who's a childhood hero you admired?",
                "What's the best birthday you remember?",
                "What's a lesson you learned young that stuck with you?",
                "What's a smell that takes you back to your childhood?",
                "What's the most trouble you got into as a kid?",
                "What's a toy you wish you still had?",
                "What's a childhood belief you later found funny?",
                "What's a family story you still retell?"
        ));

        return m;
    }


    private List<String> overlappingCategoriesFor(String category) {
        List<String> extras = new ArrayList<>();
        switch (category) {
            case "SPORTS":
                extras.add("HOBBIES");
                break;
            case "ART":
                extras.add("TELEVISION_MOVIES");
                break;
            case "FOOD_DRINK", "FASHION", "BUSINESS":
                extras.add("LIFESTYLE");
                break;
            case "SCIENCE":
                extras.add("PHILOSOPHICAL");
                break;
            case "ROMANTIC":
                extras.add("RELATIONSHIPS");
                break;
            case "TELEVISION_MOVIES":
                extras.add("ART");
                break;
            default:
                break;
        }
        return extras;
    }
}
