package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@SuppressWarnings("unused")
public class V22__Insert_Additional_Mind_Reader_Data extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DatabaseMigrationHelper.batchInsertIntoTable(
                context.getConnection(),
                "MIND_READER",
                "TEXT",
                "('Where would you find me on my day off?')",
                "('If we go out to eat, am I more likely to try something new or order the same thing I like?')",
                "('Am I a morning person or a night owl?')",
                "('Would I rather watch a movie at home or go to the cinema?')",
                "('If I had a day off, would I rather relax or do something productive?')",
                "('Would I rather go on a road trip or fly somewhere far away?')",
                "('Do I prefer sweet snacks or salty snacks?')",
                "('Would I rather read a book or watch a series?')",
                "('Am I more likely to plan everything or go with the flow?')",
                "('Would I rather spend money on experiences or things?')",
                "('If we play a board game, am I very competitive or just playing for fun?')",
                "('Would I rather go to a big party or a small gathering?')",
                "('If I start a hobby, am I likely to stick with it or move on quickly?')",
                "('Would I rather learn a musical instrument or a new language?')",
                "('If I\'m on holiday, do I prefer relaxing or exploring all day?')",
                "('Would I rather cook a meal myself or order takeaway?')",
                "('Do I prefer working alone or with a group?')",
                "('Am I more likely to arrive early or right on time?')",
                "('Do I prefer hot weather or cold weather?')",
                "('Do I prefer individual or team sports?')",
                "('Do I prefer short intense workouts or longer relaxed ones?')",
                "('Would I rather spend a weekend in the city or in nature?')",
                "('Would I rather watch live sports or highlights later?')",
                "('Would I rather win a competition or have everyone enjoy the game?')",
                "('Do I prefer structured routines or flexible days?')",
                "('Would I rather spend time improving a skill or trying lots of different things?')",
                "('Would I rather watch a comedy or something thought-provoking?')"
        );

        DatabaseMigrationHelper.batchInsertIntoTable(
                context.getConnection(),
                "MIND_READER_CATEGORY",
                "CATEGORY_CODE, MIND_READER_ID",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Where would you find me on my day off?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Where would you find me on my day off?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Where would you find me on my day off?'))",

                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we go out to eat, am I more likely to try something new or order the same thing I like?'))",
                "('FOOD_DRINK', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we go out to eat, am I more likely to try something new or order the same thing I like?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we go out to eat, am I more likely to try something new or order the same thing I like?'))",

                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I a morning person or a night owl?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I a morning person or a night owl?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I a morning person or a night owl?'))",

                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a movie at home or go to the cinema?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a movie at home or go to the cinema?'))",
                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a movie at home or go to the cinema?'))",

                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I had a day off, would I rather relax or do something productive?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I had a day off, would I rather relax or do something productive?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I had a day off, would I rather relax or do something productive?'))",

                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go on a road trip or fly somewhere far away?'))",
                "('TRAVEL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go on a road trip or fly somewhere far away?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go on a road trip or fly somewhere far away?'))",

                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer sweet snacks or salty snacks?'))",
                "('FOOD_DRINK', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer sweet snacks or salty snacks?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer sweet snacks or salty snacks?'))",

                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather read a book or watch a series?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather read a book or watch a series?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather read a book or watch a series?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to plan everything or go with the flow?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to plan everything or go with the flow?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to plan everything or go with the flow?'))",

                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend money on experiences or things?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend money on experiences or things?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend money on experiences or things?'))",

                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we play a board game, am I very competitive or just playing for fun?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we play a board game, am I very competitive or just playing for fun?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If we play a board game, am I very competitive or just playing for fun?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go to a big party or a small gathering?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go to a big party or a small gathering?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather go to a big party or a small gathering?'))",

                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I start a hobby, am I likely to stick with it or move on quickly?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I start a hobby, am I likely to stick with it or move on quickly?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I start a hobby, am I likely to stick with it or move on quickly?'))",

                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather learn a musical instrument or a new language?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather learn a musical instrument or a new language?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather learn a musical instrument or a new language?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I\'m on holiday, do I prefer relaxing or exploring all day?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I\'m on holiday, do I prefer relaxing or exploring all day?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'If I\'m on holiday, do I prefer relaxing or exploring all day?'))",

                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather cook a meal myself or order takeaway?'))",
                "('FOOD_DRINK', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather cook a meal myself or order takeaway?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather cook a meal myself or order takeaway?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer working alone or with a group?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer working alone or with a group?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer working alone or with a group?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to arrive early or right on time?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to arrive early or right on time?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Am I more likely to arrive early or right on time?'))",

                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer hot weather or cold weather?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer hot weather or cold weather?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer hot weather or cold weather?'))",

                "('SPORTS', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer individual or team sports?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer individual or team sports?'))",
                "('PHYSICAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer individual or team sports?'))",

                "('PHYSICAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer short intense workouts or longer relaxed ones?'))",
                "('SPORTS', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer short intense workouts or longer relaxed ones?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer short intense workouts or longer relaxed ones?'))",

                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend a weekend in the city or in nature?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend a weekend in the city or in nature?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend a weekend in the city or in nature?'))",

                "('SPORTS', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch live sports or highlights later?'))",
                "('CASUAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch live sports or highlights later?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch live sports or highlights later?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather win a competition or have everyone enjoy the game?'))",
                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather win a competition or have everyone enjoy the game?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather win a competition or have everyone enjoy the game?'))",

                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer structured routines or flexible days?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer structured routines or flexible days?'))",
                "('GENERAL', (SELECT ID FROM MIND_READER WHERE TEXT = 'Do I prefer structured routines or flexible days?'))",

                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend time improving a skill or trying lots of different things?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend time improving a skill or trying lots of different things?'))",
                "('INSIGHT', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather spend time improving a skill or trying lots of different things?'))",

                "('FUN', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a comedy or something thought-provoking?'))",
                "('PERSONALITY', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a comedy or something thought-provoking?'))",
                "('HOBBIES', (SELECT ID FROM MIND_READER WHERE TEXT = 'Would I rather watch a comedy or something thought-provoking?'))"
        );

        DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "MIND_READER", "ID");
        DatabaseMigrationHelper.setIdentitySequence(context.getConnection(), "MIND_READER_CATEGORY", "ID");
    }
}
