package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@SuppressWarnings("unused")
public class V16__Insert_Initial_Mind_Reader_Data extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DatabaseMigrationHelper.batchInsertIntoTable(
                context.getConnection(),
                "MIND_READER",
                "TEXT",
                "('What is my favourite colour?')",
                "('What colour are my eyes?')",
                "('Can you guess the number I''m thinking of (1-10)?')",
                "('If I had to pick a superpower, what would it be?')",
                "('What do you think motivates me the most?')",
                "('If I could change one thing about my past, what would it be?')",
                "('Am I more of an introvert or extrovert?')",
                "('Which trait do I value most in friends?')",
                "('What is my favourite food?')",
                "('What''s my go-to comfort drink?')",
                "('Which season do I prefer?')",
                "('What colour would I paint my room?')",
                "('What''s my preferred exercise or physical activity?')",
                "('What is my favourite movie?')",
                "('What is my favourite game?')",
                "('Who is my favourite sports team?')",
                "('What is my favourite song?')",
                "('Am I right or left handed?')",
                "('What was my childhood dream?')");

        DatabaseMigrationHelper.batchInsertIntoTable(
                context.getConnection(),
                "MIND_READER_CATEGORY",
                "CATEGORY_CODE, MIND_READER_ID",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite colour?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite colour?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'What colour are my eyes?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What colour are my eyes?'))",
                "('PHYSICAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What colour are my eyes?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Can you guess the number I''m thinking of (1-10)?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Can you guess the number I''m thinking of (1-10)?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I had to pick a superpower, what would it be?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I had to pick a superpower, what would it be?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What do you think motivates me the most?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'What do you think motivates me the most?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I could change one thing about my past, what would it be?'))",
                "('MEMORIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I could change one thing about my past, what would it be?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more of an introvert or extrovert?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more of an introvert or extrovert?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Which trait do I value most in friends?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Which trait do I value most in friends?'))",
                "('FOOD_DRINK', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite food?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite food?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What''s my go-to comfort drink?'))",
                "('FOOD_DRINK', (SELECT ID FROM MIND_READER WHERE TEXT = 'What''s my go-to comfort drink?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Which season do I prefer?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Which season do I prefer?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'What colour would I paint my room?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What colour would I paint my room?'))",
                "('SPORTS', (SELECT ID FROM MIND_READER WHERE TEXT = 'What''s my preferred exercise or physical activity?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What''s my preferred exercise or physical activity?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite movie?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite movie?'))",
                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite movie?'))",
                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite game?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite game?'))",
                "('SPORTS', (SELECT ID FROM MIND_READER WHERE TEXT = 'Who is my favourite sports team?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Who is my favourite sports team?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite song?'))",
                "('FAVOURITES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What is my favourite song?'))",
                "('PHYSICAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I right or left handed?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I right or left handed?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'What was my childhood dream?'))",
                "('MEMORIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'What was my childhood dream?'))",
                "('CHILDHOOD', (SELECT ID FROM MIND_READER WHERE TEXT = 'What was my childhood dream?'))");

        DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "MIND_READER", "ID");
        DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "MIND_READER_CATEGORY", "ID");
    }
}
